package com.example.sunshine;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings.Secure;

public class MessageService extends IntentService{
	// Private instance variables
	private MqttClient client;
	private boolean	quietMode;
	private MqttConnectOptions conOpt;
	private boolean clean;
	private String brokerUrl = "192.168.0.17";
	private String clientId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	
	public MessageService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent workIntent){
		String dataString = workIntent.getDataString();
	}

	private String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

	private boolean connect(){

		return false;
	}

}
