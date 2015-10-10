package com.example.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.sunshine.LEDContract.LEDEntry;

public class ColorSelectPopup extends PopupWindow{
	PopupWindow pwindow;
	View layout;
	Button selectColor, submit, close;
	String color = "FFFFFFFF";
	EditText name, ip;
	final Context context;

	public ColorSelectPopup(View layout){
		this.layout = layout;
		this.context = layout.getContext();
		this.pwindow = new PopupWindow(layout, 300,370, true);
		this.selectColor = (Button) layout.findViewById(R.id.createControllerColor);
		this.submit = (Button) layout.findViewById(R.id.createControllerSubmit);
		this.close = (Button) layout.findViewById(R.id.createControllerClose);
		this.name = (EditText) layout.findViewById(R.id.createControllerName);
		this.ip = (EditText) layout.findViewById(R.id.createControllerIp);
		
		//Add all action Listeners
		selectColor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, ColorActivity.class);
				context.startActivity(i);
			}
			
		});
		
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String nameText = name.getText().toString();
				String ipText = ip.getText().toString();
				if(nameText == null || nameText.length() == 0){
					Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
					return;
				}
				if(ipText == null || ipText.length() == 0){
					Toast.makeText(context, "Please enter an IP Address", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!Controller.checkName(nameText, context)){
					Toast.makeText(context, "Name is already taken", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!Controller.checkIP(ipText, context)){
					Toast.makeText(context, "IP already in use", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Controller controller = new Controller(nameText, ipText, (int) Long.parseLong(color, 16), context);
				ListViewAdapter.addController(controller);
				close();
				//LEDStartPage.LEDStartFragment.refresh();
			}
		});
		
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				close();
			}
		});
		
	}
	
	private void close() {
		// TODO Auto-generated method stub
		pwindow.dismiss();
		pwindow = null;
		selectColor = null;
		submit = null;
		close = null;
		color = null;
		
	}

	@Override
	public void showAsDropDown(View view){
		pwindow.showAsDropDown(view);
	}
	
	public void setColor(String color){
		this.color = color;
		layout.setBackgroundColor(Color.parseColor("#"+color));
	}
}

