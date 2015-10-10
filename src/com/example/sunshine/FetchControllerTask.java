package com.example.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.sunshine.LEDContract.LEDEntry;

public class FetchControllerTask extends AsyncTask<Void, Void, Controller[]>{
	private Context context;
	private ListViewAdapter adapter;
	
	public FetchControllerTask(Context context, ListViewAdapter adapter){
		this.context = context;
		this.adapter = adapter;
	}
	
	@Override
	protected Controller[] doInBackground(Void... params) {
		SQLiteDatabase db = new LEDDbHelper(context).getReadableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, null, null, null, null, null);
		if(!c.moveToFirst()){
			c.close();
			db.close();
			return null;
		}
		Controller[] results = new Controller[c.getCount()];
		for(int i  = 0; i < c.getCount(); i++){
			results[i] = new Controller(c.getString(1), c.getString(2), c.getInt(3));
			c.moveToNext();
		}
		db.close();
		c.close();
		return results;
	}
	
	@Override
	protected void onPostExecute(Controller[] results){
		if(results != null && adapter !=null){
			adapter.clear();
			for(Controller s : results){
				adapter.add(s);
			}
		}
	}
}
