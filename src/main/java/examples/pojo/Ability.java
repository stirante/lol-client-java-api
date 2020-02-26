
package examples.pojo;

import com.google.gson.annotations.Expose;

public class Ability {

    @Expose
    private Long abilityLevel;
    @Expose
    private String displayName;
    @Expose
    private String id;
    @Expose
    private String rawDescription;
    @Expose
    private String rawDisplayName;

    public Long getAbilityLevel() {
        return abilityLevel;
    }

    public void setAbilityLevel(Long abilityLevel) {
        this.abilityLevel = abilityLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
