package budikpet.cvut.cz.semestralwork;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import budikpet.cvut.cz.semestralwork.data.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.ArticlesContentProvider;
import budikpet.cvut.cz.semestralwork.data.ArticlesCursorAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentArticlesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentArticlesList extends Fragment implements LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 1;
    private InteractionListener listener;
    private ListView listView;
    private ArticlesCursorAdapter adapter;
    private Context activityContext;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_articles_list, container, false);

        // Initialize listView and it's adapter
		listView = fragmentView.findViewById(R.id.articlesListView);
		adapter = new ArticlesCursorAdapter(activityContext, null, 0);
		listView.setAdapter(adapter);

		// Start cursor loader
		getLoaderManager().initLoader(LOADER_ID, null, this);
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            listener = (InteractionListener) context;
			activityContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        activityContext = null;
    }

    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case LOADER_ID:
				return new CursorLoader(getContext(), ArticlesContentProvider.ARTICLE_URI,
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


	/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface InteractionListener {
        void showChosenArticle(View v);
    }
}
