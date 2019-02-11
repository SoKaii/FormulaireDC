package aubervilliers.orange.aubrecettage.model;

import java.io.Serializable;

public class Recap implements Serializable {

    private String CI2Anum;
    private String dateRecette;
    private String validOrange;
    private String referentOrange;

    public Recap(String CI2Anum, String dateRecetteI, String referentOrange) {

        this.CI2Anum = CI2Anum;
        this.dateRecette = dateRecetteI;
        this.validOrange = null;
        this.referentOrange = referentOrange;
    }

    public String getCI2Anum() {
        return CI2Anum;
    }

    public void setCI2Anum(String CI2Anum) {
        this.CI2Anum = CI2Anum;
    }

    public String getDateRecette() {
        return dateRecette;
    }

    public void setDateRecette(String dateRecetteI) {
        this.dateRecette = dateRecetteI;
    }

    public String getValidOrange() {
        return validOrange;
    }

    public void setValidOrange(String validOrange) {
        this.validOrange = validOrange;
    }

    public String getReferentOrange() {
        return referentOrange;
    }

    public void setReferentOrange(String referentOrange) {
        this.referentOrange = referentOrange;
    }

}
