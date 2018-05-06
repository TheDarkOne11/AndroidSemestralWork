package budikpet.cvut.cz.semestralwork.screens.chosenArticle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;

public class FragmentChosenArticle extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private final int LOADER_ID = 2;
	private Context activityContext;
	private Cursor data;

	private TextView heading;
	private TextView subheading;
	private TextView link;
	private TextView mainText;

	public FragmentChosenArticle() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		heading = view.findViewById(R.id.heading);
		subheading = view.findViewById(R.id.subheading);
		link = view.findViewById(R.id.fullArticleLink);
		mainText = view.findViewById(R.id.mainText);

		mainText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_chosen_article, container, false);
	}

	public void updateViewsWithContent(Cursor cursor) {
		long timeCreated = cursor.getLong(cursor.getColumnIndex(ArticleTable.TIME_CREATED));
		String author = cursor.getString(cursor.getColumnIndex(ArticleTable.AUTHOR));
		final String url = cursor.getString(cursor.getColumnIndex(ArticleTable.URL));

		// Build article from components
		heading.setText(cursor.getString(cursor.getColumnIndex(ArticleTable.HEADING)));
		subheading.setText(getSubheading(activityContext, timeCreated, author));
		link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				startActivity(intent);
			}
		});

		String text = cursor.getString(cursor.getColumnIndex(ArticleTable.TEXT));
		mainText.setText(Html.fromHtml(text));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), getArguments().<Uri>getParcelable(R.id.keyChosenArticleId + ""),
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
		this.data = data;
		if (data != null && this.data.moveToFirst()) {
			updateViewsWithContent(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Do nothing.
	}

	/**
	 * String of the subheading changes according to how long ago was the article published.
	 * This method creates this string.
	 *
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
				if (today.get(Calendar.DAY_OF_YEAR) == articleDate.get(Calendar.DAY_OF_YEAR)) {
					if (today.get(Calendar.HOUR_OF_DAY) == articleDate.get(Calendar.HOUR_OF_DAY)) {
						// Same hour
						calendarString = String.format(res.getString(R.string.calendar_lessThanHour),
								today.get(Calendar.MINUTE) - articleDate.get(Calendar.MINUTE));
					} else {
						// Same day
						calendarString = String.format(res.getString(R.string.calendar_lessThanDay),
								today.get(Calendar.HOUR_OF_DAY) - articleDate.get(Calendar.HOUR_OF_DAY));
					}
				} else if (today.get(Calendar.DAY_OF_YEAR) - 1 == articleDate.get(Calendar.DAY_OF_YEAR)) {
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
		activityContext = context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityContext = null;
	}

	/**
	 * Creates new menu with share button.
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.chosen_article_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.itemShareArticle):
				share();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Implementation of current article share functionality.
	 */
	private void share() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);

		// Setup intent
		String url = data.getString(data.getColumnIndex(ArticleTable.URL));
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.shareSubject),
				heading.getText()));
		shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareText),
				url));

		startActivity(Intent.createChooser(shareIntent, getString(R.string.shareIntent)));
	}
}
