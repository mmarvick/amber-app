package marvick.play.awesome2;

import java.io.IOException;

import marvick.play.awesome2.CatDatabaseContract.CatEntry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class Cat {
	public static final int GENERATE_NEW = 0;
	public static final int LOAD_CURRENT = 1;
	public static final int LOAD_DEFAULT = 2;
	
	private long id;
	private String name;
	private boolean bad;

	
	private Cat (long id, String name, boolean bad) {
    	this.id = id;
    	this.name = name;
    	this.bad = bad;
	}
	
	private Cat (Context context, MainActivity mainActivity) {
		//TODO: This is hacky! Fix this later!
		this.id = -1;
		this.name = "WAITING FOR GENERATION";
		this.bad = true;
		new GetOnlineCat().execute(context, mainActivity);
		
	}
	
	public long getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getBad() {
		return bad;
	}
	
	
	public static Cat loadCatOnStart(Context context) {
		//TODO: rewrite to only do one query on the db
		CatDbHelper dbHelper = new CatDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM CATS", null);
		
		if (cur != null) {
			cur.moveToFirst();
			if (cur.getInt(0) == 0)
				return loadDefault(context, dbHelper);
			else {
				Cursor first = db.rawQuery("SELECT MIN(" + CatEntry._ID + ") FROM CATS", null);
				first.moveToFirst();
				int rowID = first.getInt(0);
				return loadCatByID(dbHelper, rowID);
			}
				
		}
		return loadDefault(context, dbHelper);
	}	
	
	public static Cat loadCatAndDelete(Context context, long id) {
		//TODO: rewrite to only do one query on the db
		CatDbHelper dbHelper = new CatDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(CatEntry.TABLE_NAME, "_ID = " + id, null);
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM CATS", null);
		
		if (cur != null) {
			cur.moveToFirst();
			if (cur.getInt(0) == 0)
				return loadDefault(context, dbHelper);
			else {
				Cursor cur2 = db.rawQuery("SELECT COUNT(*) FROM CATS WHERE " + CatEntry._ID + "<" + id, null);
				if (cur2 != null) {
					cur2.moveToFirst();
					if (cur2.getInt(0) == 0) {
						Cursor first = db.rawQuery("SELECT MIN(" + CatEntry._ID + ") FROM CATS", null);
						first.moveToFirst();
						int rowID = first.getInt(0);
						return loadCatByID(dbHelper, rowID);
					} else {
						Cursor first = db.rawQuery("SELECT MAX(" + CatEntry._ID + ") FROM CATS WHERE " + CatEntry._ID + "<" + id, null);
						first.moveToFirst();
						int rowID = first.getInt(0);
						return loadCatByID(dbHelper, rowID);						
					}
				}
			}
				
		}
		
		return loadCatOnStart(context);
	}
	
	public static Cat loadCatByID(Context context, int catNumber) {
		CatDbHelper dbHelper = new CatDbHelper(context);
		return loadCatByID(dbHelper, catNumber);
	}
	
	private static Cat loadCatByID(CatDbHelper dbHelper, int id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String getInfo = "SELECT " + CatEntry.COLUMN_NAME_NAME + "," + CatEntry.COLUMN_NAME_BAD +
				" FROM " + CatEntry.TABLE_NAME + " WHERE _ID = " + id;
		Cursor c = db.rawQuery(getInfo, null);
		c.moveToFirst();
		
		String name = c.getString(0);
		int badInt = c.getInt(1);
		
		boolean bad = true;
		if (badInt == 0)
			bad = false;
		
		return new Cat(id, name, bad);
	}
	
	public static Cat newCat(Context context, MainActivity mainActivity) {
		Cat cat = new Cat(context, mainActivity);
		
		return cat;
	}
	
	
	private static Cat loadDefault(Context context, CatDbHelper dbHelper) {
		String name = "Amber";
		boolean bad = true;
		return saveCat(context, dbHelper, true, name, bad);
	}
	
	private static Cat saveCat(Context context, CatDbHelper dbHelper, boolean noise, String name, boolean bad) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CatEntry.COLUMN_NAME_NAME, name);
		values.put(CatEntry.COLUMN_NAME_BAD, bad);
		
		long id = db.insert(CatEntry.TABLE_NAME, null, values);
		
		if (noise) {
			MediaPlayer meow = null;
			if (bad) 
				meow = MediaPlayer.create(context, R.raw.bad_meow);
			else
				meow = MediaPlayer.create(context, R.raw.good_meow);
			meow.start();
		}
		
		return new Cat(id, name, bad);
	}		
	
	
	private class GetOnlineCat extends AsyncTask<Object, Void, HttpResponse> {
        private Context context;
        private MainActivity mainActivity;
        
        @Override
        protected HttpResponse doInBackground(Object... params) {
        	context = (Context) params[0];
        	mainActivity = (MainActivity) params[1];
        	
            HttpGet request = new HttpGet("http://www.mmarvick.com/amber_app/new_cat.php");
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            try {
                return client.execute(request);
            } catch (IOException e) {
	        	//TODO INSERT AN ALERT HERE
	            
                return null;
            } finally {
            client.close();
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null) {
            	CatDbHelper dbHelper = new CatDbHelper(context);
            	
        	    HttpEntity entity = result.getEntity();
        	    String textResult = null;
				try {
					textResult = EntityUtils.toString(entity);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
            	
            	JSONObject jObject = null;
            	
				try {
					jObject = new JSONObject(textResult);
					name = jObject.getString("name");
					bad = jObject.getBoolean("bad");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				saveCat(context, dbHelper, true, name, bad);
				mainActivity.onInit();
            }
        }
	}
	

}
