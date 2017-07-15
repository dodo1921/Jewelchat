package in.jewelchat.jewelchat.models;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayukhchakraborty on 17/06/17.
 */

public class Factory implements Comparable<Factory> {

	public int id;
	public int factory_type;
	public long duration;
	public int amount;
	public int diamond;
	public boolean is_on;
	public List<TaskMaterials> materials = new ArrayList<TaskMaterials>();
	public boolean open;
	public long start_time;
	public long server_time;
	public long device_time;
	public String time_left="";
	public CountDownTimer ct=null;
	public int buttonState = 0;
	public boolean all_materials_present = false;



	@Override
	public int compareTo(Factory factory) {
		return this.id - factory.id;
	}


}


