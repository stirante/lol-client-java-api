
package examples.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Events {

    @SerializedName("Events")
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
