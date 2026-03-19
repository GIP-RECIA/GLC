package fr.recia.glc.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

@Slf4j
@Service
public class StringOperation {

    /** REGEXP pour la suppression des caractères ne devant pas apparaitre dans le CN. */
    public static final String REGEX_CN = "[-']";

    /** Pour la regexp de remplacement des multi espaces.*/
    public static final String STRING_SPACE = " ";

    /** REGEXP pour la suppression des espaces en trop. */
    public static final String REGEX_SPACES = "\\s{2,}";

    /** Separateur pour les liste de valeurs. */
    public static final String LIST_SEPARATOR = ",";

    /**
     * Index du 1er caractere accentué.
     **/
    private static final int MIN = 192;

    /**
     * Index du dernier caractere accentué.
     **/
    private static final int MAX = 255;

    /**
     * Vecteur de correspondance entre accent / sans accent.
     **/
    private static final Collection<String> MAP = initMap();


    /**
     * Constructeur de l'objet StringOperation.java.
     */
    private StringOperation() {
        super();
    }

    /**
     * Initialisation du tableau de correspondance entre les caractéres accentués
     * et leur homologues non accentués.
     *
     * @return vecteur.
     */
    private static Collection<String> initMap() {
        Collection<String> result = new Vector<String>();
        String car = null;

        car = new String("A");
        /* '\u00C0'   À   alt-0192  */
        result.add(car);
        /* '\u00C1'   Á   alt-0193  */
        result.add(car);
        /* '\u00C2'   Â   alt-0194  */
        result.add(car);
        /* '\u00C3'   Ã   alt-0195  */
        result.add(car);
        /* '\u00C4'   Ä   alt-0196  */
        result.add(car);
        /* '\u00C5'   Å   alt-0197  */
        result.add(car);
        car = new String("AE");
        /* '\u00C6'   Æ   alt-0198  */
        result.add(car);
        car = new String("C");
        /* '\u00C7'   Ç   alt-0199  */
        result.add(car);
        car = new String("E");
        /* '\u00C8'   È   alt-0200  */
        result.add(car);
        /* '\u00C9'   É   alt-0201  */
        result.add(car);
        /* '\u00CA'   Ê   alt-0202  */
        result.add(car);
        /* '\u00CB'   Ë   alt-0203  */
        result.add(car);
        car = new String("I");
        /* '\u00CC'   Ì   alt-0204  */
        result.add(car);
        /* '\u00CD'   Í   alt-0205  */
        result.add(car);
        /* '\u00CE'   Î   alt-0206  */
        result.add(car);
        /* '\u00CF'   Ï   alt-0207  */
        result.add(car);
        car = new String("D");
        /* '\u00D0'   Ð   alt-0208  */
        result.add(car);
        car = new String("N");
        /* '\u00D1'   Ñ   alt-0209  */
        result.add(car);
        car = new String("O");
        /* '\u00D2'   Ò   alt-0210  */
        result.add(car);
        /* '\u00D3'   Ó   alt-0211  */
        result.add(car);
        /* '\u00D4'   Ô   alt-0212  */
        result.add(car);
        /* '\u00D5'   Õ   alt-0213  */
        result.add(car);
        /* '\u00D6'   Ö   alt-0214  */
        result.add(car);
        car = new String("*");
        /* '\u00D7'   ×   alt-0215  */
        result.add(car);
        car = new String("0");
        /* '\u00D8'   Ø   alt-0216  */
        result.add(car);
        car = new String("U");
        /* '\u00D9'   Ù   alt-0217  */
        result.add(car);
        /* '\u00DA'   Ú   alt-0218  */
        result.add(car);
        /* '\u00DB'   Û   alt-0219  */
        result.add(car);
        /* '\u00DC'   Ü   alt-0220  */
        result.add(car);
        car = new String("Y");
        /* '\u00DD'   Ý   alt-0221  */
        result.add(car);
        car = new String("Þ");
        /* '\u00DE'   Þ   alt-0222  */
        result.add(car);
        car = new String("B");
        /* '\u00DF'   ß   alt-0223  */
        result.add(car);
        car = new String("a");
        /* '\u00E0'   à   alt-0224  */
        result.add(car);
        /* '\u00E1'   á   alt-0225  */
        result.add(car);
        /* '\u00E2'   â   alt-0226  */
        result.add(car);
        /* '\u00E3'   ã   alt-0227  */
        result.add(car);
        /* '\u00E4'   ä   alt-0228  */
        result.add(car);
        /* '\u00E5'   å   alt-0229  */
        result.add(car);
        car = new String("ae");
        /* '\u00E6'   æ   alt-0230  */
        result.add(car);
        car = new String("c");
        /* '\u00E7'   ç   alt-0231  */
        result.add(car);
        car = new String("e");
        /* '\u00E8'   è   alt-0232  */
        result.add(car);
        /* '\u00E9'   é   alt-0233  */
        result.add(car);
        /* '\u00EA'   ê   alt-0234  */
        result.add(car);
        /* '\u00EB'   ë   alt-0235  */
        result.add(car);
        car = new String("i");
        /* '\u00EC'   ì   alt-0236  */
        result.add(car);
        /* '\u00ED'   í   alt-0237  */
        result.add(car);
        /* '\u00EE'   î   alt-0238  */
        result.add(car);
        /* '\u00EF'   ï   alt-0239  */
        result.add(car);
        car = new String("d");
        /* '\u00F0'   ð   alt-0240  */
        result.add(car);
        car = new String("n");
        /* '\u00F1'   ñ   alt-0241  */
        result.add(car);
        car = new String("o");
        /* '\u00F2'   ò   alt-0242  */
        result.add(car);
        /* '\u00F3'   ó   alt-0243  */
        result.add(car);
        /* '\u00F4'   ô   alt-0244  */
        result.add(car);
        /* '\u00F5'   õ   alt-0245  */
        result.add(car);
        /* '\u00F6'   ö   alt-0246  */
        result.add(car);
        car = new String("/");
        /* '\u00F7'   ÷   alt-0247  */
        result.add(car);
        car = new String("0");
        /* '\u00F8'   ø   alt-0248  */
        result.add(car);
        car = new String("u");
        /* '\u00F9'   ù   alt-0249  */
        result.add(car);
        /* '\u00FA'   ú   alt-0250  */
        result.add(car);
        /* '\u00FB'   û   alt-0251  */
        result.add(car);
        /* '\u00FC'   ü   alt-0252  */
        result.add(car);
        car = new String("y");
        /* '\u00FD'   ý   alt-0253  */
        result.add(car);
        car = new String("þ");
        /* '\u00FE'   þ   alt-0254  */
        result.add(car);
        car = new String("y");
        /* '\u00FF'   ÿ   alt-0255  */
        result.add(car);
        /* '\u00FF'       alt-0255  */
        result.add(car);

        return result;
    }

