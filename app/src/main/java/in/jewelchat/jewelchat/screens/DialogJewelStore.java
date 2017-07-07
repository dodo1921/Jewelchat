package in.jewelchat.jewelchat.screens;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;

/**
 * Created by mayukhchakraborty on 30/03/16.
 */
public class DialogJewelStore extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_jewel_store, null);

		ProgressBar jewel_count = (ProgressBar) view.findViewById(R.id.animate_progress_bar);
		int sum=0;
		for(int i=3; i<18; i++){
			sum += JewelChatApp.getSharedPref().getInt(i+"",0);
			TextView t = (TextView) view.findViewById(help("t"+i));
			t.setText(JewelChatApp.getSharedPref().getInt(i+"",0)+"");
			//t.setText("5");
		}
		Log.i("SUM", sum+"");
		jewel_count.setProgress(sum);
		jewel_count.setMax(25);
		builder.setView(view);

		return builder.create();
	}


	private int help(String t){

		if(t.equals("t3"))
			return R.id.t3;
		else if(t.equals("t4"))
			return R.id.t4;
		else if(t.equals("t5"))
			return R.id.t5;
		else if(t.equals("t6"))
			return R.id.t6;
		else if(t.equals("t7"))
			return R.id.t7;
		else if(t.equals("t8"))
			return R.id.t8;
		else if(t.equals("t9"))
			return R.id.t9;
		else if(t.equals("t10"))
			return R.id.t10;
		else if(t.equals("t11"))
			return R.id.t11;
		else if(t.equals("t12"))
			return R.id.t12;
		else if(t.equals("t13"))
			return R.id.t13;
		else if(t.equals("t14"))
			return R.id.t14;
		else if(t.equals("t15"))
			return R.id.t15;
		else if(t.equals("t16"))
			return R.id.t16;
		else
			return R.id.t17;


	}




}
