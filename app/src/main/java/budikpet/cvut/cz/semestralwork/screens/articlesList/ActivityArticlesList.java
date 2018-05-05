package budikpet.cvut.cz.semestralwork.screens.articlesList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.ScheduleBroadcastReceiver;
import budikpet.cvut.cz.semestralwork.screens.chosenArticle.FragmentArticlesList;
import budikpet.cvut.cz.semestralwork.screens.configureFeeds.ActivityConfigureFeeds;

public class ActivityArticlesList extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articles_list);
		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {
			fm.beginTransaction().add(R.id.newsListContainer, FragmentArticlesList.newInstance()).commit();

			// Update Sync timer
			sendBroadcast(new Intent(this, ScheduleBroadcastReceiver.class));
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
}
