package com.example.sunshine;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class WeatherContract {
	//Authority for content provider
	public static final String CONTENT_AUTHORITY = "com.example.sunshine";
	
	//Base uri for content provider
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	//Possible paths
	public static final String PATH_WEATHER = "weather";
	public static final String PATH_LOCATION = "location";
	
	public static long normalizeDate(long startDate){
		//Normalize start date to beginning of UTC day
		Time time = new Time();
		time.set(startDate);
		int julianDay = Time.getJulianDay(startDate, time.gmtoff);
		return time.setJulianDay(julianDay);
	}
	
	public static final class LocationEntry implements BaseColumns{
		public static final String TABLE_NAME = "location";
		
		public static final String COLUMN_LOCATION_SETTING = "location_setting";
		
		public static final String COLUMN_CITY_NAME = "city_name";
		
		public static final String COLUMN_COORD_LAT = "coord_lat";
		
		public static final String COLUMN_COORD_LONG = "coord_long";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
					.appendPath(PATH_LOCATION).build();
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + CONTENT_AUTHORITY +"/" + PATH_LOCATION;
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
		
		public static Uri buildLocationUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
	
	public static final class WeatherEntry implements BaseColumns{
		public static final String TABLE_NAME = "weather";
		
		public static final String COLUMN_LOC_KEY = "location_id";
		
		public static final String COLUMN_DATE = "date";
		
		public static final String COLUMN_WEATHER_ID = "weather_id";
		
		public static final String COLUMN_SHORT_DESC = "short_desc";
	
		public static final String COLUMN_MIN_TEMP = "min";
		
		public static final String COLUMN_MAX_TEMP = "max";
		
		public static final String COLUMN_HUMIDITY = "humidity";
		
		public static final String COLUMN_PRESSURE = "pressure";
		
		public static final String COLUMN_WIND_SPEED = "wind_speed";
		
		public static final String COLUMN_DEGREES = "degrees";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_WEATHER).build();
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + CONTENT_AUTHORITY +"/" + PATH_WEATHER;
		
		public static Uri buildWeatherUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
		
		public static Uri buildWeatherLocation(String postCode){
			return CONTENT_URI.buildUpon().appendPath(postCode).build();
		}
		
		public static Uri buildWeatherLocationWithStartDate(String postCode,
				long currentTimeMillis) {
			long date = normalizeDate(currentTimeMillis);
			return CONTENT_URI.buildUpon().appendPath(postCode)
					.appendQueryParameter(COLUMN_DATE, Long.toString(currentTimeMillis)).build();
		}
		
		public static Uri buildWeatherLocationWithDate(String postCode, long date){
			return CONTENT_URI.buildUpon()
					.appendPath(postCode).appendPath(Long.toString(date)).build();
		}
		
		public static String getLocationSettingFromUri(Uri uri){
			return uri.getPathSegments().get(1);
		}
		
		public static long getDateFromUri(Uri uri){
			return Long.parseLong(uri.getPathSegments().get(2));
		}
		
		public static long getStartDateFromUri(Uri uri){
			String dateString = uri.getQueryParameter(COLUMN_DATE);
			if(dateString != null && dateString.length() > 0)
				return Long.parseLong(dateString);
			else
				return 0;
		}
	}
}
