package in.jewelchat.jewelchat.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.network.JewelChatRequest;

/**
 * Created by mayukhchakraborty on 28/02/16.
 */
public class ActivityRegistration extends BaseNetworkActivity implements TextView.OnEditorActionListener, Response.Listener<JSONObject>, Html.ImageGetter {

	private EditText enterNumber;
	private String phoneNumber;
	private String requestTag = "mobile";
	private String e164formatNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		rootLayout = (LinearLayout) findViewById(R.id.mobile_entry_root_layout);
		rootLayout.findViewById(R.id.btn_continue).setOnClickListener(this);
		enterNumber = (EditText) rootLayout.findViewById(R.id.mobile_entry);
		enterNumber.setOnEditorActionListener(this);

		TextView textView = (TextView) rootLayout.findViewById(R.id.terms);
		textView.setClickable(true);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		String text = "<a href='http://cititalk.in/TermsofService.pdf'> Terms and Conditions </a>";
		Spanned spanned;
		if (Build.VERSION.SDK_INT >= 24) {
			spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT); // for 24 api and more
		} else {
			spanned = Html.fromHtml(text); // or for older api
		}
		//Spanned spanned = Html.fromHtml(text);
		textView.setText(spanned);

	}

	@Override
	protected void onDestroy() {
		dismissDialog();
		JewelChatApp.getRequestQueue().cancelAll(requestTag);
		super.onDestroy();
	}

	@Override
	protected void setUpAppbar() {


	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_continue)
			action();

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return ((actionId == EditorInfo.IME_ACTION_NEXT) && action());
	}


	private boolean action() {
		JewelChatApp.appLog(className + ":action");
		phoneNumber = enterNumber.getText().toString().trim();


		Phonenumber.PhoneNumber pn;
		PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		try {
			pn = phoneNumberUtil.parse(phoneNumber, "IN");
		} catch (NumberParseException e) {
			makeToast(getString(R.string.error_msg_number_length_zero));
			return false;
		}

		e164formatNumber = phoneNumberUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
		if (phoneNumber.length() == 10 ) {
			createDialog(getString(R.string.please_wait));
			Map<String, String> jsonParams = new HashMap<String, String>();
			jsonParams.put("phone", e164formatNumber);
			Log.i(">>>>", "action:omg "+e164formatNumber);
			JewelChatRequest request = new JewelChatRequest(Request.Method.POST,
					JewelChatURLS.REGISTRATION_URL, new JSONObject(jsonParams), this, this);
			addRequest(request);
			return true;
		} else if (phoneNumber.length() == 0) {
			makeToast(getString(R.string.error_msg_number_length_zero));
			return false;
		} else if (phoneNumber.length() != 10) {
			makeToast(getString(R.string.error_msg_number_length_not_ten));
			return false;
		} else {
			makeToast(getString(R.string.error_default_entry_app));
			return false;
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
			Integer userId = response.getInt("userId");
			Boolean active = response.getBoolean("active");
			SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
			editor.putLong(JewelChatPrefs.MY_ID, userId);
			editor.putString(JewelChatPrefs.NAME, response.getString("name"));
			editor.putBoolean(JewelChatPrefs.INITIAL_DETAILS_ENTERED, active);
			editor.putString(JewelChatPrefs.MY_PHONE, this.e164formatNumber);
			editor.apply();
			Intent intent = new Intent(getApplicationContext(), ActivityVerificationCode.class);
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
			manager.hideSoftInputFromWindow(enterNumber.getWindowToken(), 0);
		} catch (Exception e) {
			JewelChatApp.appLog(getClass().getSimpleName() + ":" + e.toString());
		}
	}

	@Override
	public Drawable getDrawable(String s) {
		return null;
	}
}
