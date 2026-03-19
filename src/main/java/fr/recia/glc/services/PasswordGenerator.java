package fr.recia.glc.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@Data
public class PasswordGenerator {

    //aucun O pour éviter de confondre avec le zero.
    //pas de l pour éviter de confondre avec le un.

    /** Liste des nombres autorisés. */
    private static final String DIGITS               = "0123456789";
    /** Liste des caractères minuscules autorisés. */
    private static final String LOCASE_CHARACTERS    = "abcdefghijklmnopqrstuvwxyz";
    /** Liste des caractères majuscules autorisés. */
    private static final String UPCASE_CHARACTERS    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** Liste des caractères spéciaux autorisés. */
    private static final String OTHER_CHARACTERS    = "_-#(!?)$?:;/.?­,";
    /** Ensemble des caractères autorisés. */
    private String characterList = DIGITS + LOCASE_CHARACTERS + UPCASE_CHARACTERS;
    private int nbCharDef = 8;

    /**
     * Méthode générant de façon aléatoire un password.
     * @param nbChar Le nombre de caractères voulu dans le password.
     * @return <code>String</code> Le password généré.
     */
    public String genPassword(final int nbChar) {
        String pass = "";
        Random rand = new Random();
        for (int i = 0; i < nbChar; i++) {
            pass += characterList.charAt(rand.nextInt(characterList.length()));
        }
        return pass;
    }
    /**
     * Méthode générant de façon aléatoire un password.
     * @param nbChar Le nombre de caractères voulu dans le password.
     * @param charList Liste des caractères susceptibles de constituer le password.
     * @return <code>String</code> Le password généré.
     */
    public String genPassword(final int nbChar, final String charList) {
        String pass = "";
        Random rand = new Random();
        for (int i = 0; i < nbChar; i++) {
            pass += charList.charAt(rand.nextInt(charList.length()));
        }
        return pass;
    }

    public String genPassword() {
        return genPassword(nbCharDef);
    }


}
