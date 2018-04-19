package budikpet.cvut.cz.semestralwork;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import budikpet.cvut.cz.semestralwork.data.FeedDataLoader;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.articles.ArticlesCursorAdapter;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;

public class FragmentArticlesList extends Fragment implements LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 1;
    private ListView listView;
    private ArticlesCursorAdapter adapter;
    private Context activityContext;
    private FeedDataLoader feedDataLoader;

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

		// Add loader fragment if it doesn't exist
		FragmentManager fm = getActivity().getSupportFragmentManager();
		String tag = "feedDataLoader";
		feedDataLoader = (FeedDataLoader) fm.findFragmentByTag(tag);
		if (feedDataLoader == null) {
			feedDataLoader = new FeedDataLoader();
			fm.beginTransaction().add(feedDataLoader, tag).commit();
		}
	}

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
				Uri contentUri = ContentUris.withAppendedId(FeedReaderContentProvider.ARTICLE_URI,
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
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
    }

    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case LOADER_ID:
				return new CursorLoader(getContext(), FeedReaderContentProvider.ARTICLE_URI,
						new String[] {ArticleTable.ID, ArticleTable.HEADING, ArticleTable.TEXT},
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

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemSyncIcon:
				new Synchronize().execute();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Used for getting RSS URLs asynchronously.
	 */
	private class Synchronize extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... voids) {
			String[] urls;
			try(Cursor cursor = activityContext.getContentResolver().query(FeedReaderContentProvider.FEED_URI,
					new String[]{FeedTable.URL}, null, null, null)) {
				if(cursor == null) {
					throw new IndexOutOfBoundsException("Problem with cursor");
				}

				urls = new String[cursor.getCount()];
				int counter = 0;

				// Get all URLs
				while(cursor.moveToNext()) {
					urls[counter] = cursor.getString(cursor.getColumnIndex(FeedTable.URL));
					counter++;
				}

				return urls;

			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String[] strings) {
			super.onPostExecute(strings);

			// Find feeds
			feedDataLoader.execute(strings);
		}
	}
}
