
package examples.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;

public class FullRunes {

    @Expose
    private List<Rune> generalRunes;
    @Expose
    private Rune keystone;
    @Expose
    private RuneTree runeTree;
    @Expose
    private RuneTree secondaryRuneTree;
    @Expose
    private List<StatRune> statRunes;

    public List<Rune> getGeneralRunes() {
        return generalRunes;
    }

    public void setGeneralRunes(List<Rune> generalRunes) {
        this.generalRunes = generalRunes;
    }

    public Rune getKeystone() {
        return keystone;
    }

    public void setKeystone(Rune keystone) {
        this.keystone = keystone;
    }

    public RuneTree getRuneTree() {
        return runeTree;
    }

    public void setRuneTree(RuneTree runeTree) {
        this.runeTree = runeTree;
    }

    public RuneTree getSecondaryRuneTree() {
        return secondaryRuneTree;
    }

    public void setSecondaryRuneTree(RuneTree secondaryRuneTree) {
        this.secondaryRuneTree = secondaryRuneTree;
    }

    public List<StatRune> getStatRunes() {
        return statRunes;
    }

    public void setStatRunes(List<StatRune> statRunes) {
        this.statRunes = statRunes;
    }

}
