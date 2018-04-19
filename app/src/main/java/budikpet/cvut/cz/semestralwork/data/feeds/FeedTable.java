package budikpet.cvut.cz.semestralwork.data.feeds;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Petr on 14.04.18.
 * Class used as a template for FeedTable inside database
 */
public class FeedTable {
	public static final String TABLE_NAME = "FeedTable";
	public static final String BASE_PATH = "feeds";

	// Table names
	public static final String ID = "_id";
	public static final String HEADING = "heading";
	public static final String URL = "url";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ ID + " integer primary key autoincrement, "
			+ HEADING + " text not null, "
			+ URL + " text not null "
			+ ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
								 int newVersion) {
		dropAndCreateTable(db);
	}

	public static void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropAndCreateTable(db);
	}

	private static void dropAndCreateTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
