package Test.data;

import java.util.HashSet;

import com.example.sunshine.LEDContract;
import com.example.sunshine.LEDDbHelper;
import com.example.sunshine.WeatherContract;
import com.example.sunshine.LEDContract.LEDEntry;
import com.example.sunshine.WeatherContract.LocationEntry;
import com.example.sunshine.WeatherDbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        mContext.deleteDatabase(LEDDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        mContext.deleteDatabase(LEDDbHelper.DATABASE_NAME);
        SQLiteDatabase db2 = new LEDDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db2.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {
    	String testLocationSetting = "99705";
    	String testCityName = "North Pole";
    	double testLatitude = 64.7488;
    	double testLongitude = -147.353;
    	
        // First step: Get reference to writable database
    	SQLiteDatabase db = new WeatherDbHelper(this.getContext()).getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
    	ContentValues values = new ContentValues();
    	values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
    	values.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
    	values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
    	values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        // Insert ContentValues into database and get a row ID back
    	long rowId = db.insert(LocationEntry.TABLE_NAME, null, values);
    	assertTrue(rowId != -1);
    	
    	
        // Query the database and receive a Cursor back
    	Cursor c = db.query(LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
    	assertTrue(c.moveToFirst());
    	c.moveToPosition((int)rowId-1);
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
    	TestUtilities.validateCurrentRecord("FUCK", c, values);
    	
        // Finally, close the cursor and database
    	db.close();
    	c.close();

    }
    
    public void testLEDTable(){
    	String testName = "Arduino";
    	String testIp = "192.168.0.14";
    	int testColor = 0xFFFF0000;
    	
    	SQLiteDatabase db = new LEDDbHelper(this.getContext()).getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(LEDEntry.COLUMN_NAME, testName);
    	values.put(LEDEntry.COLUMN_IPADDRESS, testIp);
    	values.put(LEDEntry.COLUMN_COLOR, testColor);
    	
    	long rowId = db.insert(LEDEntry.TABLE_NAME, null, values);
    	assertTrue(rowId != -1);
    	
    	Cursor c = db.query(LEDEntry.TABLE_NAME, null, null, null, null, null, null);
    	assertTrue(c.moveToFirst());
    	c.moveToPosition((int)rowId-1);
    	
    	TestUtilities.validateCurrentRecord("SHIT", c, values);
    	
    	db.close();
    	c.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)

        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back

        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        return -1L;
    }
}