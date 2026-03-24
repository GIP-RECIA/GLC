package fr.recia.glc.services.creation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Slf4j
@Component
@Data
public class StringOperation {

    /** REGEXP pour la suppression des caractères ne devant pas apparaitre dans le CN. */
    public static final String REGEX_CN = "[-']";

    /** Pour la regexp de remplacement des multi espaces.*/
    public static final String STRING_SPACE = " ";

    /** REGEXP pour la suppression des espaces en trop. */
    public static final String REGEX_SPACES = "\\s{2,}";

    /**
     * Transforme une chaine pouvant contenir des accents dans une version sans accent.
     * Utilisation de la norme java 1.6.
     *
     * @param chaine Chaine a convertir sans accent
     * @return Chaine dont les accents ont été supprimé
     **/
    public String sansAccent(final String chaine) {
        // on normalise la chaine
        String temp = Normalizer.normalize(chaine, Normalizer.Form.NFC);
        // on remplace les caratères entrelacés
        temp = temp.replaceAll("Æ", "AE");
        temp = temp.replaceAll("æ", "ae");
        temp = temp.replaceAll("Œ", "OE");
        temp = temp.replaceAll("œ", "oe");
        // et on remplace les caractères avec accents par décomposition des caratères :
        // caractère primaire suivi du caractère d'accent et suppression des caractères non ascii
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return temp;
    }

    /**
     * Méthode permettant de construire le CN d'une personne à partir du sn et du givenName.
     *
     * @param sn Le nom de la personne.
     * @param givenName Le prénom de la personne.
     * @return <code>String</code>Le CN formaté.
     */
    public String cleanForCN(final String sn, final String givenName) {
        StringBuilder sb = new StringBuilder();
        sb.append(sansAccent(sn.replaceAll(REGEX_CN, STRING_SPACE)
                .replaceAll(REGEX_SPACES, STRING_SPACE)));
        sb.append(STRING_SPACE);
        sb.append(sansAccent(givenName.replaceAll(REGEX_CN, STRING_SPACE)
                .replaceAll(REGEX_SPACES, STRING_SPACE).toUpperCase()));
        return sb.toString();
    }

}
