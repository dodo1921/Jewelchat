package in.jewelchat.jewelchat.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.TasksAdapter;
import in.jewelchat.jewelchat.models.Task;
import in.jewelchat.jewelchat.models.TaskMaterials;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
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

			}
		};

		tasksAdapter = new TasksAdapter(getContext(), listTask, mOnItemClickListener);
		recyclerView.setAdapter(tasksAdapter);

		if (listTask.size() < 1) {

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
			Log.i(">>>NETWORK",pageCounter+"");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.GETTASKS, t, this, this);
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

		if(response != null && response.data != null){
			if(response.statusCode == 403){

				SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
				editor.putBoolean(JewelChatPrefs.IS_LOGGED, false);
				editor.putString(JewelChatPrefs.MY_ID, "");
				editor.commit();

				JewelChatApp.getBusInstance().post(new _403NetworkErrorEvent());

			}else if(response.statusCode == 500){

				String json = new String(response.data);
				try{
					JSONObject obj = new JSONObject(json);
					errorMessage = "Please Try Again. Error 500. "+obj.getString("data");
					//makeToast(errorMessage);

				} catch(JSONException e){
					e.printStackTrace();

				}

			}else{

				if (error instanceof TimeoutError || error instanceof NoConnectionError) {
					errorMessage = "Connection Timeout";
				}else{
					errorMessage = "Network Error";
				}

			}
		}

		JewelChatApp.appLog("FragmentTask Volley error:"+errorMessage);
		FirebaseCrash.report(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		JewelChatApp.appLog(Log.INFO,"FRAGMENT","FragmentTasks" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}


			JSONArray list = (JSONArray) response.get("tasks");

			HashMap<Integer, Task> hm = new HashMap<Integer, Task>();

			for(int i=0; i<list.length();i++){

				JSONObject t = (JSONObject)list.get(i);
				Task task;

				if(!hm.containsKey(t.getInt("id"))) {

					task = new Task();
					task.id = t.getInt("id");
					task.task_id = t.getInt("task_id");
					if (t.get("duration") == null)
						task.has_duration = false;
					else {
						task.has_duration = true;
						task.duration = t.getString("duration");
					}

					if (t.get("qty") == null)
						task.has_qty = false;
					else {
						task.has_qty = true;
						task.qty = t.getInt("qty");
					}
					task.coins = t.getInt("coins");
					task.points = t.getInt("points");
					task.money = t.getDouble("money");
					task.done = t.getBoolean("done");

					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");

					task.materials.add(m);
					hm.put(task.id, task);

				}else{

					TaskMaterials m = new TaskMaterials();
					m.jeweltype_id = t.getInt("jeweltype_id");
					m.count = t.getInt("count");
					task = (Task)hm.get(t.getInt("id"));
					task.materials.add(m);

					hm.put(task.id, task);
				}

			}

			List<Task> yourList = new ArrayList<Task>(hm.values());

			Log.i("FRAGMENT", yourList.size()+"");
			if(pageCounter == 0)
				listTask.clear();

			listTask.addAll(yourList);
			pageCounter++;
			tasksAdapter.notifyDataSetChanged();
			if(yourList.size() < 5)
				no_more_items_to_load = true;

			loading = false;

		} catch (JSONException e) {
			FirebaseCrash.report(e);
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}
	}
}
