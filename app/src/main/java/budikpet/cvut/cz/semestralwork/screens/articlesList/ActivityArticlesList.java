package budikpet.cvut.cz.semestralwork.screens.articlesList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.Provider;
import budikpet.cvut.cz.semestralwork.data.config.Config;
import budikpet.cvut.cz.semestralwork.data.config.ConfigTable;
import budikpet.cvut.cz.semestralwork.data.sync.ScheduleBroadcastReceiver;
import budikpet.cvut.cz.semestralwork.screens.chosenArticle.ActivityChosenArticle;
import budikpet.cvut.cz.semestralwork.screens.chosenArticle.FragmentChosenArticle;
import budikpet.cvut.cz.semestralwork.screens.configureFeeds.ActivityConfigureFeeds;

public class ActivityArticlesList extends AppCompatActivity implements  FragmentArticlesList.CallbacksListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articles_list);

		new UpdateSyncSchedule().execute();
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

	@Override
	public void articleChosen(long entryId) {
		Uri contentUri = ContentUris.withAppendedId(Provider.ARTICLE_URI, entryId);

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			FragmentChosenArticle chosenArticle = new FragmentChosenArticle();
			Bundle bundle = new Bundle();
			bundle.putParcelable(R.id.keyChosenArticleId + "", contentUri);
			chosenArticle.setArguments(bundle);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.articleContainer, chosenArticle)
					.addToBackStack(null)
					.commit();
		} else {
			Intent intent = new Intent(this, ActivityChosenArticle.class);
			intent.setData(contentUri);
			startActivity(intent);
		}
	}

	/**
	 * Updates timer related information.
	 */
	private class UpdateSyncSchedule extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			ContentResolver resolver = getContentResolver();
			ContentValues cv = new ContentValues();

			// Update local copies of config
			String selection = ConfigTable.NAME + " == ?";
			String[] selectionArgs = {ConfigTable.LAST_SYNC_TIME};
			try (
					Cursor cursorConfig = resolver.query(Provider.CONFIG_URI, null,
							selection, selectionArgs, null)
			) {
				// Save or update lastSyncTime
				if (cursorConfig != null && cursorConfig.getCount() > 0) {
					// Update local copy of lastSyncTime
					cursorConfig.moveToNext();
					Config.lastSyncTime = cursorConfig.getLong(cursorConfig.getColumnIndex(ConfigTable.VALUE));
				} else {
					// Insert to db
					cv.put(ConfigTable.VALUE, Config.lastSyncTime);
					cv.put(ConfigTable.NAME, ConfigTable.LAST_SYNC_TIME);
					Uri entryUri = resolver.insert(Provider.CONFIG_URI, cv);
					if (entryUri == null) {
						throw new IOException("Could not insert");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("CONFIG", "Error occured: " + e.getMessage());
			}

			sendBroadcast(new Intent(getApplicationContext(), ScheduleBroadcastReceiver.class));
			return null;
		}
	}

}
