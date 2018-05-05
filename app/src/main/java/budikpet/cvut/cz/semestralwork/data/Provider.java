package budikpet.cvut.cz.semestralwork.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;
import budikpet.cvut.cz.semestralwork.data.config.ConfigTable;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;

public class Provider extends ContentProvider {
	private DBHelper dbHelper;

	private static final String AUTHORITY = "budikpet.cvut.cz.semestralWork";

	// All defined URIs provided outside
	public static final Uri ARTICLE_URI = Uri.parse("content://" + AUTHORITY + "/" + ArticleTable.BASE_PATH);
	public static final Uri FEED_URI = Uri.parse("content://" + AUTHORITY + "/" + FeedTable.BASE_PATH);
	public static final Uri CONFIG_URI = Uri.parse("content://" + AUTHORITY + "/" + ConfigTable.BASE_PATH);

	// URI return codes
	/**
	 * All articles.
	 */
	private static final int ARTICLE_LIST = 1;
	/**
	 * One article
	 */
	private static final int ARTICLE = 2;
	/**
	 * All feeds.
	 */
	private static final int FEED_LIST = 3;
	/**
	 * One feed.
	 */
	private static final int FEED = 4;

	private static final int CONFIG = 5;


	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, ArticleTable.BASE_PATH, ARTICLE_LIST);
		sURIMatcher.addURI(AUTHORITY, ArticleTable.BASE_PATH + "/#", ARTICLE);
		sURIMatcher.addURI(AUTHORITY, FeedTable.BASE_PATH, FEED_LIST);
		sURIMatcher.addURI(AUTHORITY, FeedTable.BASE_PATH + "/#", FEED);
		sURIMatcher.addURI(AUTHORITY, ConfigTable.BASE_PATH, CONFIG);
	}

	SQLiteDatabase db;
	ContentResolver contentResolver;

	public Provider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsDeleted;
		String id;
		switch (uriType) {
			case ARTICLE_LIST:
				rowsDeleted = sqlDB.delete(ArticleTable.TABLE_NAME, selection, selectionArgs);
				break;
			case ARTICLE:
				id = uri.getLastPathSegment();
				rowsDeleted = sqlDB.delete(ArticleTable.TABLE_NAME, ArticleTable.ID + "=" + id, null);
				break;
			case FEED_LIST:
				rowsDeleted = sqlDB.delete(FeedTable.TABLE_NAME, selection, selectionArgs);
				break;
			case FEED:
				id = uri.getLastPathSegment();
				rowsDeleted = sqlDB.delete(FeedTable.TABLE_NAME, FeedTable.ID + "=" + id, null);
				break;
			case CONFIG:
				rowsDeleted = sqlDB.delete(ConfigTable.TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		contentResolver.notifyChange(uri, null);
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
			case FEED_LIST:
				id = sqlDB.insert(FeedTable.TABLE_NAME, null, values);
				break;
			case CONFIG:
				id = sqlDB.insert(ConfigTable.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		contentResolver.notifyChange(uri, null);
		return Uri.parse(uri + "/" + id);
	}

	@Override
	public boolean onCreate() {
		// Init database
		dbHelper = new DBHelper(getContext());
		db = dbHelper.getWritableDatabase();
		contentResolver = getContext().getContentResolver();

		return false;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {

		// Setup queryBuilder according to URI
		int uriType = sURIMatcher.match(uri);
		String table;
		switch (uriType) {
			case ARTICLE_LIST: {
				table = ArticleTable.TABLE_NAME;
				break;
			}
			case ARTICLE: {
				table = ArticleTable.TABLE_NAME;
				String idSelection = ArticleTable.ID + "=" + ContentUris.parseId(uri);
				selection = TextUtils.isEmpty(selection) ? idSelection :
						"(" + selection + ") AND " + idSelection;
				break;
			}
			case FEED_LIST: {
				table = FeedTable.TABLE_NAME;
				break;
			}
			case FEED: {
				table = FeedTable.TABLE_NAME;
				String idSelection = FeedTable.ID + "=" + ContentUris.parseId(uri);
				selection = TextUtils.isEmpty(selection) ? idSelection :
						"(" + selection + ") AND " + idSelection;
				break;
			}
			case CONFIG: {
				table = ConfigTable.TABLE_NAME;
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(table);
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentResolver, uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {
		String table;

		switch (sURIMatcher.match(uri)) {
			case ARTICLE_LIST: {
				table = ArticleTable.TABLE_NAME;
				break;
			}
			case ARTICLE: {
				table = ArticleTable.TABLE_NAME;
				String idSelection = ArticleTable.ID + "=" + uri.getLastPathSegment();
				selection = TextUtils.isEmpty(selection) ? idSelection :
						"(" + selection + ") AND " + idSelection;
				break;
			}
			case FEED_LIST: {
				table = FeedTable.TABLE_NAME;
				break;
			}
			case FEED: {
				table = FeedTable.TABLE_NAME;
				String idSelection = FeedTable.ID + "=" + uri.getLastPathSegment();
				selection = TextUtils.isEmpty(selection) ? idSelection :
						"(" + selection + ") AND " + idSelection;
				break;
			}
			case CONFIG: {
				table = ConfigTable.TABLE_NAME;
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		long updatedRows = db.update(table, values, selection, selectionArgs);
		if (updatedRows > 0) {
			contentResolver.notifyChange(uri, null);
			return (int) updatedRows;
		} else {
			return 0;
		}
	}

}
