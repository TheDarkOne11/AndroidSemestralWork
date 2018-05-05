package budikpet.cvut.cz.semestralwork.screens.chosenArticle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.screens.articlesList.FragmentChosenArticle;

public class ActivityChosenArticle extends AppCompatActivity {
	private int articleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chosen_article);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Activity started for the first time
			getSupportFragmentManager().beginTransaction()
					.add(R.id.articleContainer, createArticleDetailFragment())
					.commit();
		}
	}

	private Fragment createArticleDetailFragment() {
		Bundle args = new Bundle();
		args.putParcelable(R.id.keyChosenArticleId + "", getIntent().getData());
		return Fragment.instantiate(this, FragmentChosenArticle.class.getName(), args);
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
		inflater.inflate(R.menu.chosen_article_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
