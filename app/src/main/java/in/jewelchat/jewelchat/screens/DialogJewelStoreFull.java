package in.jewelchat.jewelchat.screens;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import in.jewelchat.jewelchat.R;

/**
 * Created by mayukhchakraborty on 04/04/16.
 */
public class DialogJewelStoreFull extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		View view = inflater.inflate(R.layout.dialog_jewel_store, null);

		initialise();

		builder.setView(view);

		return builder.create();
	}

	private void initialise() {



	}
}

