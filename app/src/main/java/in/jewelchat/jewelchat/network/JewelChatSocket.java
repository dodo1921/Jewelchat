package in.jewelchat.jewelchat.network;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.service.service_helpers.LooperThread;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */

public class JewelChatSocket {

	private Socket socket;

	private Object msg;
	private LooperThread mLooper;
	private Messenger mMessenger;

	public JewelChatSocket(){

		IO.Options opts = new IO.Options();
		opts.forceNew = false;
		opts.reconnection = true;
		opts.reconnectionAttempts = 5;

		try {

			this.socket = IO.socket("http://192.168.0.103:8081", opts);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		mLooper = new LooperThread();
		mLooper.start();
		mMessenger = new Messenger(mLooper.getHandler());

		initialize();

	}

	private void initialize() {

		socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {

			public void call(Object... args) {
				Transport transport = (Transport)args[0];

				transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
					@Override
					public void call(Object... args) {

						Log.i(">>>>Socket", args[0].toString());
						Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
						// modify request headers
						Log.i("SOCKET COOKIE SET", "Cookie");
						//List<String> c = headers.get("Cookie");
						List<String> c = new ArrayList<String>();
						c.add(JewelChatApp.getCookie());

						if(JewelChatApp.getGCLB() != null )
							c.add(JewelChatApp.getGCLB());
						headers.put("Cookie", c);
					}
				});

				transport.on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
					@Override
					public void call(Object... args) {

						Log.i("<<<<<<<Socket", args[0].toString());
						Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
						// access response headers
						/*
						List<String> cookie = headers.get("Set-Cookie");

						for ( String c: cookie ) {
							if(c.contains("GCLB"))
								JewelChatApp.setGCLB(c);
						}
						*/

					}
				});

			}
		});

		socket.on( Socket.EVENT_CONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

				Log.i("Socket","Connect");

			}

		}).on( Socket.EVENT_CONNECTING, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

			}

		}).on( "publish_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "new_msg" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "delivery_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "msg_delivery" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "read_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "msg_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "publish_group_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "new_group_msg" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "msg_group_delivery" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "delivery_group_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "msg_group_read" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( "read_group_ack" , new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					JewelChatApp.appLog(e.toString());
				}
			}

		}).on( Socket.EVENT_DISCONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

			}

		}).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {



			}

		}).on( Socket.EVENT_RECONNECTING, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

			}

		}).on(Socket.EVENT_ERROR, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				socket.disconnect();
			}

		});

	}


	public void emitEventOneToOneMessage( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//Process aargs and then send

				Message msg = Message.obtain(null, 0, args[0]);
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//send the returned object to loopback thread for processing

			}

		});

	}

	public void emitEventGroupMessage( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}

	public void emitEventDeliveredMsg( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}

	public void emitEventReadMsg( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}

	public void emitEventPresenceMsg( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}

	public void emitEventTypingMsg( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}


	public void emitEventHistoryMsg( String eventName, Object obj ){

		this.socket.emit(eventName, obj, new Ack() {
			@Override
			public void call(Object... args) {

				//send the returned object to loopback thread for processing

			}

		});

	}



	public Socket getSocket(){

		return this.socket;

	}





}
