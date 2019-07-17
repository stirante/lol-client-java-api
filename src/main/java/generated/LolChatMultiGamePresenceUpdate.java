package generated;

import com.google.gson.annotations.SerializedName;

public class LolChatMultiGamePresenceUpdate {

	public String msg;
	@SerializedName("private")
	public String privateField;
	public String privateJwt;
	public LolChatMultiGamePresenceSharedPayload shared;
	public String sharedJwt;
	public LolChatAccountState state;

}