package fr.recia.glc.services;

import fr.recia.glc.configuration.GLCProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UidFactory {

    private String codeRegion;
    private String nomSource;
    private int nbLettreAnn;
    private int nbLettreTotal;
    private int nbLettreInc;
    private int baseConvertion;

    public UidFactory(GLCProperties glcProperties){
        this.codeRegion = glcProperties.getUidFactory().getCodeRegion();
        this.nomSource = glcProperties.getUidFactory().getNomSource();
        this.nbLettreAnn = glcProperties.getUidFactory().getNbLettreAnn();
        this.nbLettreTotal = glcProperties.getUidFactory().getNbLettreTotal();
        this.nbLettreInc = glcProperties.getUidFactory().getNbLettreInc();
        this.baseConvertion = ('Z' > 'z' ? 'Z' : 'z') + 1;
    }

    public String codeAnnee(final String annee) {
        // si l'annee est du type 2009/2010, c'est 2009 qui nous interesses
        int idx = annee.indexOf('/');
        if (idx < nbLettreAnn) {
            // cas ou on n'a pas de /
            idx = annee.length();
            if (idx < nbLettreAnn) {
                return null;
            }
        }
        return annee.substring(idx - nbLettreAnn, idx);
    }

    private boolean calculNbLettreInc(final String codeGenerateur){
        if (nbLettreInc > 0) {
            return true;
        }
        if (codeRegion != null && codeGenerateur != null ) {
            nbLettreInc = nbLettreTotal - nbLettreAnn - getCodeRegion().length() - codeGenerateur.length();
        } else {
            nbLettreInc = 0;
        }
        return nbLettreInc > 0;
    }

    public String uid(final String annee, final int increment, final String codeGenerateur) {
        String uidS;
        if (calculNbLettreInc(codeGenerateur)){
            if (nbLettreAnn > 0 ) {
                String codeAnnee = codeAnnee(annee);
                if (codeAnnee == null) {
                    return null;
                }
                uidS = String.format("%s%s%s%s",
                        codeRegion,
                        codeAnnee,
                        codeGenerateur,
                        AlphanumericsTools.toAlphanum(increment, nbLettreInc));
            } else {
                uidS = String.format("%s%s%s",
                        codeRegion,
                        codeGenerateur,
                        AlphanumericsTools.toAlphanum(increment, nbLettreInc));

            }
            return uidS;
        }
        return null;
    }

    /**
     * convertie la clee en uid
     * @param clee
     * @return uid
     */
    public String uid(final String clee) {
        long clef = Long.parseLong(clee);
        char uid[] = new char[nbLettreTotal];

        for (int i = nbLettreTotal; --i >= 0; ) {
            int c = (int) (clef % baseConvertion);
            uid[i] = (char) c;
            clef = clef/baseConvertion;
        }

        return new String(uid);
    }

    /**
     * Calcul de la clee a partir de l'uid
     */
    public String clee(final String uid) {
        long clee = 0;
        for (int i = 0; i < uid.length(); i++) {
            int val = uid.charAt(i);
            clee = (baseConvertion * clee) + val;
        }
        return String.valueOf(clee);
    }

    public String getSource(String suffixSource) {
        return String.format("%s_%s", nomSource, suffixSource);
    }

}