    /**
     * Transforme une chaine pouvant contenir des accents dans une version sans accent.
     *
     * @param chaine Chaine a convertir sans accent
     * @return Chaine dont les accents ont été supprimé
     **/
    @SuppressWarnings("static-method")
    @Deprecated
    public String sansAccentANSI(final String chaine) {
        StringBuffer result = new StringBuffer(chaine);
        log.debug("Chaine à traiter : " + chaine);
        for (int bcl = 0; bcl < chaine.length(); bcl++) {
            int carVal = chaine.charAt(bcl);
            log.debug("carVal : " + carVal + ", BCL " + bcl + ", " + chaine.charAt(bcl));
            if (carVal >= MIN && carVal <= MAX) {
                // Remplacement
                String newVal = ((Vector<String>) MAP).get(carVal - MIN);
                log.debug("newVal : " + newVal);
                result.replace(bcl, bcl + 1, newVal);
            }
        }
        log.debug("Chaine obtenue : " + result.toString());
        return result.toString();
    }

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
     * @param sn        Le nom de la personne.
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

    /**
     * Extraction de la valeur d'une option si elle est présente.
     *
     * @param listRepresentation La chaine representant la liste.
     * @return La liste construite.
     */
    public static List<Integer> buildIntList(final String listRepresentation) {
        final String[] tokens = listRepresentation.split(LIST_SEPARATOR);
        final List<Integer> value = new ArrayList<>();
        for (String token : tokens) {
            value.add(Integer.parseInt(token));
        }
        return value;
    }
}
