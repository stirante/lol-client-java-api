package generated;

import com.google.gson.annotations.SerializedName;

public enum LolPatchNotificationId {

	@SerializedName("UnspecifiedError")
	UNSPECIFIEDERROR,
	@SerializedName("ConnectionError")
	CONNECTIONERROR,
	@SerializedName("MissingFilesError")
	MISSINGFILESERROR,
	@SerializedName("FailedToWriteError")
	FAILEDTOWRITEERROR,
	@SerializedName("WillRestoreClientBackupOnRestart")
	WILLRESTORECLIENTBACKUPONRESTART,
	@SerializedName("DidRestoreClientBackup")
	DIDRESTORECLIENTBACKUP,
	@SerializedName("NotEnoughDiskSpace")
	NOTENOUGHDISKSPACE

}