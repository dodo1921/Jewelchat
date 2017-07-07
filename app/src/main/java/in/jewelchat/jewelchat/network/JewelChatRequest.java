package in.jewelchat.jewelchat.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.jewelchat.jewelchat.JewelChatApp;

/**
 * Created by mayukhchakraborty on 24/02/16.
 */
public class JewelChatRequest extends JsonObjectRequest {

	public JewelChatRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
	                       Response.ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
		this.setTag("JewelChat");
		RetryPolicy policy = new RetryPolicy() {
			@Override
			public int getCurrentTimeout() {
				return JewelChatApp.CONNECTION_TIMEOUT;
			}

			@Override
			public int getCurrentRetryCount() {
				return 0;
			}

			@Override
			public void retry(VolleyError error) throws VolleyError {
				throw error;
			}
		};
		this.setRetryPolicy(policy);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

		Map<String, String> headers = response.headers;
		String cookie = (String) headers.get("jc-cookie");
		JewelChatApp.saveCookie(cookie);
		return super.parseNetworkResponse(response);

	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {

		Map<String, String> headers = new HashMap<String, String>();
		if(!JewelChatApp.getCookie().equals("")){
			headers.put("Cookie", JewelChatApp.getCookie());
			Log.i("GETHEADER", JewelChatApp.getCookie());
		}
		return headers;

	}

}
