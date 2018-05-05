package budikpet.cvut.cz.semestralwork.data.Config;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Petr on 14.04.18.
 * Class used as a template for FeedTable inside database
 */
public class ConfigTable {
	public static final String TABLE_NAME = "ConfigTable";
	public static final String BASE_PATH = "feeds";

	// Table names
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String VALUE = "value";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ ID + " integer primary key autoincrement, "
			+ NAME + " text not null, "
			+ VALUE + " text not null "
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
