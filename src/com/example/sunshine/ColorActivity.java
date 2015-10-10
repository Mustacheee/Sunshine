package com.example.sunshine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorActivity extends ActionBarActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new ColorFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.color, menu);
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
	public static class ColorFragment extends Fragment {
		BitmapDrawable d;
		View rootView;
		ColorSelectPopup pwindow;
		ImageView imageView;
		SeekBar redBar;
		SeekBar greenBar;
		SeekBar blueBar;
		View colorPatch;
		int finalWidth, finalHeight;

		private OnSeekBarChangeListener colorBarListener = new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int red = redBar.getProgress();
				int green = greenBar.getProgress();
				int blue = blueBar.getProgress();
				updateColorPatch(0xFF000000 + (red * 0x10000) +
						(green * 0x100)+
						(blue));

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		};

		public ColorFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			this.rootView = inflater.inflate(R.layout.fragment_color,
					container, false);
			Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.colors);
			d = new BitmapDrawable(getResources(), bg);

			this.imageView = (ImageView) rootView.findViewById(R.id.colorImageView);
			this.redBar = (SeekBar) rootView.findViewById(R.id.redBar);
			this.greenBar = (SeekBar) rootView.findViewById(R.id.greenBar);
			this.blueBar = (SeekBar) rootView.findViewById(R.id.blueBar);

			colorPatch = rootView.findViewById(R.id.color_patch);
			colorPatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//LEDStartPage.LEDStartFragment.setColor(getColor());
					//getActivity().finish();
					Intent i = new Intent();
					i.putExtra(Intent.EXTRA_TEXT, getColor());
					getActivity().setResult(RESULT_OK, i);
					getActivity().finish();
				}
			});
			
			
			
			imageView.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						float widthRatio = event.getX()/v.getWidth();
						float heightRatio = event.getY()/v.getHeight();

						Bitmap bg = d.getBitmap();
						int x = Math.round(widthRatio * bg.getWidth());
						int y = Math.round(heightRatio * bg.getHeight());

						int pixel = bg.getPixel(x, y);
						updateColorPatch(pixel);
						return true;
					default:
						return false;
					}
				}
			});
			imageView.setBackground(d);
			redBar.setOnSeekBarChangeListener(colorBarListener);
			greenBar.setOnSeekBarChangeListener(colorBarListener);
			blueBar.setOnSeekBarChangeListener(colorBarListener);
			return rootView;
		}

		public void updateColorPatch(int pixel){
			String color = Integer.toHexString(pixel).toUpperCase();
			colorPatch.setBackgroundColor(Color.parseColor("#"+color));

			int red = (pixel >> 16) & 0xFF;
			int green = (pixel >> 8) &0xFF;
			int blue = pixel & 0xFF;

			redBar.setProgress(red);
			greenBar.setProgress(green);
			blueBar.setProgress(blue);
		}
		
		private String getColor(){
			int red = redBar.getProgress();
			int green = greenBar.getProgress();
			int blue = blueBar.getProgress();
			return Integer.toHexString(0xFF000000 + (red * 0x10000) +
					(green * 0x100)+
					(blue)).toUpperCase();
			
		}
	}
}

