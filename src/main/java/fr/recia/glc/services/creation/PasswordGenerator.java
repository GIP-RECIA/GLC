package fr.recia.glc.services.creation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@Data
public class PasswordGenerator {

    // aucun O pour éviter de confondre avec le zero.
    // pas de l pour éviter de confondre avec le un.

    /** Liste des nombres autorisés. */
    private static final String DIGITS = "0123456789";
    /** Liste des caractères minuscules autorisés. */
    private static final String LOCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    /** Liste des caractères majuscules autorisés. */
    private static final String UPCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** Liste des caractères spéciaux autorisés. */
    private static final String OTHER_CHARACTERS = "_-#(!?)$?:;/.?­,";
    /** Ensemble des caractères autorisés. */
    private String characterList = DIGITS + LOCASE_CHARACTERS + UPCASE_CHARACTERS;
    private int nbCharDef = 8;

    /**
     * Méthode générant de façon aléatoire un password.
     * @param nbChar Le nombre de caractères voulu dans le password.
     * @return <code>String</code> Le password généré.
     */
    public String genPassword(final int nbChar) {
        StringBuilder pass = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < nbChar; i++) {
            pass.append(characterList.charAt(rand.nextInt(characterList.length())));
        }
        return pass.toString();
    }

    public String genPassword() {
        return genPassword(nbCharDef);
    }


}
