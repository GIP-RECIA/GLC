package fr.recia.glc.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphanumericsTools {

    /** Valeur de l'incrément.*/
    private static final int INCREMENT = 64;

    /** Valeur pour la base Alpha Numérique.*/
    private static final int BASE_ALPHA_NUM = 36;

    /**
     * Constructeur de l'objet AlphanumericsTools.java.
     */
    private AlphanumericsTools() {
        super();
    }

    /**
     * Méthode de conversion d'un int en base N.
     * @param nb le nombre à convertir.
     * @param base la base de conversion, base 36 conseillée pour l'alphanumérique.
     * @param nbChar nombre de caractères que l'on doit obtenir.
     * @return <code>String</code> La chaine de caractère obtenue.
     */
    public static String toBaseN(final int nb, final int base, final int nbChar) {
        long inc = base;
        for (int i = 1; i < nbChar; i++) {
            inc = inc * base;
        }
        // on vérifie que l'on reste dans le cadre du nombre de caratères
        if (inc < (nb + 1) ) {
            log.warn("Try to convert a number in base on too less characters: nb base nbchar = ");
        }
        int reste;
        int nombre = nb;
        final int max = 9;
        String chaine = "";
        while (nombre >= base) {
            reste = nombre % base;
            nombre = (nombre - reste) / base;
            if (reste > max) {
                chaine = ((char) (reste - max + INCREMENT)) + chaine;
            } else {
                chaine = String.valueOf(reste) + chaine;
            }
        }
        if (nombre > max ) {
            chaine = String.valueOf((char) (nombre - max + INCREMENT)) + chaine;
        } else {
            chaine = String.valueOf(nombre) + chaine;
        }
        // Pour l'obtention d'un nombre de caratères égal à la demande
        for (int i = chaine.length(); i < nbChar; i++ ) {
            chaine = "0" + chaine;
        }

        return chaine.toLowerCase();
    }

    /**
     * Méthode de conversion d'un entier en alphanumérique.
     * @param nb le nombre à convertir.
     * @param nbChar le nombre de caractère maximum à obtenir, null si nb trop grand.
     * @return <code>String</code> La chaine de caractère alphanumérique obtenue.
     */
    public static String toAlphanum(final int nb, final int nbChar) {
        return toBaseN(nb, BASE_ALPHA_NUM, nbChar);
    }

}
