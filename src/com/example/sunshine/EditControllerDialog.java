package com.example.sunshine;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.LEDContract.LEDEntry;

public class EditControllerDialog extends Dialog {
	public static final int EDIT_NAME = 0;
	public static final int EDIT_IP = 1;
	private EditText editText;
	private Button close, ok;
	private int mode;
	private Controller controller;
	
	public EditControllerDialog(Context context, int mode, Controller controller) {
		super(context);
		this.mode = mode;
		this.controller = controller;
		this.setContentView(R.layout.dialog_layout);
		this.close = (Button) findViewById(R.id.dialogClose);
		this.ok = (Button) findViewById(R.id.dialogOK);
		this.editText = (EditText) findViewById(R.id.dialogEditText);
		
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newValue = editText.getText().toString();
				Controller mController = getController();
				
				if(getMode() == EDIT_NAME){
					if(newValue.equals(mController.getName())){
						dismiss();
						return;
					}
					if(!mController.setName(newValue, getContext())){
						Toast.makeText(getContext(), "Name already exists", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
					dismiss();
					return;
				}
				
				else if(getMode() == EDIT_IP){
					if(newValue.equals(mController.getIpAddress())){
						dismiss();
						return;
					}
					if(!mController.setIpAddress(newValue, getContext())){
						Toast.makeText(getContext(), "IP already in use", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(getContext(), "IP update successful", Toast.LENGTH_SHORT).show();
					dismiss();
					return;
				}
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public void setText(String s){
		editText.setText(s);
	}

	public int getMode(){
		return this.mode;
	}
	
	public Controller getController(){
		return this.controller;
	}
	
	
}
