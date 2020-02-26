
package examples.pojo;

import com.google.gson.annotations.SerializedName;

public class Abilities {

    @SerializedName("E")
    private Ability e;
    @SerializedName("Passive")
    private Passive passive;
    @SerializedName("Q")
    private Ability q;
    @SerializedName("R")
    private Ability r;
    @SerializedName("W")
    private Ability w;

    public Ability getE() {
        return e;
    }

    public void setE(Ability e) {
        this.e = e;
    }

    public Passive getPassive() {
        return passive;
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    public Ability getQ() {
        return q;
    }

    public void setQ(Ability q) {
        this.q = q;
    }

    public Ability getR() {
        return r;
    }

    public void setR(Ability r) {
        this.r = r;
    }

    public Ability getW() {
        return w;
    }

    public void setW(Ability w) {
        this.w = w;
    }

}
