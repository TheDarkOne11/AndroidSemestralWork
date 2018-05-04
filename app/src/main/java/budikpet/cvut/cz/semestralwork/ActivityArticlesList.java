package budikpet.cvut.cz.semestralwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityArticlesList extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articles_list);
		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {
			fm.beginTransaction().add(R.id.newsListContainer, FragmentArticlesList.newInstance()).commit();
		}
	}

	/**
	 * Creates new menu with share button.
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_articles_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.itemConfigureFeeds):
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
		}

		return super.onOptionsItemSelected(item);
	}

//	public void onPostExecute(ArrayList<SyndFeed> feeds) {
//		// Extract all entries
//		ArrayList<SyndEntry> entries = new ArrayList<>();
//		for (SyndFeed currFeed : feeds) {
//			List<SyndEntry> currEntries = currFeed.getEntries();
//			entries.addAll(currEntries);
//		}
//
//		// Sort entries by date
//		Collections.sort(entries, new Comparator<SyndEntry>() {
//			@Override
//			public int compare(SyndEntry o1, SyndEntry o2) {
//				return (int) (o2.getPublishedDate().getTime() - o1.getPublishedDate().getTime());
//			}
//		});
//
//		// Clear database
//		getContentResolver().delete(FeedReaderContentProvider.ARTICLE_URI, null, null);
//
//		// Store entries in the database.
//		ContentValues cv = new ContentValues();
//		for (Object curr : entries) {
//			SyndEntry entry = (SyndEntry) curr;
//
//			// Set content values
//			cv.put(ArticleTable.HEADING, entry.getTitle());
//			cv.put(ArticleTable.TEXT, entry.getDescription().getValue());
//			cv.put(ArticleTable.URL, entry.getLink());
//			cv.put(ArticleTable.TIME_CREATED, entry.getPublishedDate().getTime());
//
//			String author = entry.getAuthor();
//			if (author == null || author.equals("")) {
//				author = "UNKNOWN_AUTHOR";
//			}
//			cv.put(ArticleTable.AUTHOR, author);
//
//			// Save it to database
//			getContentResolver().insert(FeedReaderContentProvider.ARTICLE_URI, cv);
//		}
//
//		setRefreshing(false);
//	}
}
