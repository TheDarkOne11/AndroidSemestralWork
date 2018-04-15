package budikpet.cvut.cz.semestralwork;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;

public class ActivityChosenArticle extends AppCompatActivity implements FragmentChosenArticle.InteractionListener {
    private int articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_article);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            // Activity started for the first time
            Bundle extras = getIntent().getExtras();
            articleId = extras.getInt(R.id.keyChosenArticleId + "");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.articleContainer, FragmentChosenArticle.newInstance(articleId))
                    .commit();
        } else {
        	articleId = savedInstanceState.getInt(R.id.keyChosenArticleId + "");
		}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(R.id.keyChosenArticleId + "", articleId);
    }

    /**
     * Creates new menu with share button.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chosen_article_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemShareArticle) :
                share();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementation of current article share functionality.
     */
    private void share() {
        Intent ShareIntent = new Intent(Intent.ACTION_SEND);
		try(Cursor cursor = getContentResolver()
				.query(FeedReaderContentProvider.ARTICLE_URI,
						new String[] {ArticleTable.ID, ArticleTable.URL, ArticleTable.HEADING},
						ArticleTable.ID + "=\'" + articleId + "\'", null, null)) {

			if(cursor == null || !cursor.moveToFirst()) {
				throw new IndexOutOfBoundsException("Problem with cursor: " + articleId);
			}

			// Setup intent
			ShareIntent.setType("url/plain");
			ShareIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.shareSubject),
					cursor.getString(cursor.getColumnIndex(ArticleTable.HEADING))));
			ShareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareText),
					cursor.getString(cursor.getColumnIndex(ArticleTable.URL))));
		} catch (IndexOutOfBoundsException e) {
			Log.e("CURSOR_ERROR", e.getMessage());
			e.printStackTrace();
			return;
		}

        startActivity(Intent.createChooser(ShareIntent, getString(R.string.shareIntent)));

    }
}
