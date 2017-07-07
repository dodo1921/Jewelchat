package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.Task;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder>  {

	private final int HEADER = 0;
	public OnItemClickListener mItemClickListener = null;
	private Context mContext;
	private List<Task> taskList;

	public TasksAdapter(List<Task> listTask, OnItemClickListener mItemClickListener, Context context) {
		this.mContext = context;
		this.taskList = listTask;
		this.mItemClickListener = mItemClickListener;
	}

	public interface OnItemClickListener {

		void onItemClick(View view, int position);
	}


	@Override
	public TasksAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

		View view;
		switch (position) {
			case HEADER:
				view = LayoutInflater.from(viewGroup.getContext())
						.inflate(R.layout.fragment_tasks_header, viewGroup, false);
				return new MyViewHolder(view, position);
			default:
				view = LayoutInflater.from(viewGroup.getContext())
						.inflate(R.layout.fragment_tasks_element, viewGroup, false);
				return new MyViewHolder(view, position);
		}

	}

	@Override
	public void onBindViewHolder(TasksAdapter.MyViewHolder viewholder, int position) {

		switch (position) {
			case HEADER:
				break;
			default:
				int pos = position - 1;
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
		return taskList.size() + 1;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public TextView karma_user_name;
		public TextView total_karma;
		public ImageView userAvatar;
		public ImageView leaderBoard;


		public TextView contest_name;
		public TextView contest_text;
		public TextView contest_total_karma;
		public TextView contest_winners_count;
		public Button redeem_offer;

		public MyViewHolder(View itemView, int pos) {
			super(itemView);
			/*switch (pos) {
				case HEADER:
					leaderBoard.setOnClickListener(this);
					userAvatar.setOnClickListener(this);
					break;
				default:
					contest_name = (TextView) itemView.findViewById(R.id.contest_name);
					contest_text = (TextView) itemView.findViewById(R.id.contest_text);
					contest_image = (SquareImageView) itemView.findViewById(R.id.contest_image);
					contest_total_karma = (TextView) itemView.findViewById(R.id.contest_total_karma);
					contest_winners_count = (TextView) itemView.findViewById(R.id.contest_winners_count);
					redeem_offer = (Button) itemView.findViewById(R.id.redeem_offer);
					redeem_offer.setOnClickListener(this);
					contest_image.setOnClickListener(this);
			}*/
		}


		public void onClick(View v) {
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(v, getAdapterPosition());
		}
	}

}
