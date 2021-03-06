package in.jewelchat.jewelchat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class Task implements Comparable<Task> {

	public int id;
	public int task_id;
	public boolean has_duration;
	public String duration;
	public int coins;
	public int points;
	public boolean show_money;
	public double money;
	public boolean has_qty;
	public int qty;
	public int level;
	public List<TaskMaterials> materials = new ArrayList<TaskMaterials>();
	public boolean done;
	public boolean open;
	public boolean redeem;
	public int color;

	@Override
	public int compareTo(Task task) {
		return (int)( task.money - this.money);
	}


}
