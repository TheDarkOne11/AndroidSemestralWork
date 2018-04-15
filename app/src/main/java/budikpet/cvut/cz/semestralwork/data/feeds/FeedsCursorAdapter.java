package budikpet.cvut.cz.semestralwork.data.feeds;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import budikpet.cvut.cz.semestralwork.ActivityConfigureFeeds;
import budikpet.cvut.cz.semestralwork.R;

/**
 * Cursor adapter working with ListView of articles.
 */
public class FeedsCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private Context mContext;

	public FeedsCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	/**
	 * Create view of the current row.
	 * @param context
	 * @param cursor
	 * @param parent
	 * @return
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.row_feed, parent, false);
	}

	/**
	 * Fill current row.
	 * @param view
	 * @param context
	 * @param cursor
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		if(holder == null) {
			// Create view holder
			holder = new ViewHolder();
			holder.heading = view.findViewById(R.id.feedHeading);
			holder.url = view.findViewById(R.id.feedUrl);
			holder.columnHeadingID = cursor.getColumnIndex(FeedTable.HEADING);
			holder.columnUrlID = cursor.getColumnIndex(FeedTable.URL);
			holder.columnIdID = cursor.getColumnIndex(FeedTable.ID);
			view.setTag(holder);
		}

		// Use ViewHolders references to it's TextViews to give them new data of the current view.
		holder.heading.setText(cursor.getString(holder.columnHeadingID));
		holder.url.setText(cursor.getString(holder.columnUrlID));
		view.setTag(R.id.keyFeedId, cursor.getInt(holder.columnIdID));
	}

	private class ViewHolder {
		public TextView heading;
		public TextView url;
		public int columnHeadingID;
		public int columnUrlID;
		public int columnIdID;
	}
}
