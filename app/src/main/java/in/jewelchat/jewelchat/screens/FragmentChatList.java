package in.jewelchat.jewelchat.screens;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ChatListAdapter;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class FragmentChatList extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>  {

	private String className;
	private ListView listView;
	private ChatListAdapter chatListAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		className = getClass().getSimpleName();
		JewelChatApp.appLog(className + ":onCreate");
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(1, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		JewelChatApp.appLog(className + ":onCreateView");

		View view = inflater.inflate(R.layout.fragment_chat, container, false);
		listView = (ListView) view.findViewById(R.id.chat);
		chatListAdapter = new ChatListAdapter(getActivity());
		listView.setAdapter(chatListAdapter);

		/*
		this.recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent i = new Intent(getActivity(), ActivityChat.class);
				startActivity(i);

			}
		});
		*/
		//this.listView.setOnItemLongClickListener(this);
		//this.listView.setLongClickable(true);

		return view;
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
