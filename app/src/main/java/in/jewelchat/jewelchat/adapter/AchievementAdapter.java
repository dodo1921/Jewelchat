package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.Achievement;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class AchievementAdapter  extends RecyclerView.Adapter<AchievementAdapter.MyViewHolder>  {

	private final int HEADER = 0;
	public AchievementAdapter.OnItemClickListener mItemClickListener = null;
	private Context mContext;
	private List<Achievement> achievementList;


	public interface OnItemClickListener {

		void onItemClick(View view, int position);
	}

	public AchievementAdapter(Context context, List<Achievement> achievementList, AchievementAdapter.OnItemClickListener mItemClickListener) {
		this.mContext = mContext;
		this.achievementList = achievementList;
		this.mItemClickListener = mItemClickListener;
	}

	@Override
	public AchievementAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		View view;
		switch (position) {
			case HEADER:
				view = LayoutInflater.from(viewGroup.getContext())
						.inflate(R.layout.fragment_achievement_header, viewGroup, false);
				return new AchievementAdapter.MyViewHolder(view, position);
			default:
				view = LayoutInflater.from(viewGroup.getContext())
						.inflate(R.layout.fragment_achievement_element, viewGroup, false);
				return new AchievementAdapter.MyViewHolder(view, position);
		}
	}

	@Override
	public void onBindViewHolder(AchievementAdapter.MyViewHolder holder, int position) {

		switch (position) {
			case HEADER:
				holder.diamond_count_header.setText(JewelChatApp.getSharedPref().getInt("0",0)+"");
				holder.coin_count.setText(JewelChatApp.getSharedPref().getInt("1",1)+"");
				holder.name.setText(JewelChatApp.getSharedPref().getString(JewelChatPrefs.NAME,""));
				break;
			default:
				int pos = position - 1;
				holder.main_text.setText(achievementList.get(pos).text);
				holder.note.setText(achievementList.get(pos).note);
				holder.loading_details.setEnabled(false);
				if(achievementList.get(pos).diamond==1)
					holder.diamond_count.setText("");
				else
					holder.diamond_count.setText(achievementList.get(pos).diamond+"");
				if(achievementList.get(pos).level > JewelChatApp.getSharedPref().getInt(JewelChatPrefs.LEVEL,0)){
					holder.level_lock.setVisibility(View.VISIBLE);
					holder.check.setEnabled(false);
				}else{
					holder.level_lock.setVisibility(View.GONE);
					holder.check.setEnabled(true);
				}


		}
	}

	@Override
	public int getItemViewType(int position) {
		if (HEADER == position)
			return HEADER;
		return position;
	}



	@Override
	public int getItemCount() {
		return achievementList.size() + 1;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
		public TextView coin_count;
		public TextView diamond_count_header;
		public Button edit_profile;
		public ImageView profile_pic;
		public TextView name;

		public TextView main_text;
		public TextView note;
		public TextView diamond_count;
		public ProgressBar loading_details;
		public Button check;
		public LinearLayout level_lock;
		public TextView level_lock_text;

		public MyViewHolder(View itemView, int pos) {
			super(itemView);
			switch (pos) {
				case HEADER:
					coin_count = (TextView)itemView.findViewById(R.id.coin_count);
					diamond_count_header = (TextView)itemView.findViewById(R.id.diamond_count_header);
					edit_profile = (Button)itemView.findViewById(R.id.edit_profile);
					profile_pic = (ImageView)itemView.findViewById(R.id.profile_pic);
					name = (TextView) itemView.findViewById(R.id.name);
					edit_profile.setOnClickListener(this);
					break;
				default:
					main_text = (TextView) itemView.findViewById(R.id.main_content);
					note = (TextView)itemView.findViewById(R.id.note);
					diamond_count = (TextView) itemView.findViewById(R.id.diamond_count);
					loading_details = (ProgressBar) itemView.findViewById(R.id.loading_details);
					check = (Button) itemView.findViewById(R.id.check);
					check.setOnClickListener(this);
					level_lock = (LinearLayout) itemView.findViewById(R.id.level_lock);
					level_lock_text = (TextView) itemView.findViewById(R.id.level_lock_text);
			}
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(v, getAdapterPosition());
		}
	}
}
