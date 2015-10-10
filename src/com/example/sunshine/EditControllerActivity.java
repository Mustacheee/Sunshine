package com.example.sunshine;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class EditControllerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_controller);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new EditControllerFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_controller, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class EditControllerFragment extends Fragment {
		TextView nameView, ipView, colorView;
		String name, ip;
		int color;
		Context context;
		Controller controller;

		public EditControllerFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_edit_controller,
					container, false);
			
			this.context = rootView.getContext();
			
			Bundle bundle = getActivity().getIntent().getExtras();
			this.controller = ListViewAdapter.getController(bundle.getString(Intent.EXTRA_TEXT));
			if(controller == null){
				this.controller = new Controller();
			}

			nameView = (TextView) rootView.findViewById(R.id.editControllerName);
			this.name = controller.getName();
			nameView.setText(name);
			
			ipView = (TextView) rootView.findViewById(R.id.editControllerIP);
			this.ip = controller.getIpAddress();
			ipView.setText(ip);
			
			colorView = (TextView) rootView.findViewById(R.id.editControllerColor);
			this.color = controller.getColor();
			colorView.setText(Integer.toHexString(color));
			
			nameView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					final EditControllerDialog dialog = new EditControllerDialog(context, EditControllerDialog.EDIT_NAME, controller);
					dialog.setTitle("Change Name");
					dialog.setText(name);
					dialog.show();
				}	
			});
			
			ipView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					final EditControllerDialog dialog = new EditControllerDialog(context, EditControllerDialog.EDIT_IP, controller);
					dialog.setTitle("Edit IP");
					dialog.setText(ip);
					dialog.show();
				}
			});
			
			colorView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					Intent i = new Intent(context, ColorActivity.class);
					startActivityForResult(i, 1);
				}
			});
			return rootView;
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			if(requestCode ==1){
				if(resultCode == RESULT_OK){
					String color = data.getStringExtra(Intent.EXTRA_TEXT);
					colorView.setText(color);
					this.controller.setColor(Long.parseLong(color,16), context);
				}
			}
		}
	}
}
