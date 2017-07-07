package in.jewelchat.jewelchat.models;

/**
 * Created by mayukhchakraborty on 21/06/17.
 */

public class GameStateChangeEvent {

	public final int TOTAL;
	public final int LEVEL;
	public final int LEVEL_XP;
	public final int XP;
	public final boolean levelchange;


	public GameStateChangeEvent(int TOTAL, int LEVEL, int LEVEL_XP, int XP, boolean levelchange){
		this.TOTAL = TOTAL;
		this.LEVEL = LEVEL;
		this.LEVEL_XP = LEVEL_XP;
		this.XP = XP;
		this.levelchange = levelchange;
	}


}
