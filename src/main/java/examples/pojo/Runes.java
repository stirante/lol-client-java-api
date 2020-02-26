
package examples.pojo;

import com.google.gson.annotations.Expose;

public class Runes {

    @Expose
    private Rune keystone;
    @Expose
    private RuneTree primaryRuneTree;
    @Expose
    private RuneTree secondaryRuneTree;

    public Rune getKeystone() {
        return keystone;
    }

    public void setKeystone(Rune keystone) {
        this.keystone = keystone;
    }

    public RuneTree getPrimaryRuneTree() {
        return primaryRuneTree;
    }

    public void setPrimaryRuneTree(RuneTree runeTree) {
        this.primaryRuneTree = runeTree;
    }

    public RuneTree getSecondaryRuneTree() {
        return secondaryRuneTree;
    }

    public void setSecondaryRuneTree(RuneTree runeTree) {
        this.secondaryRuneTree = runeTree;
    }

}
