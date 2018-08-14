package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GcloudVoiceChatSessionResource {

	public String id;
	public Boolean isMuted;
	public Boolean isTransmitEnabled;
	public List<GcloudVoiceChatParticipantResource> participants;
	public GcloudVoiceChatSessionStatus status;
	public Integer volume;

}