package com.example.sunshine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends ActionBarActivity {
	public static SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new MainActivityFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainActivityFragment extends Fragment {

		public static String postalCode;
		public static String mode;
		public static String units;
		public static String count;
		public ArrayList<String> list = new ArrayList<String>();
		public ArrayAdapter<String> adapter;

		public MainActivityFragment() {
			setHasOptionsMenu(true);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			adapter = new ArrayAdapter<String>(getActivity(),
					R.layout.list_item_forecast,
					R.id.list_item_forecast_textview,
					list);

			ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String weatherStr = (String) parent.getItemAtPosition(position);

					Intent i = new Intent(view.getContext(), DetailActivity.class);
					Bundle sendBundle = new Bundle();
					sendBundle.putString(i.EXTRA_TEXT, weatherStr);

					i.putExtras(sendBundle);
					startActivity(i);
				}
			});
			refresh();
			return rootView;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch(item.getItemId()){
			case R.id.action_settings:
				Intent i = new Intent(getContext(), SettingsActivity.class);
				startActivity(i);
				return true;
			case R.id.action_refresh:
				refresh();
				return true;
			case R.id.action_view_location:
				viewLocationOnMap();
				return true;
			case R.id.action_color_activity:
				Intent j = new Intent(getContext(), LEDStartPage.class);
				startActivity(j);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		private void viewLocationOnMap() {
			postalCode = sharedPreferences.getString("POSTCODE", "91945");
			Intent i = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("geo:0,0?q="+postalCode));
			startActivity(i);
		}

		private void refresh(){
			if(!adapter.isEmpty())
				adapter.clear();

			FetchWeatherTask task = new FetchWeatherTask(getActivity(), adapter);
			task.execute();
		}
	}
}
