package in.jewelchat.jewelchat.service.service_helpers;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.database.database_crud.InsertNewGroupMessage;
import in.jewelchat.jewelchat.database.database_crud.InsertNewMessage;
import in.jewelchat.jewelchat.database.database_crud.UpdateDeliveryAck;
import in.jewelchat.jewelchat.database.database_crud.UpdateMessageDelivered;
import in.jewelchat.jewelchat.database.database_crud.UpdateMessageRead;
import in.jewelchat.jewelchat.database.database_crud.UpdatePublishAck;
import in.jewelchat.jewelchat.database.database_crud.UpdatePublishGroupAck;
import in.jewelchat.jewelchat.database.database_crud.UpdateReadAck;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */

public class LooperThread extends Thread {
	private Handler mHandler;
	private Looper mLooper;
	private String msgchannel;
	private String groupchannel;

	private static final String TAG = "LooperThread";



	public Looper getLooper(){
		return mLooper;
	}

	public Handler getHandler(){
		synchronized(this){
			while (mHandler == null){
				try{
					wait();
				}catch(InterruptedException e){}
			}
		}
		return mHandler;
	}

	static class MyHandler extends Handler{
		public void handleMessage(Message msg){

			JSONObject packet = (JSONObject)msg.obj;

			try {
				String eventname = packet.getString("eventname");

				switch(eventname){
					case "new_msg":{
						Intent s = new Intent(JewelChatApp.getInstance(), InsertNewMessage.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "publish_ack":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdatePublishAck.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "msg_delivery":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdateMessageDelivered.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "delivery_ack":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdateDeliveryAck.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "msg_read":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdateMessageRead.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "read_ack":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdateReadAck.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "new_group_msg":{
						Intent s = new Intent(JewelChatApp.getInstance(), InsertNewGroupMessage.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}
					case "publish_group_ack":{
						Intent s = new Intent(JewelChatApp.getInstance(), UpdatePublishGroupAck.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
						break;
					}

				}


			} catch (JSONException e) {
				JewelChatApp.appLog(getClass().getSimpleName()+":"+e);
			}

		}
	}


	public void run(){
		Looper.prepare();
		synchronized(this){
			mHandler = new LooperThread.MyHandler();
			mLooper = Looper.myLooper();
			notifyAll();
		}
		Looper.loop();

	}
}
