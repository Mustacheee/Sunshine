package com.example.sunshine;

import android.provider.BaseColumns;

public class LEDContract {
	
	public static final class LEDEntry implements BaseColumns{
		public static final String TABLE_NAME = "LEDControllers";
		
		public static final String COLUMN_NAME = "name";
		
		public static final String COLUMN_IPADDRESS = "ip";
		
		public static final String COLUMN_COLOR = "color";
	}

}
