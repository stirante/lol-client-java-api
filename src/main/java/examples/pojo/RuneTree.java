
package examples.pojo;

import com.google.gson.annotations.Expose;

public class RuneTree {

    @Expose
    private String displayName;
    @Expose
    private Long id;
    @Expose
    private String rawDescription;
    @Expose
    private String rawDisplayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    public String getRawDisplayName() {
        return rawDisplayName;
    }

    public void setRawDisplayName(String rawDisplayName) {
        this.rawDisplayName = rawDisplayName;
    }

}
