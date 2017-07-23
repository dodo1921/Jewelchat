package in.jewelchat.jewelchat.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.jewelchat.jewelchat.JewelChat;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.AchievementAdapter;
import in.jewelchat.jewelchat.models.Achievement;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class FragmentAchievements extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>{

	private String className;
	private RecyclerView recyclerView;
	private AchievementAdapter achievementAdapter;
	private AchievementAdapter.OnItemClickListener mOnItemClickListener;
	private List<Achievement> achivementList;
	private int previousTotal = 0;
	private int firstVisibleItem = 0;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private boolean loading = true;
	private int pageCounter = 0;
	private boolean no_more_items_to_load = false;
	private int pos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		className = getClass().getSimpleName();
		JewelChatApp.appLog(className + ":onCreate");
		super.onCreate(savedInstanceState);

		if(achivementList == null)
			achivementList = new ArrayList<Achievement>();




	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		JewelChatApp.appLog(className + ":onCreateView");
		View view = inflater.inflate(R.layout.fragment_achievement, container, false);
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		JewelChatApp.appLog(className + ":onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		recyclerView = (RecyclerView) view.findViewById(R.id.achivement);
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);

		mOnItemClickListener = new AchievementAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

				pos = position - 1;
				switch (view.getId()) {
					case R.id.check:
						checkAchievement(achivementList.get(pos).id);
						break;
					case R.id.edit_profile:
						Intent intent = new Intent(getActivity(), ActivityEditProfile.class);
						//intent.putExtra("image_url", contestList.get(pos).contest.small_picture);
						startActivity(intent);
						break;

				}

			}
		};

		achievementAdapter = new AchievementAdapter(getContext(), achivementList, mOnItemClickListener);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(achievementAdapter);

		if (achivementList.size() < 1) {

			loadAchivements();
		}

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				visibleItemCount = recyclerView.getChildCount();
				totalItemCount = mLayoutManager.getItemCount();
				firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
				/*
				if (loading) {
					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;
					}
				}
				*/
				if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
					loading = true;
					loadAchivements();
				}
			}

		});


	}


	@Subscribe
	public void onGameStateChangeEvent( GameStateChangeEvent event) {
		achievementAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.i("Task>>>","onResume");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i("Fragment>>>", "TASK Destroyed");

	}



	private void checkAchievement(int id){


		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO,"CHECKACHIEVEMENT","CheckAchievement" + ":onResponse");
				((JewelChat)getActivity()).dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}
					int d = achivementList.get(pos).diamond;
					if(response.getInt("percent")<100) {

						Achievement a = achivementList.get(pos);
						a.progress_enabled = true;
						a.progress = response.getInt("percent");

						achivementList.set(pos, a);
						achievementAdapter.notifyDataSetChanged();
						((JewelChat)getActivity()).dismissDialog();

					}else{
						((JewelChat)getActivity()).dismissDialog();
						pageCounter = 0;
						achivementList.clear();
						achievementAdapter.notifyDataSetChanged();
						loading = true;
						Intent service1 = new Intent(getContext(), GameStateLoadService.class);
						getActivity().startService(service1);
						loadAchivements();
						int new_count = JewelChatApp.getSharedPref().getInt("0",0) + d;
						JewelChatApp.getSharedPref().edit().putInt("0",new_count).apply();

						AlertDialog completed = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme)).create();
						completed.setTitle("Congratulation");
						completed.setMessage("You have won "+d+" diamond.");
						completed.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						completed.show();
					}



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
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.REDEEMACHIEVEMENT, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				((JewelChat)getActivity()).createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}


	private void loadAchivements(){

		if(no_more_items_to_load)
			return;

		JSONObject t = new JSONObject();
		try {
			t.put("page", pageCounter);
			Log.i(">>>NETWORK",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.GETACHIEVEMENTS, t, this, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED)
				JewelChatApp.getRequestQueue().add(req);
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onErrorResponse(VolleyError error) {
		String errorMessage = error.toString();
		NetworkResponse response = error.networkResponse;

		if (response != null && response.data != null) {
			if (response.statusCode == 403) {

				SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
				editor.putBoolean(JewelChatPrefs.IS_LOGGED, false);
				editor.putString(JewelChatPrefs.MY_ID, "");
				editor.commit();

				JewelChatApp.getBusInstance().post(new _403NetworkErrorEvent());

			} else if (response.statusCode == 500) {

				String json = new String(response.data);
				try {
					JSONObject obj = new JSONObject(json);
					errorMessage = obj.getString("data");
					//makeToast(errorMessage);

				} catch (JSONException e) {
					e.printStackTrace();

				}

			}
		}

		if (error instanceof TimeoutError ) {
			errorMessage = "Connection Timeout";
		}else if(error instanceof NoConnectionError){
			errorMessage = "No Connection Error";
		}

		((JewelChat)getActivity()).dismissDialog();
		((JewelChat) getActivity()).networkErrorMessage(errorMessage);
		JewelChatApp.appLog(Log.ERROR,"FragmentAchievement","FragmentAchievement Volley error:"+errorMessage);
		FirebaseCrash.report(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		JewelChatApp.appLog(Log.INFO,"FRAGMENT","FragmentAchievements" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}


			JSONArray list = (JSONArray) response.get("achievements");
			Type listType = new TypeToken<List<Achievement>>() {}.getType();
			Gson gson = new Gson();
			List<Achievement> yourList = gson.fromJson(list.toString(), listType);
			Log.i("FRAGMENT", yourList.size()+"");
			if(pageCounter == 0)
				achivementList.clear();

			achivementList.addAll(yourList);
			pageCounter++;
			achievementAdapter.notifyDataSetChanged();
			if(yourList.size() < 9)
				no_more_items_to_load = true;

			loading = false;

		} catch (JSONException e) {
			FirebaseCrash.report(e);
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}
	}


}
