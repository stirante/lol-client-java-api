
package examples.pojo;

import com.google.gson.annotations.Expose;

public class SummonerSpells {

    @Expose
    private SummonerSpell summonerSpellOne;
    @Expose
    private SummonerSpell summonerSpellTwo;

    public SummonerSpell getFirst() {
        return summonerSpellOne;
    }

    public void setFirst(SummonerSpell summonerSpell) {
        this.summonerSpellOne = summonerSpell;
    }

    public SummonerSpell getSecond() {
        return summonerSpellTwo;
    }

    public void setSecond(SummonerSpell summonerSpell) {
        this.summonerSpellTwo = summonerSpell;
    }

}
