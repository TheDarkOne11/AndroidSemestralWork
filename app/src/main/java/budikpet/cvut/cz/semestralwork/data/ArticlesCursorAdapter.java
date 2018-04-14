package budikpet.cvut.cz.semestralwork.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
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
	private LayoutInflater mInflater;
	private Context mContext;

	public ArticlesCursorAdapter(Context context, Cursor c, int flags) {
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
		View view = mInflater.inflate(R.layout.row_article, parent, false);
		return view;
	}

	/**
	 * Fill current row.
	 * @param view
	 * @param context
	 * @param cursor
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView heading = view.findViewById(R.id.rowHeading);
		heading.setText(cursor.getString(cursor.getColumnIndex(ArticleTable.HEADING)));

		TextView text = view.findViewById(R.id.rowText);
		text.setText(cursor.getString(cursor.getColumnIndex(ArticleTable.TEXT)));
	}
}
