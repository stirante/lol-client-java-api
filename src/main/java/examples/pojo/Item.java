
package examples.pojo;

import com.google.gson.annotations.Expose;

public class Item {

    @Expose
    private Boolean canUse;
    @Expose
    private Boolean consumable;
    @Expose
    private Long count;
    @Expose
    private String displayName;
    @Expose
    private Long itemID;
    @Expose
    private Long price;
    @Expose
    private String rawDescription;
    @Expose
    private String rawDisplayName;
    @Expose
    private Long slot;

    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

}
