package in.jewelchat.jewelchat.screens;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.FactoryAdapter;
import in.jewelchat.jewelchat.models.Factory;
import in.jewelchat.jewelchat.models.Factory_material;
import in.jewelchat.jewelchat.network.JewelChatRequest;

/**
 * Created by mayukhchakraborty on 16/06/17.
 */

public class ActivityJewelFactories extends BaseNetworkActivity implements Response.Listener<JSONObject>{


	private RecyclerView recyclerView;
	private FactoryAdapter adapter;
	private FactoryAdapter.OnItemClickListener mOnItemClickListener;
	private List<Factory> factoryList;

	private boolean loadmore = true;

	private int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_factories);

		createDialog(getString(R.string.please_wait));
		Map<String, Integer> jsonParams = new HashMap<>();
		jsonParams.put("page", page);
		JewelChatRequest request = new JewelChatRequest(Request.Method.POST,
				JewelChatURLS.GETFACTORIES, new JSONObject(jsonParams), this, this);

		recyclerView = (RecyclerView) findViewById(R.id.factory_list);

		factoryList = new ArrayList<Factory>();
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerView.setLayoutManager(mLayoutManager);

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				if(dy>625*(page+1) && loadmore){

					loadmorefactories();

				}
			}
		});


		mOnItemClickListener = new FactoryAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

			}
		};

		adapter = new FactoryAdapter(this, mOnItemClickListener, factoryList);

		recyclerView.setAdapter(adapter);

	}

	public void loadmorefactories(){
		Map<String, Integer> jsonParams = new HashMap<>();
		jsonParams.put("page", page);
		JewelChatRequest request = new JewelChatRequest(Request.Method.POST,
				JewelChatURLS.GETFACTORIES, new JSONObject(jsonParams), this, this);
	}


	@Override
	public void onClick(View view) {

	}

	@Override
	public void onResponse(JSONObject response) {

		JewelChatApp.appLog(className + ":onResponse");
		try {
			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}

			JSONArray fac = response.getJSONArray("fac");

			HashMap<Integer, Factory> fac_map = new HashMap<Integer, Factory>();

			for(int i=0; i< fac.length(); i++){
				JSONObject faci = fac.getJSONObject(i);
				Integer id = faci.getInt("id");
				Factory f = fac_map.get(id);
				if(f == null){
					Factory_material met = new Factory_material(faci.getInt("jeweltype_id"), faci.getInt("count"), false);
					ArrayList<Factory_material> metlist = new ArrayList<Factory_material>();
					metlist.add(met);
					f = new Factory(false, faci.getInt("id"),
							faci.getInt("factory_type"),
							faci.getInt("level"),
							faci.getInt("duration"),
							faci.getInt("is_on"),
							faci.getString("start_time"),
							metlist);
				}else{
					Factory_material met = new Factory_material(faci.getInt("jeweltype_id"), faci.getInt("count"), false);
					f.materials.add(met);
				}
			}

			for (Map.Entry<Integer, Factory> entry : fac_map.entrySet())
			{
				factoryList.add(entry.getValue());
			}

			if(fac_map.keySet().size() < 5)
				loadmore = false;
			else if(fac_map.keySet().size() == 5)
				loadmore = true;

			if( page == 0 )
				dismissDialog();

			page++;

			adapter.notifyDataSetChanged();


		} catch (JSONException e) {
			dismissDialog();
			makeToast("Please try again");
			FirebaseCrash.report(e);
		} catch (Exception e) {
			dismissDialog();
			makeToast("Please try again");
			FirebaseCrash.report(e);
		}

	}


}
