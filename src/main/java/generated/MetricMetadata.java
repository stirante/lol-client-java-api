package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MetricMetadata {

	public List<MetricMetadataAlert> alerts;
	public String category;
	public MetricDataType data_type;
	public String description;
	public String destination;
	public String info;
	public MetricMetadataNotify notify;
	public Integer period;
	public String pretty_name;
	public MetricPriority priority;
	public Integer sample_window_ms;
	public AggregationType transientAggregation;
	public MetricType type;
	public String units;

}