package in.jewelchat.jewelchat.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.JewelChat;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.network.JewelChatRequest;

import static in.jewelchat.jewelchat.R.id.reference;

/**
 * Created by mayukhchakraborty on 08/06/17.
 */

public class ActivityInitialDetails extends BaseNetworkActivity implements TextView.OnEditorActionListener,   Response.Listener<JSONObject> {

	private EditText enterName;
	private String name;
	private EditText enterRef;
	private Button submit;

	private String refph;
	private String e164formatNumber = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_details);
		enterName = (EditText) findViewById(R.id.name);
		enterName.setOnEditorActionListener(this);
		enterRef = (EditText) findViewById(reference);
		enterRef.setOnClickListener(this);
		className = getClass().getSimpleName();
		//rootLayout = (LinearLayout) findViewById(R.id.verify_root);
		submit = (Button) findViewById(R.id.next_continue);
		submit.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		Log.i("<<<<<<<<InitialDetail", ">>>>");
		if (v.getId() == R.id.next_continue)
				action();
	}

	private boolean action() {
		Log.i(">>>>>InitialDetail", ">>>>");
		JewelChatApp.appLog(className + ":action");
		name = enterName.getText().toString().trim();
		if(name.length() == 0){
			makeToast("Please enter your name");
			return false;
		}

		refph = enterRef.getText().toString().trim();
		Phonenumber.PhoneNumber pn;
		PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		try {
			if(!refph.equals("")) {
				pn = phoneNumberUtil.parse(refph, "IN");
				e164formatNumber = phoneNumberUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
			}
		} catch (NumberParseException e) {
			makeToast(getString(R.string.error_msg_number_length_zero));
			return false;
		}

		if (refph.length() == 10 && !e164formatNumber.equals(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_PHONE, "")) ) {
			createDialog(getString(R.string.please_wait));
			Map<String, String> jsonParams = new HashMap<String, String>();
			jsonParams.put("reference", e164formatNumber);
			jsonParams.put("name", name);
			JewelChatRequest request = new JewelChatRequest(Request.Method.POST,
					JewelChatURLS.INITIAL_DETAILS, new JSONObject(jsonParams), this, this);
			addRequest(request);
			return true;
		} else {
			createDialog(getString(R.string.please_wait));
			Map<String, String> jsonParams = new HashMap<String, String>();
			jsonParams.put("name", name);
			JewelChatRequest request = new JewelChatRequest(Request.Method.POST,
					JewelChatURLS.INITIAL_DETAILS, new JSONObject(jsonParams), this, this);
			addRequest(request);
			return true;
		}

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

			SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
			editor.putBoolean(JewelChatPrefs.INITIAL_DETAILS_ENTERED,true);
			editor.putString(JewelChatPrefs.NAME, response.getString("name"));
			editor.apply();

			Intent intent = new Intent(getApplicationContext(), JewelChat.class);
			hideKeyBoard();
			dismissDialog();
			startActivity(intent);
			finish();

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

	private void hideKeyBoard() {
		try {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(enterName.getWindowToken(), 0);
			manager.hideSoftInputFromWindow(enterRef.getWindowToken(), 0);
		} catch (Exception e) {
			JewelChatApp.appLog(getClass().getSimpleName() + ":" + e.toString());
		}
	}

	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
		return ((actionId == EditorInfo.IME_ACTION_DONE) && action());
	}
}
