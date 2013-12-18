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
	
	private String name;
	private boolean bad;
	private long id;
	private Context context;
	private MainActivity mainActivity;
	private CatDbHelper dbHelper;
	
	private Cat (final Context context, MainActivity mainActivity, int loadType, int id) {
		dbHelper = new CatDbHelper(context);
		
		this.context = context;
		this.mainActivity = mainActivity;
    	this.id = id;
    	if (loadType == GENERATE_NEW)
    		new GetOnlineCat().execute();
    	if (loadType == LOAD_CURRENT)
    		loadCatPrefs();
    	if (loadType == LOAD_DEFAULT)
    		loadDefault();
	}
	
	public static Cat loadCatOnLoad(Context context, MainActivity mainActivity) {
		CatDbHelper dbHelper = new CatDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM CATS", null);
		
		if (cur != null) {
			cur.moveToFirst();
			if (cur.getInt(0) == 0)
				return makeDefault(context, mainActivity);
			else {
				Cursor first = db.rawQuery("SELECT MIN(" + CatEntry._ID + ") FROM CATS", null);
				first.moveToFirst();
				int rowID = first.getInt(0);
				return loadCat(context, mainActivity, rowID);
			}
				
		}
		return makeDefault(context, mainActivity);
	}	
	
	public static Cat loadCat(Context context, MainActivity mainActivity, int catNumber) {
		return new Cat(context, mainActivity, LOAD_CURRENT, catNumber);
	}
	
	public static Cat generateCat(Context context, MainActivity mainActivity) {
		int id = 1;
		return new Cat(context, mainActivity, GENERATE_NEW, id);
	}
	
	public static Cat makeDefault(Context context, MainActivity mainActivity) {
		int id = 1;
		return new Cat(context, mainActivity, LOAD_DEFAULT, id);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getBad() {
		return bad;
	}
	
	public void deleteCat() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(CatEntry.TABLE_NAME, "_ID = " + id, null);
	}
	
	private void loadDefault() {
		name = "Amber";
		bad = true;
		finishCreation();
	}
	
	private void loadCatPrefs() {
		//Log.e("Get", id);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String getInfo = "SELECT " + CatEntry.COLUMN_NAME_NAME + "," + CatEntry.COLUMN_NAME_BAD +
				" FROM " + CatEntry.TABLE_NAME + " WHERE _ID = " + id;
		Cursor c = db.rawQuery(getInfo, null);
		c.moveToFirst();
		
		name = c.getString(0);
		int badInt = c.getInt(1);
		
		if (badInt == 0)
			bad = false;
		else
			bad = true;
	}
	
	private class GetOnlineCat extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... params) {
        	
        	
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

            	
        	    HttpEntity entity = result.getEntity();
        	    String textResult = null;
				try {
					textResult = EntityUtils.toString(entity);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					loadCatPrefs();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					loadCatPrefs();
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
					loadCatPrefs();
					return;
				}
				finishCreation();
				mainActivity.onInit();
            }
        }
	}
	
	private void finishCreation() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CatEntry.COLUMN_NAME_NAME, name);
		values.put(CatEntry.COLUMN_NAME_BAD, bad);
		
		id = db.insert(CatEntry.TABLE_NAME, null, values);
		
		if (id != 1) {
			MediaPlayer meow = null;
			if (bad) 
				meow = MediaPlayer.create(context, R.raw.bad_meow);
			else
				meow = MediaPlayer.create(context, R.raw.good_meow);
			meow.start();
		}
	}
}
