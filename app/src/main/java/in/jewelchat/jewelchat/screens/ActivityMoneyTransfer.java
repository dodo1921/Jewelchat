package in.jewelchat.jewelchat.screens;

import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;

import org.json.JSONObject;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.R;

/**
 * Created by mayukhchakraborty on 23/06/17.
 */

public class ActivityMoneyTransfer extends BaseNetworkActivity implements Response.Listener<JSONObject>{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		className = getClass().getSimpleName();


	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onResponse(JSONObject response) {

	}
}
