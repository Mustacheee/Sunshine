package com.example.sunshine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sunshine.LEDContract.LEDEntry;

public class LEDDbHelper extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_NAME = "led.db";

	public LEDDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_LED_TABLE = "CREATE TABLE " + LEDEntry.TABLE_NAME + " (" +
					LEDEntry._ID + " INTEGER PRIMARY KEY," +
					LEDEntry.COLUMN_NAME + " TEXT NOT NULL," +
					LEDEntry.COLUMN_IPADDRESS + " TEXT NOT NULL," +
					LEDEntry.COLUMN_COLOR + " INTEGER NOT NULL);";
		db.execSQL(SQL_CREATE_LED_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

}
