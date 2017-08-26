package generated;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PluginMetadataResource {

	public String app;
	public String feature;
	public List<String> globalAssetBundles;
	public Boolean hasBundledAssets;
	@SerializedName("implements")
	public Map<String, Object> implementsField;
	public Map<String, Object> perLocaleAssetBundles;
	public String subtype;
	public PluginThreadingModel threading;
	public String type;

}