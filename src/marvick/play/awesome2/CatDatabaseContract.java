package marvick.play.awesome2;

import android.provider.BaseColumns;

public class CatDatabaseContract {
	public CatDatabaseContract() {};
	
	public static abstract class CatEntry implements BaseColumns{
		public static final String TABLE_NAME = "cats";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_BAD = "bad";
	}
}
