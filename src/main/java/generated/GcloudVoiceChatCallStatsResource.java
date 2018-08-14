package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GcloudVoiceChatCallStatsResource {

	public String callId;
	public List<Long> captureDeviceConsecutivelyReadCount;
	public String codecName;
	public Long currentBars;
	public Long currentOpusBandwidth;
	public Long currentOpusBitRate;
	public Long currentOpusComplexity;
	public Long currentOpusMaxPacketSize;
	public Long currentOpusVbrMode;
	public Long incomingDiscarded;
	public Long incomingOutOfTime;
	public Long incomingPacketloss;
	public Long incomingReceived;
	public Double lastLatencyMeasured;
	public Long latencyMeasurementCount;
	public Long latencyPacketsDropped;
	public Long latencyPacketsLost;
	public Long latencyPacketsMalformed;
	public Long latencyPacketsNegativeLatency;
	public Long latencyPacketsSent;
	public Double latencySum;
	public Double maxLatency;
	public Double minLatency;
	public Long outgoingSent;
	public Long plcOn;
	public Long plcSyntheticFrames;
	public Double rFactor;
	public Long renderDeviceErrors;
	public Long renderDeviceOverruns;
	public Long renderDeviceUnderruns;
	public Double sampleIntervalBegin;
	public Double sampleIntervalEnd;

}