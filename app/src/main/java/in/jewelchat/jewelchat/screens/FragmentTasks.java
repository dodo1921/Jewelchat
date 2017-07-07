package in.jewelchat.jewelchat.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.TasksAdapter;
import in.jewelchat.jewelchat.models.Task;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class FragmentTasks extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>{

	private String className;
	private RecyclerView recyclerView;
	private TasksAdapter tasksAdapter;
	private TasksAdapter.OnItemClickListener mOnItemClickListener;
	private List<Task> listTask;

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
		recyclerView = (RecyclerView) view.findViewById(R.id.task);
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		tasksAdapter = new TasksAdapter(listTask, mOnItemClickListener, getContext());
		recyclerView.setAdapter(tasksAdapter);

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

			}

		});

		mOnItemClickListener = new TasksAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

			}
		};
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

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {

	}
}
