package com.example.sunshine;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class LEDStartPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ledstart_page);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new LEDStartFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ledstart_page, menu);
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
	public static class LEDStartFragment extends Fragment {
		View rootView;
		ListView controllerList;
		Button addBtn;
		ColorSelectPopup pwindow;
		ArrayList<Controller> list = new ArrayList<Controller>();
		ListViewAdapter adapter;
		LayoutInflater inflater;
		private Context context;

		public LEDStartFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflate, ViewGroup container,
				Bundle savedInstanceState) {
			inflater = inflate;
			rootView = inflater.inflate(R.layout.fragment_ledstart_page,
					container, false);

			context = getActivity();
			controllerList = (ListView) rootView.findViewById(R.id.controllerListView);
			addBtn = (Button) rootView.findViewById(R.id.controllerAddBtn);
			adapter = new ListViewAdapter(context,R.layout.list_item_led, list);

			controllerList.setAdapter(adapter);
			controllerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			controllerList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i = new Intent(context, LEDDetailActivity.class);
					i.putExtra(Intent.EXTRA_TEXT, list.get(position).getName());
					startActivity(i);
					
				}
				
			});
			controllerList.setMultiChoiceModeListener(new MultiChoiceModeListener(){

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					mode.getMenuInflater().inflate(R.menu.color, menu);
					return true;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch(item.getItemId()){
					case(R.id.action_delete):
						SparseBooleanArray selected = adapter.getSelectedIds();
						for(int i = (selected.size() - 1); i >= 0;i--){
							if(selected.valueAt(i)){
								Controller controllerName = adapter.getItem(selected.keyAt(i));
								adapter.remove(controllerName);
							}
						}
						mode.finish();
						return true;
					}
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					adapter.removeSelection();

				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					final int checkedCount = controllerList.getCheckedItemCount();
					mode.setTitle(checkedCount + " Selected");
					adapter.toggleSelection(position);
				}

			});
			addBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					addController();
				}				
			});
			return rootView;
		}

		public void addController() {
			// TODO Auto-generated method stub
			View pView = inflater.inflate(R.layout.create_controller, null);
			pwindow = new ColorSelectPopup(pView);
			pwindow.showAsDropDown(addBtn);

		}

		public void refresh(){
			FetchControllerTask task = new FetchControllerTask(context, adapter);
			task.execute();
		}

		public void setColor(String color) {
			// TODO Auto-generated method stub
			pwindow.setColor(color);
		}

		@Override
		public void onResume(){
			super.onResume();
			refresh();
		}
	}
}
