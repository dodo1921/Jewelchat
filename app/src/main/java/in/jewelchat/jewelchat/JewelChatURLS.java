package in.jewelchat.jewelchat;

/**
 * Created by mayukhchakraborty on 24/02/16.
 */
public class JewelChatURLS {

	private static final String baseURL = "http://192.168.1.2:3000";

	public static final String CLOUDPATH = " ";

	public static final String REGISTRATION_URL = baseURL + "/registerPhoneNumber";
	public static final String VERIFICATIONCODE_URL = baseURL + "/verifyCode";
	public static final String RESENDVCODE_URL = baseURL + "/resendVcode";
	public static final String INITIAL_DETAILS = baseURL + "/initialDetails";
	public static final String UPDATE_GCM_TOKEN = baseURL + "/updateGcmToken";

	public static final String GETCUSTOMTOKEN = baseURL + "/getCustomTokenFirebase";

	public static final String UPDATEPROFILEPIC = baseURL + "/updateProfilePic";
	public static final String UPDATEPROFILESTATUS = baseURL + "/updateProfileStatus";
	public static final String UPDATEPROFILENAME = baseURL + "/updateProfileName";

	public static final String INVITE = baseURL + "/inviteUser";
	public static final String DOWNLOADCONTACT = baseURL + "/downloadContact";
	public static final String DOWNLOADCONTACT_PHONE = baseURL + "/downloadContact_phone";

	public static final String PICKJEWEL = baseURL +"/pickJewel";

	public static final String GETCONTACTBYPHONENUMBERLIST = baseURL + "/getContactByPhoneNumberList";
	public static final String GETGAMESTATE = baseURL + "/getGameState";

	public static final String GETALLCHATMESSAGES = baseURL + "/getAllChatMessages";
	public static final String GETALLGROUPMESSAGES = baseURL + "/getAllGroupChatMessages";

	public static final String GETGROUPLIST = baseURL + "/getGroups";
	public static final String GETBLOCKEDUSERS = baseURL + "/getBlockedUsers";

	public static final String GETFACTORIES = baseURL + "/getFactories";

	public static final String STARTFACTORY = baseURL + "/startFactory";
	public static final String STOPFACTORY = baseURL + "/stopFactory";
	public static final String FLUSHFACTORY = baseURL + "/flushFactory";


	public static final String GETACHIEVEMENTS = baseURL + "/getAchievements";
	public static final String REDEEMACHIEVEMENT = baseURL + "/redeemAchievement";
	public static final String GETTASKS = baseURL + "/getTasks";
	public static final String REDEEMTASK = baseURL + "/redeemTask";

	public static final String GETWALLET = baseURL + "/getWallet";

	public static final String DELIVERY = baseURL + "/delivery";


}
