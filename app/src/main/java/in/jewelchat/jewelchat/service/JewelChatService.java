package in.jewelchat.jewelchat.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChat;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.database.database_crud.InsertNewGroupMessage;
import in.jewelchat.jewelchat.database.database_crud.InsertNewMessage;
import in.jewelchat.jewelchat.database.database_crud.UpdateDeliveryAck;
import in.jewelchat.jewelchat.database.database_crud.UpdateMessageDelivered;
import in.jewelchat.jewelchat.database.database_crud.UpdateMessageRead;
import in.jewelchat.jewelchat.database.database_crud.UpdatePublishAck;
import in.jewelchat.jewelchat.database.database_crud.UpdatePublishGroupAck;
import in.jewelchat.jewelchat.database.database_crud.UpdateReadAck;

import static android.content.ContentValues.TAG;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class JewelChatService extends FirebaseMessagingService{

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// TODO(developer): Handle FCM messages here.
		// If the application is in the foreground handle both data and notification messages here.
		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		Log.d(TAG, "From: " + remoteMessage.getFrom());
		Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

		Log.i("OMG FCM", "OMG FCM MSG");

		JSONObject packet = new JSONObject(remoteMessage.getData());

		String eventname = null;
		try {

			eventname = packet.getString("eventname");
			switch(eventname){
				case "new_msg":{
					Intent s = new Intent(getApplicationContext(), InsertNewMessage.class);
					s.putExtra("json", packet.toString());
					startService(s);
					sendNotification(remoteMessage.getNotification().getBody());
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
			e.printStackTrace();
		}


	}


	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, JewelChat.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher_jewelchat)
				.setContentTitle("Firebase Push Notification")
				.setContentText(messageBody)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, notificationBuilder.build());
	}
}
