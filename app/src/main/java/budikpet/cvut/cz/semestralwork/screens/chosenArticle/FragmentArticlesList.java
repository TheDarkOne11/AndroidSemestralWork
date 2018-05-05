package budikpet.cvut.cz.semestralwork.screens.chosenArticle;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.Provider;
import budikpet.cvut.cz.semestralwork.data.SyncService;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.articles.ArticlesCursorAdapter;

public class FragmentArticlesList extends Fragment implements LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 1;
	private ListView listView;
	private ArticlesCursorAdapter adapter;
	private Context activityContext;
	private MenuItem itemRefresh;
	private View actionProgress;
//	private Synchronize synchronize;

	private BroadcastReceiver syncStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int state = intent.getIntExtra(R.id.keyState + "", SyncService.STOPPED);
			switch (state){
				case SyncService.RUNNING:
					itemRefresh.setActionView(R.layout.action_view_progress);
					break;
				case SyncService.STOPPED:
					itemRefresh.setActionView(null);
					break;
			}
		}
	};


	public FragmentArticlesList() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment FragmentArticlesList.
	 */
	public static FragmentArticlesList newInstance() {
		return new FragmentArticlesList();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		// Get progress bar view
		actionProgress = LayoutInflater.from(getActivity()).inflate(R.layout.action_view_progress,
				null);
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(syncStateReceiver, new IntentFilter(SyncService.broadcastFilter));
		super.onResume();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(syncStateReceiver);
		super.onPause();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activityContext = context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityContext = null;
	}

	//<editor-fold desc="Loader&View">
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_articles_list, container, false);

		// Initialize listView and it's adapter
		listView = fragmentView.findViewById(R.id.articlesListView);
		adapter = new ArticlesCursorAdapter(activityContext, null, 0);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				Uri contentUri = ContentUris.withAppendedId(Provider.ARTICLE_URI,
						cursor.getLong(cursor.getColumnIndex(ArticleTable.ID)));

				Intent intent = new Intent(activityContext, ActivityChosenArticle.class);
				intent.setData(contentUri);
				startActivity(intent);
			}
		});

		// Start cursor loader
		getLoaderManager().initLoader(LOADER_ID, null, this);
		return fragmentView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case LOADER_ID:
				return new CursorLoader(getContext(), Provider.ARTICLE_URI,
						new String[]{ArticleTable.ID, ArticleTable.HEADING, ArticleTable.TEXT},
						null, null, ArticleTable.TIME_CREATED + " DESC");
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
	//</editor-fold>

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemRefresh:
//				synchronize = new Synchronize();
//				synchronize.execute();
				Intent intent = new Intent(getContext(), SyncService.class);
				getActivity().startService(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_articles_list_menu, menu);
		itemRefresh = menu.findItem(R.id.itemRefresh);

		// Refreshing
//		if (synchronize != null) {
//			setRefreshing(synchronize.isRunning());
//		} else {
//			setRefreshing(false);
//		}
	}

	private void setRefreshing(boolean isRefreshing) {
		if (itemRefresh == null) {
			return;
		}

		if (isRefreshing) {
			itemRefresh.setActionView(actionProgress);
		} else {
			itemRefresh.setActionView(null);
		}
	}

	/**
	 * Used for getting RSS URLs asynchronously.
	 */
