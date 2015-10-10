package com.example.sunshine;

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
import android.widget.Button;
import android.widget.TextView;

public class LEDDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leddetail);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new LEDDetailFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leddetail, menu);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Intent refresh = new Intent(this, LEDDetailActivity.class);
			refresh.putExtra(Intent.EXTRA_TEXT, data.getStringExtra(Intent.EXTRA_TEXT));
			startActivity(refresh);
			this.finish();
		}
	}

	public static class LEDDetailFragment extends Fragment {

		String name, ip;
		int color;
		static Context context;
		
		public LEDDetailFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_leddetail,
					container, false);
			
			context = rootView.getContext();
			TextView textView = (TextView) rootView.findViewById(R.id.ledDetailTextView);
			Bundle bundle = getActivity().getIntent().getExtras();
			Controller controller = ListViewAdapter.getController(bundle.getString(Intent.EXTRA_TEXT));
			if(controller == null){
				controller = new Controller();
			}
			name = controller.getName();
			ip = controller.getIpAddress();
			color = controller.getColor();
			
			textView.setText(name+"\n");
			textView.append(ip+"\n");
			textView.append(Integer.toHexString(color)+"\n");
			
			Button edit= (Button) rootView.findViewById(R.id.ledDetailEdit);
			edit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), EditControllerActivity.class);
					i.putExtra(Intent.EXTRA_TEXT, name);
					getActivity().startActivityForResult(i, 1);
				}
				
			});
			
			Button send = (Button) rootView.findViewById(R.id.ledDetailSend);
			send.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					String brokerURL = "tcp://" + ip + ":1883";
					AcessMqtt access = new AcessMqtt();
					
					if(access.init(brokerURL, "BENDIESEL", "MC", "spongebobert")){
						access.write("outTopic", Integer.toHexString(color));
						access.reset();
					}
				}
			});
			return rootView;
		}
	}
}
