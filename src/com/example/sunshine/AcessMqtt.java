package com.example.sunshine;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class AcessMqtt {
	private MqttClient client = null;
	
	public boolean init(String brokerURL, String clientId, String accountName, String accountPassword){
		if(client == null){
			try{
				client = new MqttClient(brokerURL, clientId, new MemoryPersistence());
				if(client != null){
					MqttConnectOptions options = new MqttConnectOptions();
					
					options.setUserName(accountName);
					options.setPassword(accountPassword.toCharArray());
				//	options.setSocketFactory(SslUtility.getInstance().getSocketFactory(certificateId, keystorePassword));
					options.setCleanSession(true);
					client.connect(options);
				}
			}catch(MqttException e){
				reset();
			}

		}
		return client != null;
	}
	
	public boolean write(String mqttTopic, String mqttMessage){
		MqttDeliveryToken deliveryToken = null;
		
		if(client != null){
			try{
				deliveryToken = client.getTopic(mqttTopic).publish(mqttMessage.getBytes(),1,false);
			}
			catch(MqttPersistenceException ex){
				//Log
			}
			catch(MqttException ex){
				//Log
			}
		}
		return deliveryToken != null;
	}
	
	public void reset(){
		if(client != null){
			try{
				if(client.isConnected())
					client.disconnect(0);
			}catch(MqttException e){
				//Log
			}
			client = null;
		}
	}

}