//	private class Synchronize extends AsyncTask<Void, Void, Void> {
//		private boolean running;
//		long numOfDays = 30;
//
//		@Override
//		protected Void doInBackground(Void... voids) {
//			try (Cursor cursor = activityContext.getContentResolver().query(Provider.FEED_URI,
//					new String[]{FeedTable.VALUE}, null, null, null)) {
//				if (cursor == null) {
//					throw new IndexOutOfBoundsException("Problem with cursor");
//				}
//
//				LinkedList<String> urls = new LinkedList<>();
//
//				// Get all URLs from database
//				while (cursor.moveToNext()) {
//					urls.add(cursor.getString(cursor.getColumnIndex(FeedTable.VALUE)));
//				}
//
//				// Update entries
//				for(String curr : urls) {
//					processFeed(curr);
//				}
//				removeOldEntries();
//
//			} catch (IndexOutOfBoundsException | FeedException | IOException e) {
//				Log.e("FEED_UPDATE", "Could not update feed");
//				Log.e("FEED_UPDATE", "Message: " + e.getMessage());
//				e.printStackTrace();
//			}
//
//			// TODO Testing only
//			SystemClock.sleep(5000);
//
//			return null;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			running = true;
//			setRefreshing(true);
//			Log.i("SYNC", "PreExecute");
//		}
//
//		@Override
//		protected void onPostExecute(Void aVoid) {
//			running = false;
//			setRefreshing(false);
//			Log.i("SYNC", "PostExecute");
//		}
//
//		private void processFeed(String url) throws IOException, FeedException {
//			SyndFeedInput input = new SyndFeedInput();
//			VALUE feedUrl = new VALUE(url);
//			SyndFeed feed = input.build(new InputStreamReader(feedUrl.openStream()));
//
//			// Save entries of current feed
//			for (Object curr : feed.getEntries()) {
//				SyndEntry entry = (SyndEntry) curr;
//
//				saveEntry(entry);
//			}
//		}
//
//		/**
//		 * Removes all entries that are older than certain time frame.
//		 */
//		private void removeOldEntries() {
//
//
//			String selection = ArticleTable.TIME_CREATED + " < ?";
//			String[] selectionArgs = {String.valueOf(getTime())};
//			getActivity().getContentResolver().delete(Provider.ARTICLE_URI,
//					selection, selectionArgs);
//		}
//
//		/**
//		 * @return time in millis. Entries older than this time should be deleted.
//		 */
//		private long getTime() {
//			return System.currentTimeMillis() - numOfDays * 24 * 60 * 60 * 1000;
//		}
//
//		/**
//		 * Persists entry information.
//		 *
//		 * @param entry is the article to be persisted.
//		 */
//		private void saveEntry(SyndEntry entry) {
//			// Get content values of current entry
//			ContentValues cv = getContentValues(entry);
//			ContentResolver resolver = getContext().getContentResolver();
//
//			// Check if entry isn`t too old
//			if (entry.getPublishedDate().getTime() < getTime()) {
//				return;
//			}
//
//			// Entries can be identified by their link
//			String selection = ArticleTable.VALUE + "=?";
//			String[] selectionArgs = {entry.getLink()};
//			try (
//					Cursor savedEntry = resolver.query(Provider.ARTICLE_URI, null,
//							selection, selectionArgs, null)
//			) {
//				// Save or update entry
//				if (savedEntry != null && savedEntry.getCount() > 0) {
//					// Entry already exists, update it
//					int updated = resolver.update(Provider.ARTICLE_URI, cv, selection,
//							selectionArgs);
//					if (updated == 0) {
//						throw new IOException("Can't update the entry: " + entry.getLink());
//					}
//				} else {
//					// Insert new entry
//					Uri entryUri = resolver.insert(Provider.ARTICLE_URI, cv);
//					if (entryUri == null) {
//						throw new IOException("Can't save the entry: " + entry.getLink());
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.e("ADD_FEED", "Error occured when adding data from feed: " + e.getMessage());
//			}
//		}
//
//		/**
//		 * Fills ContentValues using entry data.
//		 *
//		 * @param entry data
//		 * @return ContentValues from data.
//		 */
//		private ContentValues getContentValues(@NonNull SyndEntry entry) {
//			ContentValues cv = new ContentValues();
//
//			// Set content values
//			cv.put(ArticleTable.NAME, entry.getTitle());
//			cv.put(ArticleTable.TEXT, entry.getDescription().getValue());
//			cv.put(ArticleTable.VALUE, entry.getLink());
//			cv.put(ArticleTable.TIME_CREATED, entry.getPublishedDate().getTime());
//
//			String author = entry.getAuthor();
//			if (author == null || author.equals("")) {
//				author = "UNKNOWN_AUTHOR";
//			}
//			cv.put(ArticleTable.AUTHOR, author);
//
//			return cv;
//		}
//
//		public boolean isRunning() {
//			return running;
//		}
//	}
}
