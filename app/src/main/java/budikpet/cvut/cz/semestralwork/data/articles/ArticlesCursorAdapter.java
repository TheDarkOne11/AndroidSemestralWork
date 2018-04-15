package budikpet.cvut.cz.semestralwork.data.articles;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import budikpet.cvut.cz.semestralwork.R;

/**
 * Cursor adapter working with ListView of articles.
 */
public class ArticlesCursorAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private Context context;

	public ArticlesCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater = LayoutInflater.from(context);
		this.context = context;
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
		return inflater.inflate(R.layout.row_article, parent, false);
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
			holder.heading = view.findViewById(R.id.articleHeading);
			holder.text = view.findViewById(R.id.articleText);
			holder.columnHeadingID = cursor.getColumnIndex(ArticleTable.HEADING);
			holder.columnTextID = cursor.getColumnIndex(ArticleTable.TEXT);
			holder.columnIdID = cursor.getColumnIndex(ArticleTable.ID);
			view.setTag(holder);
		}

		// Use ViewHolders references to it's TextViews to give them new data of the current view.
		holder.heading.setText(cursor.getString(holder.columnHeadingID));
		holder.text.setText(cursor.getString(holder.columnTextID));
		view.setTag(R.id.keyChosenArticleId, cursor.getInt(holder.columnIdID));
	}

	private class ViewHolder {
		public TextView heading;
		public TextView text;
		public int columnHeadingID;
		public int columnTextID;
		public int columnIdID;
	}
}
