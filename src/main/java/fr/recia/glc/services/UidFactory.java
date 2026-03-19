package fr.recia.glc.services;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class UidFactory {

    private String codeRegion = "F";
    private String nomSource = "SarapisUI";
    private int nbLettreAnn = 2;
    private int nbLettreTotal = 8;
    private int nbLettreInc = 0;
    private int baseConvertion = ('Z' > 'z' ? 'Z' : 'z') + 1;

    // TODO : variable en fonction du domaine
    private String codeGenerateur = "Z";

    public String codeAnnee(final String annee) {
        // si l'annee est du type 2009/2010
        // c'est 2009 qui nous interesses
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

    private boolean calculNbLettreInc(){
        if (nbLettreInc > 0) {
            return true;
        }
        if (codeRegion != null && codeGenerateur != null ) {
            nbLettreInc = nbLettreTotal - nbLettreAnn - getCodeRegion().length() - getCodeGenerateur().length();
        } else {
            nbLettreInc = 0;
        }
        return nbLettreInc > 0;
    }

    public String uid(final String annee, final int increment) {
        String uidS;
        if (calculNbLettreInc()){
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
            clee = (baseConvertion * clee )+ val;
        }
        return String.valueOf(clee);
    }

    public String getSource(String suffixSource) {
        return String.format("%s_%s", nomSource, suffixSource);
    }


}