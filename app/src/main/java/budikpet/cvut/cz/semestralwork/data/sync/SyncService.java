package budikpet.cvut.cz.semestralwork.data.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.Provider;
import budikpet.cvut.cz.semestralwork.data.config.Config;
import budikpet.cvut.cz.semestralwork.data.config.ConfigTable;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;

public class SyncService extends IntentService {
	public static final String broadcastFilter = "SyncServiceBroadcast";
	public static final int RUNNING = 0;
	public static final int STOPPED = 1;

	public SyncService() {
		super("SyncService");
	}

	private void publishState(int state) {
		Intent intent = new Intent(broadcastFilter);
		intent.putExtra(R.id.keyState + "", state);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}

	private void scheduleNewAlarm() {
		ContentResolver resolver = getContentResolver();

		// Store new time to database
		String selection = ConfigTable.NAME + " == ?";
		String[] selectionArgs = {ConfigTable.LAST_SYNC_TIME};
		try (
				Cursor cursorConfig = resolver.query(Provider.CONFIG_URI, null,
						selection, selectionArgs, null)
		) {
			// Update sync time
			if (cursorConfig != null && cursorConfig.getCount() > 0) {
				ContentValues cv = new ContentValues();
				Config.lastSyncTime = System.currentTimeMillis() + Config.syncInterval;
				cv.put(ConfigTable.VALUE, Config.lastSyncTime);

				int updated = resolver.update(Provider.CONFIG_URI, cv, selection,
						selectionArgs);
				if (updated == 0) {
					throw new IOException("Can't update");
				}
			} else {
				throw new IOException("Config does not exist.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("UPDATE_SCHEDULE", "Error occured: " + e.getMessage());
		}

		// New alarm
		sendBroadcast(new Intent(getApplicationContext(), ScheduleBroadcastReceiver.class));
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent == null) {
			return;
		}

		publishState(RUNNING);

		String newFeedUrl = intent.getStringExtra(R.id.keyFeedId + "");
		if(newFeedUrl != null) {
			// Update entries of new feed and feed itself
			updateEntries(newFeedUrl);
		} else {
			// Update all entries
			updateEntries();
			scheduleNewAlarm();
		}

		publishState(STOPPED);

		// TODO Testing only
		SystemClock.sleep(5000);
	}

	/**
	 * Update all entries of the feed symbolized by the given URL.
	 * @param url
	 */
	private void updateEntries(String url) {
		ContentValues cv = new ContentValues();
		try {
			SyndFeedInput input = new SyndFeedInput();
			URL feedUrl = new URL(url);
			SyndFeed feed = input.build(new InputStreamReader(feedUrl.openStream()));

			if(feed.getLink() == null) {
				// URL doesn`t lead to RSS feed
				cv.put(FeedTable.HEADING, getApplicationContext().getResources().getString(R.string.rssNotFound));
				cv.put(FeedTable.URL, url);
				getContentResolver().insert(Provider.FEED_URI, cv);
				return;
			}

			// New feed exists, update it
			cv.put(FeedTable.HEADING, feed.getTitle());
			cv.put(FeedTable.URL, url);
			Uri uri = getContentResolver().insert(Provider.FEED_URI, cv);
			long feedId = ContentUris.parseId(uri);

			// Save entries of current feed
			for (Object curr : feed.getEntries()) {
				SyndEntry entry = (SyndEntry) curr;

				saveEntry(entry, feedId);
			}

		} catch (UnknownHostException e) {
			cv.put(FeedTable.HEADING, getApplicationContext().getResources().getString(R.string.rssNotFound));
			cv.put(FeedTable.URL, url);
			getContentResolver().insert(Provider.FEED_URI, cv);
		} catch (IOException | FeedException e) {
			e.printStackTrace();
			Log.e("CHECK_FEED", e.getMessage());
		}
	}

	/**
	 * Updates all entries of all feeds from database.
	 */
	private void updateEntries() {
		try (
				Cursor cursor = getContentResolver().query(Provider.FEED_URI,
				new String[]{FeedTable.ID, FeedTable.URL}, null, null, null)
		) {
			if (cursor == null) {
				throw new IndexOutOfBoundsException("Problem with cursor");
			}

			LinkedList<String> urls = new LinkedList<>();

			// Get all URLs from database
			while (cursor.moveToNext()) {
				String curr = cursor.getString(cursor.getColumnIndex(FeedTable.URL));
				Long feedId = cursor.getLong(cursor.getColumnIndex(FeedTable.ID));
				processFeed(curr, feedId);
			}

			removeOldEntries();

		} catch (IndexOutOfBoundsException | FeedException | IOException e) {
			Log.e("FEED_UPDATE", "Could not update feed");
			Log.e("FEED_UPDATE", "Message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void processFeed(String url, Long feedId) throws IOException, FeedException {
		SyndFeedInput input = new SyndFeedInput();
		URL feedUrl = new URL(url);
		SyndFeed feed = input.build(new InputStreamReader(feedUrl.openStream()));

		// Save entries of current feed
		for (Object curr : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) curr;

			saveEntry(entry, feedId);
		}

	}

	/**
	 * Removes all entries that are older than certain time frame.
	 */
	private void removeOldEntries() {


		String selection = ArticleTable.TIME_CREATED + " < ?";
		String[] selectionArgs = {String.valueOf(getTime())};
		getContentResolver().delete(Provider.ARTICLE_URI,
				selection, selectionArgs);
	}

	/**
	 * @return time in millis. Entries older than this time should be deleted.
	 */
	private long getTime() {
		return System.currentTimeMillis() - Config.oldestEntryDays * 24 * 60 * 60 * 1000;
	}

	/**
	 * Persists entry information.
	 *
	 * @param entry is the article to be persisted.
	 * @param feedId
	 */
	private void saveEntry(SyndEntry entry, Long feedId) {
		// Get content values of current entry
		ContentValues cv = getContentValues(entry, feedId);
		ContentResolver resolver = getContentResolver();

		// Check if entry isn`t too old
		if (entry.getPublishedDate().getTime() < getTime()) {
			return;
		}

		// Entries can be identified by their link
		String selection = ArticleTable.URL + "=?";
		String[] selectionArgs = {entry.getLink()};
		try (
				Cursor savedEntry = resolver.query(Provider.ARTICLE_URI, null,
						selection, selectionArgs, null)
		) {
			// Save or update entry
			if (savedEntry != null && savedEntry.getCount() > 0) {
				// Entry already exists, update it
				int updated = resolver.update(Provider.ARTICLE_URI, cv, selection,
						selectionArgs);
				if (updated == 0) {
					throw new IOException("Can't update the entry: " + entry.getLink());
				}
			} else {
				// Insert new entry
				Uri entryUri = resolver.insert(Provider.ARTICLE_URI, cv);
				if (entryUri == null) {
					throw new IOException("Can't save the entry: " + entry.getLink());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("ADD_FEED", "Error occured when adding data from feed: " + e.getMessage());
		}
	}

	/**
	 * Fills ContentValues using entry data.
	 *
	 * @param entry data
	 * @param feedId
	 * @return ContentValues from data.
	 */
	private ContentValues getContentValues(@NonNull SyndEntry entry, Long feedId) {
		ContentValues cv = new ContentValues();

		// Set content values
		cv.put(ArticleTable.HEADING, entry.getTitle());
		cv.put(ArticleTable.TEXT, entry.getDescription().getValue());
		cv.put(ArticleTable.URL, entry.getLink());
		cv.put(ArticleTable.TIME_CREATED, entry.getPublishedDate().getTime());
		cv.put(ArticleTable.FEED_ID, feedId);

		String author = entry.getAuthor();
		if (author == null || author.equals("")) {
			author = "UNKNOWN_AUTHOR";
		}
		cv.put(ArticleTable.AUTHOR, author);

		return cv;
	}

}
