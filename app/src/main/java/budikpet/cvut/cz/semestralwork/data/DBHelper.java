package budikpet.cvut.cz.semestralwork.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Petr on 14.04.18.
 */

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "feedReader.db";
	public static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ArticleTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ArticleTable.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ArticleTable.onDowngrade(db, oldVersion, newVersion);
	}
}
