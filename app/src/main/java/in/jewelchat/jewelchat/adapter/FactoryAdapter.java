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
import in.jewelchat.jewelchat.models.Factory;

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
	public void onBindViewHolder(MyViewHolder holder, int position) {
		Factory f = this.factoryList.get(position);
		//int x;
		//if(f.factory_type == 1)
		//holder.factory_image.setImageResource(R.drawable.t3);

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
		public TextView karma_user_name;
		public TextView total_karma;
		public ImageView userAvatar;
		public ImageView leaderBoard;


		public TextView contest_name;
		public TextView contest_text;
		public TextView contest_total_karma;
		public TextView contest_winners_count;
		public Button redeem_offer;

		public MyViewHolder(View itemView) {
			super(itemView);
			/*contest_name = (TextView) itemView.findViewById(R.id.contest_name);
			contest_text = (TextView) itemView.findViewById(R.id.contest_text);
			contest_image = (SquareImageView) itemView.findViewById(R.id.contest_image);
			contest_total_karma = (TextView) itemView.findViewById(R.id.contest_total_karma);
			contest_winners_count = (TextView) itemView.findViewById(R.id.contest_winners_count);
			redeem_offer = (Button) itemView.findViewById(R.id.redeem_offer);
			redeem_offer.setOnClickListener(this);
			contest_image.setOnClickListener(this);*/

		}


		public void onClick(View v) {
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(v, getAdapterPosition());
		}
	}
}
