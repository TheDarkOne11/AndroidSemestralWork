package budikpet.cvut.cz.semestralwork;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import budikpet.cvut.cz.semestralwork.data.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.ArticlesContentProvider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentChosenArticle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChosenArticle extends Fragment {
    private int articleId;
	private final int LOADER_ID = 2;
	private Context activityContext;
    private InteractionListener listener;

    public FragmentChosenArticle() {
        // Required empty public constructor
    }

    public static FragmentChosenArticle newInstance(int articleId) {
        FragmentChosenArticle fragment = new FragmentChosenArticle();
        Bundle args = new Bundle();
        args.putInt(R.id.keyChosenArticleId + "", articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            articleId = getArguments().getInt(R.id.keyChosenArticleId + "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chosen_article, container, false);

		String[] columns = new String[] {ArticleTable.ID, ArticleTable.HEADING,
				ArticleTable.TEXT, ArticleTable.TIME_CREATED, ArticleTable.AUTHOR, ArticleTable.URL};

        Cursor cursor = activityContext.getContentResolver()
				.query(ArticlesContentProvider.ARTICLE_URI,
						columns, ArticleTable.ID + "=\'" + articleId + "\'", null, null);
        cursor.moveToFirst();

		TextView heading = view.findViewById(R.id.heading);
		TextView subheading = view.findViewById(R.id.subheading);
		TextView mainText = view.findViewById(R.id.mainText);
		TextView link = view.findViewById(R.id.fullArticleLink);

		long timeCreated = cursor.getLong(cursor.getColumnIndex(ArticleTable.TIME_CREATED));
		String author = cursor.getString(cursor.getColumnIndex(ArticleTable.AUTHOR));
		final String url = cursor.getString(cursor.getColumnIndex(ArticleTable.URL));

		// Build article from components
		heading.setText(cursor.getString(cursor.getColumnIndex(ArticleTable.HEADING)));
		subheading.setText(getSubheading(activityContext, timeCreated, author));
		mainText.setText(cursor.getString(cursor.getColumnIndex(ArticleTable.TEXT)));
		link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});

		cursor.close();
        return view;
    }

	/**
	 * String of the subheading changes according to how long ago was the article published.
	 * This method creates this string.
	 * @return subheading string
	 */
	private String getSubheading(Context context, long timeCreated, String author) {
		StringBuilder stringBuilder = new StringBuilder();
		Resources res = context.getResources();
		GregorianCalendar articleDate = new GregorianCalendar();
		articleDate.setTimeInMillis(timeCreated);
		GregorianCalendar today = new GregorianCalendar();

		// Create time string
		String calendarString;
		if (today.get(Calendar.YEAR) == articleDate.get(Calendar.YEAR)) {
			if (today.get(Calendar.DAY_OF_YEAR) - articleDate.get(Calendar.DAY_OF_YEAR) < 7) {
				if(today.get(Calendar.DAY_OF_YEAR) == articleDate.get(Calendar.DAY_OF_YEAR)) {
					if(today.get(Calendar.HOUR_OF_DAY) == articleDate.get(Calendar.HOUR_OF_DAY)) {
						// Same hour
						calendarString = String.format(res.getString(R.string.calendar_lessThanHour),
								today.get(Calendar.MINUTE) - articleDate.get(Calendar.MINUTE));
					} else {
						// Same day
						calendarString = String.format(res.getString(R.string.calendar_lessThanDay),
								today.get(Calendar.HOUR_OF_DAY) - articleDate.get(Calendar.HOUR_OF_DAY));
					}
				} else if(today.get(Calendar.DAY_OF_YEAR) - 1 == articleDate.get(Calendar.DAY_OF_YEAR)) {
					// Yesterday
					calendarString = res.getString(R.string.calendar_yesterday);
				} else {
					// More than 2 days
					calendarString = String.format(res.getString(R.string.calendar_lessThanWeek),
							today.get(Calendar.DAY_OF_YEAR) - articleDate.get(Calendar.DAY_OF_YEAR));
				}
			} else {
				// Longer than 7 days
				calendarString = String.format(res.getString(R.string.calendar_moreThanWeek), articleDate);
			}
		} else {
			// Longer than 7 days
			calendarString = String.format(res.getString(R.string.calendar_moreThanWeek), articleDate);
		}
		String authorString = String.format(res.getString(R.string.authorString), author);
		stringBuilder.append(calendarString)
				.append(" ")
				.append(authorString);

		return stringBuilder.toString();
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

    }
}
