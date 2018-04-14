package budikpet.cvut.cz.semestralwork.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ArticlesContentProvider extends ContentProvider {
	private DBHelper dbHelper;

	private static final String AUTHORITY = "budikpet.cvut.cz.semestralWork";
	private static final String BASE_PATH = "articles";

	// All defined URIs provided outside
	public static final Uri ARTICLE_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	// URI return codes
	/** All articles. */
	private static final int ARTICLE_LIST = 1;


	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ARTICLE_LIST);
	}

	public ArticlesContentProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsDeleted;
		switch (uriType) {
			case ARTICLE_LIST:
				rowsDeleted = sqlDB.delete(ArticleTable.TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		long id;
		switch (uriType) {
			case ARTICLE_LIST:
				id = sqlDB.insert(ArticleTable.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(uri + "/" + id);
	}

	@Override
	public boolean onCreate() {
		// Init database
		dbHelper = new DBHelper(getContext());

		return false;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Setup queryBuilder according to URI
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case ARTICLE_LIST:
				queryBuilder.setTables(ArticleTable.TABLE_NAME);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {
		return 0;
	}

}
