package com.example.sunshine;

import java.util.ArrayList;
import java.util.List;

import com.example.sunshine.LEDContract.LEDEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

public class ListViewAdapter extends ArrayAdapter<Controller> {
	Context context;
	LayoutInflater inflater;
	static ArrayList<Controller> controllerList;
	private SparseBooleanArray mSelectedIds;
	
	public ListViewAdapter(Context context, int resourceId,
				ArrayList<Controller> controllerList){
		super(context, resourceId, controllerList);
		this.mSelectedIds = new SparseBooleanArray();
		this.context = context;
		this.controllerList = controllerList;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public void remove(Controller s){
		SQLiteDatabase db = new LEDDbHelper(context).getWritableDatabase();
		db.delete(LEDEntry.TABLE_NAME, LEDEntry.COLUMN_NAME + " = '" + s.getName() + "'", null);
		db.close();
		controllerList.remove(s);
		notifyDataSetChanged();
	}
	
	public List<Controller> getControllerList(){
		return controllerList;
	}
	
	public void toggleSelection(int position){
		selectView(position, !mSelectedIds.get(position));
	}
	
	public void selectView(int position, boolean value){
		if(value)
			mSelectedIds.put(position, value);
		else
			mSelectedIds.delete(position);
		notifyDataSetChanged();
	}
	
	public void removeSelection(){
		mSelectedIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}
	
	public int getSelectedCount(){
		return mSelectedIds.size();
	}
	
	public SparseBooleanArray getSelectedIds(){
		return mSelectedIds;
	}
	
	public static Controller getController(String name){
		int size = controllerList.size();
		String s;
		for(int i = 0; i < size; i++){
			s = controllerList.get(i).getName();
			if(s.equals(name)){
				return controllerList.get(i);
			}
		}
		return null;
	}

	public static void addController(Controller controller) {
		controllerList.add(controller);
	}

}
