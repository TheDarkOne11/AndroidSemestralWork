package budikpet.cvut.cz.semestralwork.screens.chosenArticle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import budikpet.cvut.cz.semestralwork.R;

public class ActivityChosenArticle extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chosen_article);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Activity started for the first time
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.articleContainer, createArticleDetailFragment())
					.commit();
		}
	}

	private Fragment createArticleDetailFragment() {
		Bundle args = new Bundle();
		args.putParcelable(R.id.keyChosenArticleId + "", getIntent().getData());
		return Fragment.instantiate(this, FragmentChosenArticle.class.getName(), args);
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
