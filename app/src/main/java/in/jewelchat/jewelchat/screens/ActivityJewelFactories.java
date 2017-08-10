package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.FactoryAdapter;
import in.jewelchat.jewelchat.models.Factory;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models.TaskMaterials;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 16/06/17.
 */

public class ActivityJewelFactories extends BaseNetworkActivity implements Response.Listener<JSONObject>{

	private String className;
	private RecyclerView recyclerView;
	private FactoryAdapter adapterFactory;
	private FactoryAdapter.OnItemClickListener mOnItemClickListener;
	private List<Factory> factoryList;
	private int previousTotal = 0;
	private int firstVisibleItem = 0;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private boolean loading = true;
	private int pageCounter = 0;
	private boolean no_more_items_to_load = false;
	private int pos;
	private boolean network_call_is_on = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_factories);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		setUpAppbar();

		if(factoryList == null)
			factoryList = new ArrayList<Factory>();

		recyclerView = (RecyclerView)findViewById(R.id.factory_list);
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerView.setLayoutManager(mLayoutManager);


		mOnItemClickListener = new FactoryAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

				pos = position;
				switch (view.getId()) {
					case R.id.factory:
						for (Factory t:
								factoryList) {
							t.open = false;
						}
						factoryList.get(pos).open = true;
						List<TaskMaterials> list = factoryList.get(pos).materials;
						boolean r = true;
						for (TaskMaterials tm:
								list) {
							if(JewelChatApp.getSharedPref().getInt((tm.jeweltype_id+""),0)>=tm.count){
								tm.tick = true;
							}else{
								tm.tick = false;
								r = false;
							}
						}
						factoryList.get(pos).materials = list;
						factoryList.get(pos).all_materials_present = r;
						adapterFactory.notifyDataSetChanged();
						break;
					case R.id.start :
						if(factoryList.get(pos).all_materials_present && factoryList.get(pos).buttonState==0) {
							//Start factory network call;
							startFactory(factoryList.get(pos).id);
						}else if(factoryList.get(pos).buttonState==1){
							//Check diamond present, and jewel store space, stop factory
							int sum=0;
							for(int i=3; i<18; i++){
								sum += JewelChatApp.getSharedPref().getInt(i+"",0);
							}

							if( 25-sum >= factoryList.get(pos).amount ) {
								if (JewelChatApp.getSharedPref().getInt("0", 0) >= factoryList.get(pos).diamond)
									stopFactory(factoryList.get(pos).id);
								else
									snackbarMsg("Not enough diamonds...");
							}else
								snackbarMsg("Not enough space in Jewel Store...");
						}else if(factoryList.get(pos).buttonState==2){
							//Jewel Store space,  stop factory
							int sum=0;
							for(int i=3; i<18; i++){
								sum += JewelChatApp.getSharedPref().getInt(i+"",0);
							}

							if(25-sum >= factoryList.get(pos).amount)
								stopFactory(factoryList.get(pos).id);
							else
								snackbarMsg("Not enough space in Jewel Store...");
						}
						break;

					case R.id.flush:
						Log.i("Flush", "Flush");
						flushFactory(factoryList.get(pos).id);
						break;

					default:
						Log.i("click event","Redeem");

				}
			}
		};

		adapterFactory = new FactoryAdapter(getApplicationContext(), mOnItemClickListener, factoryList);
		recyclerView.setAdapter(adapterFactory);

		if (factoryList.size() < 1) {
			loading = true;
			loadFactories();
		}

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				visibleItemCount = recyclerView.getChildCount();
				totalItemCount = mLayoutManager.getItemCount();
				firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

				if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
					loading = true;
					loadFactories();
				}
			}

		});

	}


	@Override
	public void onErrorResponse(VolleyError error) {
		network_call_is_on = false;
		super.onErrorResponse(error);
	}


	private void flushFactory(int id){

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				network_call_is_on = false;
				JewelChatApp.appLog(Log.INFO,"FLUSH","FLUSHFACTORY" + ":onResponse");
				dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					//Intent service1 = new Intent(getApplicationContext(), GameStateLoadService.class);
					//startService(service1);

					factoryList.clear();
					pageCounter = 0;
					loading = true;
					no_more_items_to_load = false;
					loadFactories();



				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}
			}
		};



		JSONObject t = new JSONObject();
		try {
			t.put("id", id);
			//Log.i(">>>NETWORK",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.FLUSHFACTORY, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				createDialog("Please Wait");
				network_call_is_on = true;
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	private void startFactory(int id){

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				network_call_is_on = false;
				JewelChatApp.appLog(Log.INFO,"REDEEMTASK","REDEEMTASK" + ":onResponse");
				dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}


					factoryList.get(pos).buttonState = 1;
					factoryList.get(pos).ct = new CountDownTimer(factoryList.get(pos).duration , 1000) {
						@Override
						public void onTick(long millis) {
							long second = (millis / 1000) % 60;
							long minute = (millis / (1000 * 60)) % 60;
							long hour = (millis / (1000 * 60 * 60)) % 24;

							factoryList.get(pos).time_left = String.format("%02d:%02d:%02d", hour, minute, second);

							//fac.time_left = (long)(l/1000)+":";
							adapterFactory.notifyDataSetChanged();
						}

						@Override
						public void onFinish() {
							factoryList.get(pos).is_on = false;
							factoryList.get(pos).time_left = "DONE";
							adapterFactory.notifyDataSetChanged();
							factoryList.get(pos).buttonState = 2;
						}
					}.start();

					factoryList.get(pos).is_on = true;
					adapterFactory.notifyDataSetChanged();
					Intent service1 = new Intent(getApplicationContext(), GameStateLoadService.class);
					startService(service1);


				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}

			}
		};



		JSONObject t = new JSONObject();
		try {
			t.put("id", id);
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.STARTFACTORY, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				createDialog("Please Wait");
				network_call_is_on = true;
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	private void stopFactory(int id){

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				network_call_is_on = false;
				JewelChatApp.appLog(Log.INFO,"REDEEMTASK","REDEEMTASK" + ":onResponse");
				dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					Intent service1 = new Intent(getApplicationContext(), GameStateLoadService.class);
					startService(service1);

					factoryList.clear();
					pageCounter = 0;
					loading = true;
					no_more_items_to_load = false;
					loadFactories();



				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}
			}
		};



		JSONObject t = new JSONObject();
		try {
			t.put("id", id);
			//Log.i(">>>NETWORK",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.STOPFACTORY, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				createDialog("Please Wait");
				network_call_is_on = true;
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	private void loadFactories() {

		if(no_more_items_to_load)
			return;

		JSONObject t = new JSONObject();
		try {
			t.put("page", pageCounter);
			Log.i(">>>NETWORKFactory",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.GETFACTORIES, t, this, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onResponse(JSONObject response) {

		JewelChatApp.appLog(Log.INFO,"Activity","JewelFactory" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			Log.i("INSIDE Loop", "Inside loop"+error);
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}


			JSONArray list = response.getJSONArray("fac");
			Log.i("INSIDE Loop", "Inside loop"+list.length());
			HashMap<Integer, Factory> hm = new HashMap<Integer, Factory>();

			for(int i=0; i<list.length();i++){

				JSONObject t = list.getJSONObject(i);
				//Factory fac;
				boolean flag = true;

				if(!hm.containsKey(t.getInt("id"))) {

					final Factory fac = new Factory();
					fac.id = t.getInt("id");
					fac.factory_type = t.getInt("factory_type");
					fac.duration = t.optInt("duration")*1000l;
					fac.is_on = t.optInt("is_on")==1?true:false;
					fac.diamond = t.optInt("diamond");
					fac.amount = t.optInt("amount");
					if(fac.is_on) {
						if (t.optString("start_time") != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							Date testDate = null;
							try {
								testDate = sdf.parse(t.optString("start_time"));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							fac.start_time = testDate.getTime();
						}

						if (response.getString("time") != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							Date testDate = null;
							try {
								testDate = sdf.parse(response.getString("time"));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							fac.server_time = testDate.getTime();
						}


						if( fac.server_time < fac.start_time+fac.duration ){
							fac.buttonState = 1;
							fac.ct = new CountDownTimer(fac.duration - (fac.server_time-fac.start_time), 1000) {
								@Override
								public void onTick(long millis) {
									long second = (millis / 1000) % 60;
									long minute = (millis / (1000 * 60)) % 60;
									long hour = (millis / (1000 * 60 * 60)) % 24;

									fac.time_left = String.format("%02d:%02d:%02d", hour, minute, second);

									//fac.time_left = (long)(l/1000)+":";
									adapterFactory.notifyDataSetChanged();
								}

								@Override
								public void onFinish() {
									fac.is_on = false;
									fac.time_left = "DONE";
									adapterFactory.notifyDataSetChanged();
									fac.buttonState = 2;
								}
							}.start();
						}else{
							fac.time_left = "DONE";
							fac.is_on = false;
							fac.buttonState = 2;
						}

					}

					fac.device_time = System.currentTimeMillis();
					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");
					fac.materials.add(m);
					hm.put(fac.id, fac);
				}else{

					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");
					Factory fac = (Factory)hm.get(t.getInt("id"));
					fac.materials.add(m);

					hm.put(fac.id, fac);
				}

			}

			List<Factory> yourList = new ArrayList<Factory>(hm.values());

			Collections.sort(yourList);

			Log.i("FRAGMENT<<<<<", yourList.size()+"");
			if(pageCounter == 0)
				factoryList.clear();

			factoryList.addAll(yourList);
			pageCounter++;
			adapterFactory.notifyDataSetChanged();
			if(yourList.size() == 0)
				no_more_items_to_load = true;

			loading = false;

		} catch (JSONException e) {
			e.printStackTrace();
			FirebaseCrash.report(e);
		} catch (Exception e) {
			e.printStackTrace();
			FirebaseCrash.report(e);
		}

	}

	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		titleText.setText("Jewel Factory");

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}


	@Subscribe
	public void onGameStateChangeEvent( GameStateChangeEvent event) {

		ImageView store_image= 	(ImageView)appbarRoot.findViewById(R.id.jewel_store_image);
		if(event.TOTAL==0) {
			store_image.setImageResource(R.drawable.js_empty);
		}else if(event.TOTAL>0 && event.TOTAL<25){
			store_image.setImageResource(R.drawable.js_half);
		}else if(event.TOTAL == 25){
			store_image.setImageResource(R.drawable.js_full);
		}
		LEVEL.setText(event.LEVEL+"");
		XP.setMax(event.LEVEL_XP);XP.setProgress(event.XP);
		LEVEL_SCORE.setText(event.XP+"/"+event.LEVEL_XP);

	}

	@Subscribe
	public void OnNoInternetEvent( NoInternet event) {
		//showNoInternetDialog();
		noInternet();
	}

	@Subscribe
	public void on_403NetworkErrorEvent( _403NetworkErrorEvent event) {
		show403Dialog();
	}


}
