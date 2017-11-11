package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PatcherProductState {

	public PatcherComponentStateAction action;
	public List<PatcherComponentState> components;
	public String id;
	public Boolean isCorrupted;
	public Boolean isStopped;
	public Boolean isUpToDate;
	public Boolean isUpdateAvailable;
	public Double percentPatched;

}