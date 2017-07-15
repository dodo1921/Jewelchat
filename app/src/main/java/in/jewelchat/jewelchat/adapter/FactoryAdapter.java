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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.Factory;
import in.jewelchat.jewelchat.models.TaskMaterials;

/**
 * Created by mayukhchakraborty on 17/06/17.
 */

public class FactoryAdapter extends RecyclerView.Adapter<FactoryAdapter.MyViewHolder> {

	private Context mContext;
	private List<Factory> factoryList;
	public FactoryAdapter.OnItemClickListener mItemClickListener = null;

	public FactoryAdapter(Context mContext, FactoryAdapter.OnItemClickListener mItemClickListener, List<Factory> factoryList) {
		this.mContext = mContext;
		this.factoryList = factoryList;
		this.mItemClickListener = mItemClickListener;
	}

	public interface OnItemClickListener {

		void onItemClick(View view, int position);
	}


	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.activity_factories_element, parent, false);

		return new MyViewHolder(itemView);
	}



	@Override
	public void onBindViewHolder(MyViewHolder holder, int pos) {
		//Factory f = this.factoryList.get(position);
		//int pos = position;
		holder.thumbnail.setImageResource(getJewelDrawable(factoryList.get(pos).factory_type));
		holder.duration.setText("Duration: "+(int)(factoryList.get(pos).duration/60000) + "mins" );
		holder.duration.setText("Qty: "+(int)(factoryList.get(pos).amount));
		holder.factory_on.setVisibility(factoryList.get(pos).is_on?View.VISIBLE:View.GONE);
		holder.factory_timer.setText(factoryList.get(pos).time_left);

		if(factoryList.get(pos).open)
			holder.factory_details.setVisibility(View.VISIBLE);
		else
			holder.factory_details.setVisibility(View.GONE);

		holder.line1.setVisibility(View.GONE); holder.tick1.setVisibility(View.INVISIBLE);
		holder.line2.setVisibility(View.GONE); holder.tick2.setVisibility(View.INVISIBLE);
		holder.line3.setVisibility(View.GONE); holder.tick3.setVisibility(View.INVISIBLE);
		holder.line4.setVisibility(View.GONE); holder.tick4.setVisibility(View.INVISIBLE);
		holder.line5.setVisibility(View.GONE); holder.tick5.setVisibility(View.INVISIBLE);

		List<TaskMaterials> m = factoryList.get(pos).materials;
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

		if(!factoryList.get(pos).all_materials_present && factoryList.get(pos).buttonState==0) {
			holder.start.setEnabled(false);holder.start.setText("Start");
		}else if(factoryList.get(pos).all_materials_present && factoryList.get(pos).buttonState==0) {
			holder.start.setEnabled(true);holder.start.setText("Start");
		}else if(factoryList.get(pos).buttonState==1){
			holder.start.setEnabled(true);holder.start.setText("Fast track production with "+factoryList.get(pos).diamond+" diamonds");
		}else if(factoryList.get(pos).buttonState==2){
			//Animation animation = AnimationUtils.loadAnimation(this.mContext, R.anim.jewel_rotate);
			//holder.thumbnail.startAnimation(animation);
			holder.start.setEnabled(true);holder.start.setText("Transfer jewels to Jewel Store");
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
		return position;
	}

	@Override
	public int getItemCount() {
		return factoryList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public ImageView thumbnail;
		public ProgressBar factory_on;
		public TextView factory_timer;
		public TextView duration;
		public TextView amount;
		public RelativeLayout factory;
		public LinearLayout factory_details;
		public Button start;



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



		public MyViewHolder(View itemView) {
			super(itemView);

			thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
			factory_on = (ProgressBar) itemView.findViewById(R.id.factory_on);
			factory_timer = (TextView)itemView.findViewById(R.id.factory_timer);
			duration = (TextView)itemView.findViewById(R.id.duration);
			amount = (TextView)itemView.findViewById(R.id.amount);
			factory = (RelativeLayout)itemView.findViewById(R.id.factory);
			factory.setOnClickListener(this);
			factory_details = (LinearLayout)itemView.findViewById(R.id.factory_details);


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

			start = (Button)itemView.findViewById(R.id.start);
			start.setOnClickListener(this);

		}


		public void onClick(View v) {
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(v, getAdapterPosition());
		}
	}
}
