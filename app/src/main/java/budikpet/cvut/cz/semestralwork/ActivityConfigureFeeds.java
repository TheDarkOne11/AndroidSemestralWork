package budikpet.cvut.cz.semestralwork;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

import java.util.ArrayList;

import budikpet.cvut.cz.semestralwork.data.FeedDataLoader;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedsCursorAdapter;

public class ActivityConfigureFeeds extends AppCompatActivity
		implements FeedDataLoader.TaskCallbacks, LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 5;
	private FeedDataLoader feedDataLoader;
	private ListView listView;
	private FeedsCursorAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_feeds);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {

		}

		// Initialize listView and it's adapter
		listView = findViewById(R.id.feedsListView);
		adapter = new FeedsCursorAdapter(this, null, 0);
		listView.setAdapter(adapter);

		// Start cursor loader
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

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
		inflater.inflate(R.menu.configure_feeds_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.itemAddFeed) :
				// TODO Create functionality
				Log.i("MENU", "Add feed clicked");
				return true;
			case (android.R.id.home) :
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case LOADER_ID:
				return new CursorLoader(this, FeedReaderContentProvider.FEED_URI,
						new String[] {FeedTable.ID, FeedTable.HEADING, FeedTable.URL},
						null, null, null);
			default:
				break;
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
			case LOADER_ID:
				adapter.swapCursor(cursor);
				break;

			default:
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
			case LOADER_ID:
				// Deactivate adapter cursor
				adapter.swapCursor(null);
				break;

			default:
				break;
		}
	}

	@Override
	public void onPreExecute() {
	}

	/**
	 * Gets new loaded feed data, stores it in database.
	 * @param feeds
	 */
	@Override
	public void onPostExecute(ArrayList<SyndFeed> feeds) {
		// Store entries in the database.
		ContentValues cv = new ContentValues();
//		for(Object curr : entries) {
//			SyndEntry entry = (SyndEntry) curr;
//
//			// Set content values
//			cv.put(FeedTable.HEADING, entry.getTitle());
//			cv.put(FeedTable.TEXT, entry.getDescription().getValue());
//			cv.put(FeedTable.URL, entry.getLink());
//			cv.put(FeedTable.TIME_CREATED, entry.getPublishedDate().getTime());
//
//			String author = entry.getAuthor();
//			if(author == null || author.equals("")) {
//				author = "UNKNOWN_AUTHOR";
//			}
//			cv.put(FeedTable.AUTHOR, author);
//
//			// Save it to database
//			getContentResolver().insert(FeedReaderContentProvider.FEED_URI, cv);
//		}
	}

	public void addFeed(View view) {
		// TODO Add feed
		Log.i("ADD_FEED", "AddFeed item clicked");
	}
}
