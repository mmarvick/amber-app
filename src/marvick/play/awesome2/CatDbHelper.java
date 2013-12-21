package marvick.play.awesome2;

import marvick.play.awesome2.CatDatabaseContract.CatEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CatDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "CatDatabase.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + CatEntry.TABLE_NAME + " (" +
					CatEntry._ID + " INTEGER PRIMARY KEY," +
					CatEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
					CatEntry.COLUMN_NAME_BAD + TEXT_TYPE + " )";
	
	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + CatEntry.TABLE_NAME;

	public CatDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Improve this if the database does get updated
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

}
