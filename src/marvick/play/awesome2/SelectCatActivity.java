package marvick.play.awesome2;

import java.util.ArrayList;

import marvick.play.awesome2.CatDatabaseContract.CatEntry;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectCatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_cat);
		
		final ListView listview = (ListView) findViewById(R.id.catSelectionList);
		CatDbHelper dbHelper = new CatDbHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		String query = "SELECT " + "_ID" + ", " + CatEntry.COLUMN_NAME_NAME + " FROM " + CatEntry.TABLE_NAME;
		Cursor cur = db.rawQuery(query, null);
		
		cur.moveToFirst();
		ArrayList<Integer> catIDs = new ArrayList<Integer>();
		ArrayList<String> catNames = new ArrayList<String>();
		
		while (!cur.isAfterLast()) {
			int id = cur.getInt(0);
			String name = cur.getString(1);
			catIDs.add(id);
			catNames.add(name);
			cur.moveToNext();
		}
		
		final ArrayList<Integer> catIDsShare = catIDs;
		//TODO this is SUPER hacky!!
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catNames);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int catNumber = catIDsShare.get(position);;
				((MyApp)getApplicationContext()).setActiveCat(Cat.loadCat(getApplicationContext(), null, catNumber));
				SelectCatActivity.this.finish();
			}
			
		});
		
	}
}
