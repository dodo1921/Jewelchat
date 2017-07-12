package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.jewelchat.jewelchat.JewelChat;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.TasksAdapter;
import in.jewelchat.jewelchat.models.Task;
import in.jewelchat.jewelchat.models.TaskMaterials;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

import static in.jewelchat.jewelchat.R.id.task;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class FragmentTasks extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>{

	private String className;
	private RecyclerView recyclerView;
	private TasksAdapter tasksAdapter;
	private TasksAdapter.OnItemClickListener mOnItemClickListener;
	private List<Task> listTask;
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
		if(listTask == null)
			listTask = new ArrayList<Task>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		JewelChatApp.appLog(className + ":onCreateView");
		View view = inflater.inflate(R.layout.fragment_task, container, false);
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		JewelChatApp.appLog(className + ":onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		recyclerView = (RecyclerView) view.findViewById(task);
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);


		mOnItemClickListener = new TasksAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				pos = position - 1;
				switch (view.getId()) {
					case R.id.task:
						for (Task t:
						     listTask) {
							t.open = false;
						}
						listTask.get(pos).open = true;
						List<TaskMaterials> list = listTask.get(pos).materials;
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
						listTask.get(pos).materials = list;
						listTask.get(pos).redeem = r;
						tasksAdapter.notifyDataSetChanged();
						break;

					case R.id.redeem:
						taskRedeem(listTask.get(pos).id);
						break;
					case R.id.wallet:
						Intent intent1 = new Intent(getActivity(), ActivityWallet.class);
						startActivity(intent1);
						break;
					case R.id.leaderBoard:
						Intent intent2 = new Intent(getActivity(), ActivityLeaderboard.class);
						startActivity(intent2);
						break;
					case R.id.childrenList:
						Intent intent3 = new Intent(getActivity(), ActivityReference.class);
						startActivity(intent3);
						break;
					default:
						Log.i("click event","Redeem");

				}
			}
		};

		tasksAdapter = new TasksAdapter(getContext(), listTask, mOnItemClickListener);
		recyclerView.setAdapter(tasksAdapter);

		if (listTask.size() < 1) {
			loading = true;
			loadAchivements();
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
					loadAchivements();
				}
			}

		});


	}

	private void taskRedeem(int id) {

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO,"REDEEMTASK","REDEEMTASK" + ":onResponse");
				((JewelChat)getActivity()).dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					((JewelChat)getActivity()).dismissDialog();
					pageCounter = 0;
					listTask.clear();
					tasksAdapter.notifyDataSetChanged();
					loading = true;
					Intent service1 = new Intent(getContext(), GameStateLoadService.class);
					getActivity().startService(service1);
					loadAchivements();

					AlertDialog completed = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme)).create();
					completed.setTitle("Congratulation");
					completed.setMessage("You have successfully completed the task.");
					completed.setCancelable(true);
					completed.show();

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
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.REDEEMTASK, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				((JewelChat)getActivity()).createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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

	private void loadAchivements() {

		if(no_more_items_to_load)
			return;

		JSONObject t = new JSONObject();
		try {
			t.put("page", pageCounter);
			Log.i(">>>NETWORKTASK",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.GETTASKS, t, this, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
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
		}else if (error instanceof TimeoutError ) {
			errorMessage = "Connection Timeout";
		}else if(error instanceof NoConnectionError){
			errorMessage = "No Connection Error";
		}else{
			errorMessage = "Network Error";
		}

		((JewelChat)getActivity()).dismissDialog();
		((JewelChat) getActivity()).networkErrorMessage(errorMessage);
		JewelChatApp.appLog(Log.ERROR,"FragmentAchievement","FragmentAchievement Volley error:"+errorMessage);
		FirebaseCrash.report(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		JewelChatApp.appLog(Log.INFO,"FRAGMENT","FragmentTasks" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			Log.i("INSIDE Loop", "Inside loop"+error);
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}


			JSONArray list = response.getJSONArray("tasks");

			HashMap<Integer, Task> hm = new HashMap<Integer, Task>();

			for(int i=0; i<list.length();i++){

				JSONObject t = list.getJSONObject(i);
				Task task;
				boolean flag = true;

				if(!hm.containsKey(t.getInt("id"))) {

					task = new Task();
					task.id = t.getInt("id");
					task.task_id = t.getInt("task_id");
					if (t.opt("duration").toString().equals("null"))
						task.has_duration = false;
					else {
						task.has_duration = true;
						task.duration = t.getString("duration");
					}

					if (t.opt("qty").toString().equals("null"))
						task.has_qty = false;
					else {
						task.has_qty = true;
						task.qty = t.getInt("qty");
						if(task.qty == 0)
							flag=false;
					}
					task.coins = t.getInt("coins");
					task.points = t.getInt("points");

					task.show_money = t.optInt("show_money")==1?true:false;
					task.money = t.getDouble("money");
					if(!task.show_money || task.money == 0.00 )
						task.color = R.color.green;
					else if(task.money > 10.00)
						task.color = R.color.red;
					else
						task.color = R.color.yellow;

					task.done = t.optInt("done")==1?true:false;
					if(task.done)
						flag=false;
					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");

					task.materials.add(m);
					task.redeem = false;

					task.level = t.getInt("level");

					//if(task.level>JewelChatApp.getSharedPref().getInt(JewelChatPrefs.LEVEL,0))
					//		flag=false;
					if(flag)
						hm.put(task.id, task);

				}else{
					Log.i(">>>>>TASK", "OLD"+t.getInt("id"));
					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");
					task = (Task)hm.get(t.getInt("id"));
					task.materials.add(m);

					hm.put(task.id, task);
				}

			}

			List<Task> yourList = new ArrayList<Task>(hm.values());

			Log.i("FRAGMENT<<<<<", yourList.size()+"");
			if(pageCounter == 0)
				listTask.clear();

			listTask.addAll(yourList);
			pageCounter++;
			tasksAdapter.notifyDataSetChanged();
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
}
