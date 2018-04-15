package budikpet.cvut.cz.semestralwork;

import android.content.ContentValues;
import android.content.Intent;
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

import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;
import budikpet.cvut.cz.semestralwork.data.FeedDataLoader;

public class ActivityArticlesList extends AppCompatActivity
		implements FragmentArticlesList.InteractionListener, FeedDataLoader.TaskCallbacks {
	private FeedDataLoader feedDataLoader;

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

    @Override
    public void showChosenArticle(View v) {
        // Get stored article through ID that was stored in View
        int articleId = (int) v.getTag(R.id.keyChosenArticleId);

        Intent showArticle = new Intent(this, ActivityChosenArticle.class);
        showArticle.putExtra(R.id.keyChosenArticleId + "", articleId);

        startActivity(showArticle);
    }

    public void goToConfigureFeeds() {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemConfigureFeeds) :
				Log.i("MENU", "ConfigureFeeds clicked");
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
			case R.id.itemSynchronize:
				feedDataLoader.execute("http://servis.idnes.cz/rss.aspx?c=technet",
						"http://servis.idnes.cz/rss.aspx?c=zpravodaj");
//				feedDataLoader.execute("http://servis.idnes.cz/rss.aspx?c=hobby");
//				feedDataLoader.execute("http://servis.idnes.cz/rss.aspx?c=autokat");
//				feedDataLoader.execute("http://servis.idnes.cz/rss.aspx?c=bonusweb");
				return true;
        }

        return super.onOptionsItemSelected(item);
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
	}
}
