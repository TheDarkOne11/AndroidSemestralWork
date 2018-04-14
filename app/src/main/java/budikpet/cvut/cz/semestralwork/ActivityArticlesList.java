package budikpet.cvut.cz.semestralwork;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

import budikpet.cvut.cz.semestralwork.articles.DataStorage;
import budikpet.cvut.cz.semestralwork.data.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.ArticlesContentProvider;
import budikpet.cvut.cz.semestralwork.data.LoaderFragment;

public class ActivityArticlesList extends AppCompatActivity
		implements FragmentArticlesList.InteractionListener, LoaderFragment.TaskCallbacks {
	private LoaderFragment loaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
			String tag = "loaderFragment";

            DataStorage.init();
			FragmentTransaction transaction = fm.beginTransaction()
					.add(R.id.newsListContainer, FragmentArticlesList.newInstance());

			// Add loader fragment if it doesn't exist
			loaderFragment = (LoaderFragment) fm.findFragmentByTag(tag);
			if (loaderFragment == null) {
				loaderFragment = new LoaderFragment();
				transaction.add(loaderFragment, tag);
			}

			transaction.commit();
        }
    }

    @Override
    public void showChosenArticle(View v) {
        // Get stored article through ID that was stored in View
        int articleId = Integer.parseInt((String) v.getTag(R.id.keyChosenArticleId));

        Intent showArticle = new Intent(this, ActivityChosenArticle.class);
        showArticle.putExtra(R.id.keyChosenArticleId + "", articleId);

        startActivity(showArticle);
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
                // TODO Create functionality
                Log.i("MENU", "ConfigureFeeds clicked");
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
				loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=technet");
				loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=zpravodaj");
				loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=hobby");
				loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=autokat");
				loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=bonusweb");
				return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onPreExecute() {

	}

	/**
	 * Gets new loaded feed data, stores it in database.
	 * @param feed
	 */
	@Override
	public void onPostExecute(SyndFeed feed) {
		ContentValues cv = new ContentValues();
		for(Object curr : feed.getEntries()) {
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
			getContentResolver().insert(ArticlesContentProvider.ARTICLE_URI, cv);
		}
	}
}
