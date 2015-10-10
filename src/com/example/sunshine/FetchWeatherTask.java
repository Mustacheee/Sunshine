package com.example.sunshine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.sunshine.WeatherContract.LocationEntry;
import com.example.sunshine.WeatherContract.WeatherEntry;

public class FetchWeatherTask extends AsyncTask<Void, Void, String[]> {
	private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
	private Context context;
	private ArrayAdapter<String> adapter;
	
	
	public FetchWeatherTask(Context context, ArrayAdapter<String> adapter){
		this.adapter = adapter;
		this.context = context;
	}
	@Override
	protected String[] doInBackground(Void... params){

		HttpURLConnection urlConnection = null;
		BufferedReader bufferedReader = null;
		String forecastJsonStr = null;

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String postCode = sharedPreferences.getString("POSTCODE", "91945");
		String units = sharedPreferences.getString("PREF_UNITS", "imperial");
		String format = "json";
		int numDays = Integer.parseInt(sharedPreferences.getString("COUNT", "7"));


		try{
			//Construct URL
			final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
			final String LOCATION_PARAM = "q";
			final String FORMAT_PARAM = "mode";
			final String UNITS_PARAM = "units";
			final String DAYS_PARAM = "cnt";

			Uri queryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
					.appendQueryParameter(LOCATION_PARAM, postCode)
					.appendQueryParameter(FORMAT_PARAM, format)
					.appendQueryParameter(UNITS_PARAM, units)
					.appendQueryParameter(DAYS_PARAM, numDays+"")
					.build();

			URL url = new URL(queryUri.toString());

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();

			if(inputStream == null)
				return null;

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while((line = bufferedReader.readLine()) !=null){
				buffer.append(line+"\n");
			}

			if(buffer.length() == 0)
				return null;

			forecastJsonStr = buffer.toString();
		}catch(IOException e){
			Log.e(LOG_TAG, "Error", e);
		}finally{
			urlConnection.disconnect();

			if(bufferedReader != null){
				try{
					bufferedReader.close();
				}
				catch(final IOException e){
					Log.e(LOG_TAG, "Error closing stream.", e);
				}
			}
		}

		try{
			return getWeatherDataFromJson(forecastJsonStr, postCode);
		}catch(JSONException e){
			Log.e(LOG_TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}

	private String[] getWeatherDataFromJson(String jsonStr, String postCode) throws JSONException {

		//City Information
		final String CITY_PARAM = "city";
		final String CITY_NAME = "name";

		//Coordinates Information << City
		final String COORD_PARAM = "coord";
		final String LAT_PARAM = "lat";
		final String LONG_PARAM = "lon";

		//Weather Information
		final String LIST_PARAM = "list";
		final String PRESSURE_PARAM = "pressure";
		final String HUMIDITY_PARAM = "humidity";
		final String WIND_SPEED_PARAM = "speed";
		final String WIND_DIRECTION_PARAM = "deg";

		//Temp Information << List
		final String TEMP_PARAM = "temp";
		final String MAX_TEMP = "max";
		final String MIN_TEMP = "min";

		//More WEather << List
		final String WEATHER_PARAM = "weather";
		final String DESC_PARAM = "main";
		final String WEATHER_ID = "id";

		try{
			JSONObject forecastJson = new JSONObject(jsonStr);
			JSONArray listOfDays = forecastJson.getJSONArray(LIST_PARAM);

			JSONObject cityJson = forecastJson.getJSONObject(CITY_PARAM);
			String cityName = cityJson.getString(CITY_NAME);

			JSONObject coordJson = cityJson.getJSONObject(COORD_PARAM);
			double cityLat = coordJson.getDouble(LAT_PARAM);
			double cityLong = coordJson.getDouble(LONG_PARAM);

			long locationId = addLocation(postCode, cityName, cityLat, cityLong);
			
			Vector<ContentValues> cVVector = new Vector<ContentValues>(listOfDays.length());
			
			//Normalize Time and Date
			Time dayTime = new Time();
			dayTime.setToNow();

			//Start at the day returned by local time
			int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
			dayTime = new Time();
			
			for(int i = 0; i < listOfDays.length(); i++){
				long dateTime;
				double pressure;
				int humidity;
				double windSpeed;
				double windDirection;
				
				double high;
				double low;
				
				String description;
				int weatherId;
				
				//Get JSONObject for a day
				JSONObject dayForecast = listOfDays.getJSONObject(i);
				
				dateTime = dayTime.setJulianDay(julianStartDay + i);
				pressure = dayForecast.getDouble(PRESSURE_PARAM);
				humidity = dayForecast.getInt(HUMIDITY_PARAM);
				windSpeed = dayForecast.getDouble(WIND_SPEED_PARAM);
				windDirection = dayForecast.getDouble(WIND_DIRECTION_PARAM);
				
				JSONObject weatherObject = dayForecast.getJSONArray(WEATHER_PARAM).getJSONObject(0);
				description = weatherObject.getString(DESC_PARAM);
				weatherId = weatherObject.getInt(WEATHER_ID);
				
				JSONObject tempObject = dayForecast.getJSONObject(TEMP_PARAM);
				high = tempObject.getDouble(MAX_TEMP);
				low = tempObject.getDouble(MIN_TEMP);
				
				ContentValues values = new ContentValues();
				values.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
				values.put(WeatherEntry.COLUMN_DATE, dateTime);
				values.put(WeatherEntry.COLUMN_SHORT_DESC, description);
				values.put(WeatherEntry.COLUMN_MIN_TEMP, low);
				values.put(WeatherEntry.COLUMN_MAX_TEMP, high);
				values.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
				values.put(WeatherEntry.COLUMN_PRESSURE, pressure);
				values.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
				values.put(WeatherEntry.COLUMN_DEGREES, windDirection);
				values.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
				
				cVVector.add(values);
			}
				
				if(cVVector.size() > 0){
					SQLiteDatabase db = new WeatherDbHelper(context).getWritableDatabase();
					int i = 0;
					for(ContentValues cv : cVVector){
						db.insert(WeatherEntry.TABLE_NAME, null, cv);
						i++;
					}
					
					if(i != cVVector.size())
						Log.e(LOG_TAG, "Something went wrong bulkInserting");
				}
				Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
						postCode, System.currentTimeMillis());
				
				Log.d(LOG_TAG, "FetchWeatherTask complete. " + cVVector.size() + " Inserted");
				String[] results = convertContentValuesToUXFormat(cVVector);
				return results;
			}catch(JSONException e){
				Log.e(LOG_TAG, e.getMessage(), e);
				e.printStackTrace();
			}
			return null;
		}

		//Extract the values from ContentValue objects and put into a string
		private String[] convertContentValuesToUXFormat(
			Vector<ContentValues> cVVector) {
		String[] results = new String[cVVector.size()];
		for(int i = 0; i < cVVector.size(); i++){
			ContentValues values = cVVector.elementAt(i);
			String highAndLow = formatHighLows(
					values.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP),
					values.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP));
			results[i] = getReadableDateString(
					values.getAsLong(WeatherEntry.COLUMN_DATE)) 
					+ " - " + values.getAsString(WeatherEntry.COLUMN_SHORT_DESC)
					+ " - " + highAndLow;
			
		}
		return results;
	}

		private long addLocation(String postCode, String cityName, double cityLat,
				double cityLong) {
			SQLiteDatabase db = new WeatherDbHelper(context).getWritableDatabase();
			String whereClause = "location_setting = ? AND city_name = ?";
			String[] whereArgs = new String[]{postCode, cityName};
			
			Cursor c = db.query(LocationEntry.TABLE_NAME, 
					null,
					whereClause,
					whereArgs, 
					null, 
					null, 
					null);
			//Check if location already exists
			if(!c.moveToFirst())
				return -1;
			
			c.close();
			//Else add location
			ContentValues values = new ContentValues();
			values.put(LocationEntry.COLUMN_LOCATION_SETTING, postCode);
			values.put(LocationEntry.COLUMN_CITY_NAME, cityName);
			values.put(LocationEntry.COLUMN_COORD_LAT, cityLat);
			values.put(LocationEntry.COLUMN_COORD_LONG, cityLong);
			
			long rowId = db.insert(LocationEntry.TABLE_NAME, null, values);
			db.close();
			return rowId;
		}

		private String formatHighLows(double min, double max) {
			long roundedHigh = Math.round(max);
			long roundedLow = Math.round(min);

			String highLowStr = roundedHigh + "/" + roundedLow;
			return highLowStr;
		}

		private String getReadableDateString(long dateTime) {
			SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
			return shortenedDateFormat.format(dateTime);
		}
		
		@Override
		protected void onPostExecute(String[] results){
			if(results != null && adapter != null){
				adapter.clear();
				for(String s : results){
					adapter.add(s);
				}
			}
		}
		
		
	}
