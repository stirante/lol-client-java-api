
package examples.pojo;

import com.google.gson.annotations.Expose;

public class StatRune {

    @Expose
    private Long id;
    @Expose
    private String rawDescription;

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

}
