package generated;

import com.google.gson.annotations.SerializedName;

public enum PatcherComponentStateAction {

	@SerializedName("Idle")
	IDLE,
	@SerializedName("CheckingForUpdates")
	CHECKINGFORUPDATES,
	@SerializedName("Patching")
	PATCHING,
	@SerializedName("Repairing")
	REPAIRING,
	@SerializedName("Migrating")
	MIGRATING

}