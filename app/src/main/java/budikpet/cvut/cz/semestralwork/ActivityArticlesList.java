package budikpet.cvut.cz.semestralwork;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import budikpet.cvut.cz.semestralwork.data.FeedDataLoader;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;

public class ActivityArticlesList extends AppCompatActivity
		implements FeedDataLoader.TaskCallbacks {
	private FeedDataLoader feedDataLoader;
	private MenuItem itemRefreshProgress, itemRefreshIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
		FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
			fm.beginTransaction().add(R.id.newsListContainer, FragmentArticlesList.newInstance()).commit();
        }

		// Add loader fragment if it doesn't exist
		String tag = "feedDataLoader";
		feedDataLoader = (FeedDataLoader) fm.findFragmentByTag(tag);
		if (feedDataLoader == null) {
			feedDataLoader = new FeedDataLoader();
			fm.beginTransaction().add(feedDataLoader, tag).commit();
		}
    }

    /**
     * Creates new menu with share button.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_list_menu, menu);
        return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Store instance of the menu item containing progress
		itemRefreshProgress = menu.findItem(R.id.itemSyncProgress);
		itemRefreshIcon = menu.findItem(R.id.itemSyncIcon);

		setRefreshing(feedDataLoader.isRunning());
		return super.onPrepareOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemConfigureFeeds) :
				// Go to activityConfigureFeeds
				Intent configureFeeds = new Intent(this, ActivityConfigureFeeds.class);
				startActivity(configureFeeds);
                return true;
            case R.id.itemPreferences:
                // TODO Create functionality
                Log.i("MENU", "Preferences clicked");
                return true;
            case R.id.itemAbout:
                // TODO Create functionality
                Log.i("MENU", "About clicked");
                return true;
			case R.id.itemSyncIcon:
				synchronizeData();
				return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private void setRefreshing(boolean refreshing) {
		if(itemRefreshIcon == null || itemRefreshProgress == null) {
			return;
		}

		itemRefreshIcon.setVisible(!refreshing);
		itemRefreshProgress.setVisible(refreshing);
	}

    private void synchronizeData() {
		try(Cursor cursor = getContentResolver().query(FeedReaderContentProvider.FEED_URI,
				new String[]{FeedTable.URL}, null, null, null)) {
			if(cursor == null) {
				throw new IndexOutOfBoundsException("Problem with cursor");
			}

			String[] urls = new String[cursor.getCount()];
			int counter = 0;

			// Get all URLs
			while(cursor.moveToNext()) {
				urls[counter] = cursor.getString(cursor.getColumnIndex(FeedTable.URL));
				counter++;
			}

			feedDataLoader.execute(urls);

		} catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPreExecute() {
		setRefreshing(true);
	}

	/**
	 * Gets new loaded feed data, stores it in database.
	 * @param feeds
	 */
	@Override
	public void onPostExecute(ArrayList<SyndFeed> feeds) {
		// Extract all entries
		ArrayList<SyndEntry> entries = new ArrayList<>();
		for(SyndFeed currFeed : feeds) {
			List<SyndEntry> currEntries = currFeed.getEntries();
			entries.addAll(currEntries);
		}

		// Sort entries by date
		Collections.sort(entries, new Comparator<SyndEntry>() {
			@Override
			public int compare(SyndEntry o1, SyndEntry o2) {
				return (int) (o2.getPublishedDate().getTime() - o1.getPublishedDate().getTime());
			}
		});

		// Clear database
		getContentResolver().delete(FeedReaderContentProvider.ARTICLE_URI, null, null);

		// Store entries in the database.
		ContentValues cv = new ContentValues();
		for(Object curr : entries) {
			SyndEntry entry = (SyndEntry) curr;

			// Set content values
			cv.put(ArticleTable.HEADING, entry.getTitle());
			cv.put(ArticleTable.TEXT, entry.getDescription().getValue());
			cv.put(ArticleTable.URL, entry.getLink());
			cv.put(ArticleTable.TIME_CREATED, entry.getPublishedDate().getTime());

			String author = entry.getAuthor();
			if(author == null || author.equals("")) {
				author = "UNKNOWN_AUTHOR";
			}
			cv.put(ArticleTable.AUTHOR, author);

			// Save it to database
			getContentResolver().insert(FeedReaderContentProvider.ARTICLE_URI, cv);
		}

		setRefreshing(false);
	}
}
