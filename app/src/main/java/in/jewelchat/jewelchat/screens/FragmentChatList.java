package in.jewelchat.jewelchat.screens;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ChatListAdapter;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class FragmentChatList extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>  {

	private String className;
	private ListView listView;
	private ChatListAdapter chatListAdapter;
	private CursorLoader chatListCursorLoader;


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

		this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Cursor c = (Cursor)chatListAdapter.getItem(position);
				c.moveToPosition(position);
				Toast.makeText( getContext() , c.getInt(0)+"", Toast.LENGTH_SHORT).show();

				Bundle bundle = new Bundle();


				//Intent i = new Intent(getActivity(), ActivityChatRoom.class);
				//startActivity(i);

			}
		});

		return view;
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		Uri uri = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ "chatlist");
		if(chatListCursorLoader == null) {
			chatListCursorLoader = new CursorLoader(getActivity(),
					uri, null, null, null, null);
		};
		return chatListCursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		chatListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		chatListAdapter.swapCursor(null);
	}
}
