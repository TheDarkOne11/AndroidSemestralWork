package budikpet.cvut.cz.semestralwork.data;

import android.database.sqlite.SQLiteDatabase;

import java.util.GregorianCalendar;

/**
 * Created by Petr on 14.04.18.
 * Class used as a template for ArticleTable inside database
 */
public class ArticleTable {
	public static final String TABLE_NAME = "ArticleTable";

	// Table names
	public static final String ID = "_id";
	public static final String HEADING = "heading";
	public static final String TEXT = "text";
	public static final String AUTHOR = "author";
	public static final String URL = "url";
	public static final String TIME_CREATED = "timeCreated";

	// Save time in millis
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ ID + " integer primary key autoincrement, "
			+ HEADING + " text not null, "
			+ TEXT + " text null, "
			+ AUTHOR + " text null, "
			+ URL + " text not null, "
			+ TIME_CREATED + " integer null "
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

	public static void dropAndCreateTable(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}