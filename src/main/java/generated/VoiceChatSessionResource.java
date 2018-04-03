package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VoiceChatSessionResource {

	public String id;
	public Boolean isMuted;
	public Boolean isTransmitEnabled;
	public List<VoiceChatParticipantResource> participants;
	public VoiceChatSessionStatus status;
	public Integer volume;

}