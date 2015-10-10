package com.example.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.sunshine.LEDContract.LEDEntry;

public class Controller {
	String name;
	String ipAddress;
	int color;
	
	public Controller(){
		this.name = "Default";
		this.ipAddress = "Default";
		this.color = 0xFFFFFFFF;
	}
	
	public Controller(String name, String ipAddress, int color){
		this.name = name;
		this.ipAddress = ipAddress;
		this.color = color;
	}
	
	public Controller(String name, String ipAddress, int color, Context context){
		SQLiteDatabase db = new LEDDbHelper(context).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LEDEntry.COLUMN_NAME, name);
		values.put(LEDEntry.COLUMN_IPADDRESS, ipAddress);
		values.put(LEDEntry.COLUMN_COLOR, color);
		
		db.insert(LEDEntry.TABLE_NAME, null, values);
		db.close();
		
		this.name = name;
		this.ipAddress = ipAddress;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}

	public String getIpAddress() {
		return ipAddress;
	}


	public int getColor() {
		return color;
	}

	public boolean setName(String newName, Context context) {
		SQLiteDatabase db = new LEDDbHelper(context).getWritableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, "name = ?", new String[]{newName}, null, null, null);
		if(c.getCount() != 0){
			db.close();
			c.close();
			Toast.makeText(context, "Name already exists", Toast.LENGTH_SHORT).show();
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(LEDEntry.COLUMN_NAME, newName);
		db.update(LEDEntry.TABLE_NAME, values, "name = ?", new String[]{this.name});
		c.close();
		db.close();
		this.name = newName;
		return true;
	}
	
	public boolean setIpAddress(String newIP, Context context) {
		SQLiteDatabase db = new LEDDbHelper(context).getWritableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, "ip = ?", new String[]{newIP}, null, null, null);
		if(c.getCount() != 0){
			db.close();
			c.close();
			Toast.makeText(context, "IP already in use", Toast.LENGTH_SHORT).show();
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(LEDEntry.COLUMN_IPADDRESS, newIP);
		db.update(LEDEntry.TABLE_NAME, values, "name = ?", new String[]{this.name});
		c.close();
		db.close();
		this.ipAddress = newIP;
		return true;
	}
	
	public boolean setColor(long l, Context context) {
		SQLiteDatabase db = new LEDDbHelper(context).getWritableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, "name = ?", new String[]{this.name}, null, null, null);
		if(c.getCount() == 0){
			db.close();
			c.close();
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(LEDEntry.COLUMN_COLOR, l);
		db.update(LEDEntry.TABLE_NAME, values, "name = ?", new String[]{this.name});
		c.close();
		db.close();
		this.color = (int) l;
		return true;
	}
	
	@Override
	public String toString(){
		return this.name;
	}

	public static boolean checkName(String nameText, Context context) {
		SQLiteDatabase db = new LEDDbHelper(context).getReadableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, "name = ?", new String[]{nameText}, null, null, null);
		if(c.getCount() != 0){
			db.close();
			c.close();
			return false;
		}
		db.close();
		c.close();
		return true;
	}
	
	public static boolean checkIP(String ipText, Context context){
		SQLiteDatabase db = new LEDDbHelper(context).getReadableDatabase();
		Cursor c = db.query(LEDEntry.TABLE_NAME, null, "ip = ?", new String[]{ipText}, null, null, null);
		if(c.getCount()!=0){
			db.close();
			c.close();
			return false;
		}
		db.close();
		c.close();
		return true;
	}
}
