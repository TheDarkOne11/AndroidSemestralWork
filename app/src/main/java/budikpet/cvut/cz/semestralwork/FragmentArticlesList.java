package budikpet.cvut.cz.semestralwork;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Map;

import budikpet.cvut.cz.semestralwork.articles.Article;
import budikpet.cvut.cz.semestralwork.articles.DataStorage;
import budikpet.cvut.cz.semestralwork.feeds.FeedHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentArticlesList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentArticlesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentArticlesList extends Fragment {
    private OnFragmentInteractionListener mListener;
    private FeedHandler feedHandler;

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
        FragmentArticlesList fragment = new FragmentArticlesList();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        LinearLayout articlesContainer = fragmentView.findViewById(R.id.articlesContainer);
        feedHandler = new FeedHandler();

        // Create new clickable TextViews for all articles
        final Hashtable<String, Article> articles = DataStorage.getArticles();
        for(Map.Entry<String, Article> entry : articles.entrySet()) {
            // Heading
            TextView heading = new TextView(getContext());
            heading.setId(View.generateViewId());
            heading.setTypeface(null, Typeface.BOLD);
            heading.setText(entry.getValue().getHeading());
            heading.setPadding(8, 4, 8, 4);

            // Text
            TextView text = new TextView(getContext());
            text.setId(View.generateViewId());
            text.setTextSize(12);
            text.setText(entry.getValue().getText());
            text.setEllipsize(TextUtils.TruncateAt.END);
            text.setMaxLines(3);
            text.setPadding(8, 4, 8, 4);

            // Assemble and add article
            LinearLayout currArticleView = new LinearLayout(getContext());
            currArticleView.setTag(R.id.keyChosenArticleId, entry.getKey());
            currArticleView.setOrientation(LinearLayout.VERTICAL);
            currArticleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.showChosenArticle(v);
                }
            });
            currArticleView.addView(heading);
            currArticleView.addView(text);
            articlesContainer.addView(currArticleView);
        }

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        public void showChosenArticle(View v);
    }
}
