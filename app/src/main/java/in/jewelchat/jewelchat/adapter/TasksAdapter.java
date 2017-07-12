package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.Task;
import in.jewelchat.jewelchat.models.TaskMaterials;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder>  {

	private final int HEADER = 0;
	public OnItemClickListener mItemClickListener = null;
	private Context mContext;
	private List<Task> taskList;

	public TasksAdapter(Context context, List<Task> listTask, OnItemClickListener mItemClickListener ) {
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
	public void onBindViewHolder(TasksAdapter.MyViewHolder holder, int position) {

		switch (position) {
			case HEADER:
				break;
			default:
				int pos = position - 1;
				Log.i("LEVEL",taskList.get(pos).level+"");
				if(taskList.get(pos).level> JewelChatApp.getSharedPref().getInt(JewelChatPrefs.LEVEL,0)){
					holder.level_lock.setVisibility(View.VISIBLE);
					holder.level_lock_text.setText("LEVEL "+taskList.get(pos).level);
				}else
					holder.level_lock.setVisibility(View.GONE);

				holder.task.setBackgroundColor(mContext.getResources().getColor(taskList.get(pos).color));
				holder.points.setText(taskList.get(pos).points+" points");
				holder.coins.setText(taskList.get(pos).coins+" coins");
				if(taskList.get(pos).show_money && taskList.get(pos).money>0){
					holder.money_row.setVisibility(View.VISIBLE);
					if(taskList.get(pos).money<1.00){
						holder.money.setText(((int)(taskList.get(pos).money*100.00)) + " paise");
					}else if(taskList.get(pos).money==1.00){
						holder.money.setText(((int)(taskList.get(pos).money)) + " rupee");
					}else {
						holder.money.setText(((double)(taskList.get(pos).money)) + " rupees");
					}
				}else{
					holder.money_row.setVisibility(View.GONE);
				}

				if(taskList.get(pos).has_qty){
					if(taskList.get(pos).qty>0) {
						holder.qty.setVisibility(View.VISIBLE);
						holder.qty.setText("Qty:"+taskList.get(pos).qty+"  ");
					}else{
						holder.qty.setVisibility(View.GONE);
					}

				}else{
					holder.qty.setVisibility(View.GONE);
				}

				if(taskList.get(pos).has_duration){
					holder.time_left.setVisibility(View.VISIBLE);
					holder.time_left.setText("Expires at :"+taskList.get(pos).duration);
				}else{
					holder.time_left.setVisibility(View.GONE);
				}

				if(taskList.get(pos).open)
					holder.task_details.setVisibility(View.VISIBLE);
				else
					holder.task_details.setVisibility(View.GONE);

				//holder.level_lock.setVisibility(View.GONE);

				holder.line1.setVisibility(View.GONE); holder.tick1.setVisibility(View.INVISIBLE);
				holder.line2.setVisibility(View.GONE); holder.tick2.setVisibility(View.INVISIBLE);
				holder.line3.setVisibility(View.GONE); holder.tick3.setVisibility(View.INVISIBLE);
				holder.line4.setVisibility(View.GONE); holder.tick4.setVisibility(View.INVISIBLE);
				holder.line5.setVisibility(View.GONE); holder.tick5.setVisibility(View.INVISIBLE);

				List<TaskMaterials> m = taskList.get(pos).materials;
				for(int i=0; i<m.size();i++){
					if(i==0){
						holder.line1.setVisibility(View.VISIBLE);
						holder.quantity1.setText(JewelChatApp.getSharedPref().getInt(m.get(i).jeweltype_id+"",0)+"/"+m.get(i).count+"");
						holder.material1.setImageResource(getJewelDrawable(m.get(i).jeweltype_id));
						if(m.get(i).tick)
							holder.tick1.setVisibility(View.VISIBLE);
						else
							holder.tick1.setVisibility(View.INVISIBLE);
					}else if(i==1){
						holder.line2.setVisibility(View.VISIBLE);
						holder.quantity2.setText(JewelChatApp.getSharedPref().getInt(m.get(i).jeweltype_id+"",0)+"/"+m.get(i).count+"");
						holder.material2.setImageResource(getJewelDrawable(m.get(i).jeweltype_id));
						if(m.get(i).tick)
							holder.tick2.setVisibility(View.VISIBLE);
						else
							holder.tick2.setVisibility(View.INVISIBLE);
					}else if(i==2){
						holder.line3.setVisibility(View.VISIBLE);
						holder.quantity3.setText(JewelChatApp.getSharedPref().getInt(m.get(i).jeweltype_id+"",0)+"/"+m.get(i).count+"");
						holder.material3.setImageResource(getJewelDrawable(m.get(i).jeweltype_id));
						if(m.get(i).tick)
							holder.tick3.setVisibility(View.VISIBLE);
						else
							holder.tick3.setVisibility(View.INVISIBLE);
					}else if(i==3){
						holder.line4.setVisibility(View.VISIBLE);
						holder.quantity4.setText(JewelChatApp.getSharedPref().getInt(m.get(i).jeweltype_id+"",0)+"/"+m.get(i).count+"");
						holder.material4.setImageResource(getJewelDrawable(m.get(i).jeweltype_id));
						if(m.get(i).tick)
							holder.tick4.setVisibility(View.VISIBLE);
						else
							holder.tick4.setVisibility(View.INVISIBLE);
					}else if(i==4){
						holder.line5.setVisibility(View.VISIBLE);
						holder.quantity5.setText(JewelChatApp.getSharedPref().getInt(m.get(i).jeweltype_id+"",0)+"/"+m.get(i).count+"");
						holder.material5.setImageResource(getJewelDrawable(m.get(i).jeweltype_id));
						if(m.get(i).tick)
							holder.tick5.setVisibility(View.VISIBLE);
						else
							holder.tick5.setVisibility(View.INVISIBLE);
					}
				}

				holder.redeem.setEnabled(taskList.get(pos).redeem);


		}

	}

	private int getJewelDrawable(int aid){
		if(aid == 3)
			return R.drawable.t3;
		else if(aid == 4)
			return R.drawable.t4;
		else if(aid == 5)
			return R.drawable.t5;
		else if(aid == 6)
			return R.drawable.t6;
		else if(aid == 7)
			return R.drawable.t7;
		else if(aid == 8)
			return R.drawable.t8;
		else if(aid == 9)
			return R.drawable.t9;
		else if(aid == 10)
			return R.drawable.t10;
		else if(aid == 11)
			return R.drawable.t11;
		else if(aid == 12)
			return R.drawable.t12;
		else if(aid == 13)
			return R.drawable.t13;
		else if(aid == 14)
			return R.drawable.t14;
		else if(aid == 15)
			return R.drawable.t15;
		else if(aid == 16)
			return R.drawable.t16;
		else if(aid == 17)
			return R.drawable.t17;
		else if(aid == 0)
			return R.drawable.diamond_small;
		else if(aid == 1)
			return R.drawable.coin;
			return 0;
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
		public ImageView wallet;
		public ImageView leaderBoard;
		public ImageView childrenList;
		public Button factory;


		public RelativeLayout task;
		public TextView points;
		public TextView coins;
		public LinearLayout money_row;
		public TextView money;
		public TextView time_left;
		public TextView qty;
		public LinearLayout level_lock;
		public TextView level_lock_text;
		public LinearLayout task_details;

		public RelativeLayout line1;
		public TextView quantity1;
		public ImageView material1;
		public ImageView tick1;
		public RelativeLayout line2;
		public TextView quantity2;
		public ImageView material2;
		public ImageView tick2;
		public RelativeLayout line3;
		public TextView quantity3;
		public ImageView material3;
		public ImageView tick3;
		public RelativeLayout line4;
		public TextView quantity4;
		public ImageView material4;
		public ImageView tick4;
		public RelativeLayout line5;
		public TextView quantity5;
		public ImageView material5;
		public ImageView tick5;

		public Button redeem;

		public MyViewHolder(View itemView, int pos) {
			super(itemView);
			switch (pos) {
				case HEADER:
					wallet = (ImageView)itemView.findViewById(R.id.wallet);
					leaderBoard = (ImageView)itemView.findViewById(R.id.leaderBoard);
					childrenList = (ImageView)itemView.findViewById(R.id.childrenList);
					factory = (Button)itemView.findViewById(R.id.factory);
					wallet.setOnClickListener(this);
					leaderBoard.setOnClickListener(this);
					childrenList.setOnClickListener(this);
					factory.setOnClickListener(this);
					break;
				default:
					task = (RelativeLayout)itemView.findViewById(R.id.task);
					task.setOnClickListener(this);
					points = (TextView)itemView.findViewById(R.id.points);
					coins = (TextView)itemView.findViewById(R.id.coins);
					money_row = (LinearLayout)itemView.findViewById(R.id.money_row);
					money = (TextView)itemView.findViewById(R.id.money);
					time_left = (TextView)itemView.findViewById(R.id.time_left);
					qty = (TextView)itemView.findViewById(R.id.qty);
					level_lock = (LinearLayout)itemView.findViewById(R.id.level_lock);
					level_lock_text = (TextView)itemView.findViewById(R.id.level_lock_text);
					task_details = (LinearLayout)itemView.findViewById(R.id.task_details);

					line1 = (RelativeLayout)itemView.findViewById(R.id.line1);
					quantity1 = (TextView) itemView.findViewById(R.id.quantity1);
					material1 = (ImageView)itemView.findViewById(R.id.material1);
					tick1 = (ImageView)itemView.findViewById(R.id.tick1);

					line2 = (RelativeLayout)itemView.findViewById(R.id.line2);
					quantity2 = (TextView) itemView.findViewById(R.id.quantity2);
					material2 = (ImageView)itemView.findViewById(R.id.material2);
					tick2 = (ImageView)itemView.findViewById(R.id.tick2);

					line3 = (RelativeLayout)itemView.findViewById(R.id.line3);
					quantity3 = (TextView) itemView.findViewById(R.id.quantity3);
					material3 = (ImageView)itemView.findViewById(R.id.material3);
					tick3 = (ImageView)itemView.findViewById(R.id.tick3);

					line4 = (RelativeLayout)itemView.findViewById(R.id.line4);
					quantity4 = (TextView) itemView.findViewById(R.id.quantity4);
					material4 = (ImageView)itemView.findViewById(R.id.material4);
					tick4 = (ImageView)itemView.findViewById(R.id.tick4);

					line5 = (RelativeLayout)itemView.findViewById(R.id.line5);
					quantity5 = (TextView) itemView.findViewById(R.id.quantity5);
					material5 = (ImageView)itemView.findViewById(R.id.material5);
					tick5 = (ImageView)itemView.findViewById(R.id.tick5);

					redeem = (Button)itemView.findViewById(R.id.redeem);
					redeem.setOnClickListener(this);


			}

		}


		public void onClick(View v) {
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(v, getAdapterPosition());
		}
	}

}
