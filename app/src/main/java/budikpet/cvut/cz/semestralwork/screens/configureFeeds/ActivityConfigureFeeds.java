package budikpet.cvut.cz.semestralwork.screens.configureFeeds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.Provider;
import budikpet.cvut.cz.semestralwork.data.database.FeedTable;
import budikpet.cvut.cz.semestralwork.data.sync.SyncService;

public class ActivityConfigureFeeds extends AppCompatActivity
		implements LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 5;
	private ListView listView;
	private FeedsCursorAdapter adapter;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(R.id.keyState + "", SyncService.FEED_OK);
			switch (state) {
				case SyncService.FEED_OK:
					Toast.makeText(getApplicationContext(), R.string.DialogFeedAdded, Toast.LENGTH_SHORT).show();
					break;
				case SyncService.FEED_NOT_OK:
					Toast.makeText(getApplicationContext(), R.string.DialogFeedWrong, Toast.LENGTH_SHORT).show();
					break;
				case SyncService.FEED_DUPLICATE:
					Toast.makeText(getApplicationContext(), R.string.DialogFeedDuplicate, Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	@Override
	protected void onResume() {
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(receiver, new IntentFilter(SyncService.feedBroadcastFilter));
		super.onResume();
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onPause();
	}

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
		inflater.inflate(R.menu.configure_feeds_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.itemAddFeed):
				// Starts new addFeed dialog
				DialogAddFeed dialogAddFeed = new DialogAddFeed();
				dialogAddFeed.show(getSupportFragmentManager(), "DialogAddFeed");
				return true;
			case (android.R.id.home):
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case LOADER_ID:
				return new CursorLoader(this, Provider.FEED_URI,
						new String[]{FeedTable.ID, FeedTable.HEADING, FeedTable.URL},
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

	public void removeFeed(View view) {
		// Starts new dialog
		final int feedId = (int) view.getTag(R.id.keyFeedId);

		Bundle args = new Bundle();
		args.putLong(R.id.keyFeedId + "", feedId);

		DialogRemoveFeed dialogRemoveFeed = new DialogRemoveFeed();
		dialogRemoveFeed.setArguments(args);
		dialogRemoveFeed.show(getSupportFragmentManager(), "RemoveFeed");
	}
}
