package budikpet.cvut.cz.semestralwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import budikpet.cvut.cz.semestralwork.articles.Article;
import budikpet.cvut.cz.semestralwork.articles.DataStorage;

public class ActivityNewsList extends AppCompatActivity implements FragmentArticlesList.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        if (savedInstanceState == null) {
            DataStorage.init();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.newsListContainer, FragmentArticlesList.newInstance())
                    .commit();
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
        }

        return super.onOptionsItemSelected(item);
    }
}
