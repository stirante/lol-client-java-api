package generated;

import com.google.gson.annotations.SerializedName;

public enum LolReplaysMetadataState {

	@SerializedName("checking")
	CHECKING,
	@SerializedName("found")
	FOUND,
	@SerializedName("watch")
	WATCH,
	@SerializedName("download")
	DOWNLOAD,
	@SerializedName("downloading")
	DOWNLOADING,
	@SerializedName("incompatible")
	INCOMPATIBLE,
	@SerializedName("missingOrExpired")
	MISSINGOREXPIRED,
	@SerializedName("retryDownload")
	RETRYDOWNLOAD,
	@SerializedName("unsupported")
	UNSUPPORTED,
	@SerializedName("error")
	ERROR

}