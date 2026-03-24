package fr.recia.glc.services.creation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@Data
public class NameCalculator {

    private final StringOperation stringOperation;

    private static final char MAX_VALUE = 512;
    /** la taille maximales des logins avant increment. */
    private static final int MAX_LOGIN_LENGHT = 18;
    private final String[] coresp = new String[NameCalculator.MAX_VALUE];
    private final Pattern pattern;

    public NameCalculator(StringOperation stringOperation) {

        this.stringOperation = stringOperation;

        for (char c = 0; c < NameCalculator.MAX_VALUE; c++) {
            String s = "" + c;
            s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{Alpha}]", "");
            if (!s.isEmpty()) {
                coresp[c] = s;
            } else {
                coresp[c] = "";
            }
        }
        coresp['Æ'] = "AE";
        coresp['æ'] = "ae";
        coresp['Œ'] = "OE";
        coresp['œ'] = "oe";
        pattern = Pattern.compile("([\\s|-]++)|(.)");

    }

    public String display(final String nom, final String prenom) {
        return prenom.trim() + " " + nom.trim();
    }

    public String cn(final String nom, final String prenom) {
        return stringOperation.cleanForCN(nom.toUpperCase(), prenom);
    }

    public String sansAccent(final String chaine, final char blanc) {
        Matcher m = pattern.matcher(chaine);
        StringBuilder res = new StringBuilder();

        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) != null) {
                    switch (i) {
                        case 1:
                            res.append(blanc);
                            break;
                        case 2:
                            char c = m.group(i).charAt(0);
                            if (c < MAX_VALUE) {
                                res.append(coresp[c]);
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
        }
        return res.toString();
    }

    /**
     * le login ne doit pas depasser 18 carateres pour pouvoir metre un increment de 2 caractere
     */
    public String login(final String nom, final String prenom) {
        String aux = String.format("%s.%s", sansAccent(prenom.trim(), '-'), sansAccent(nom.trim(), '-')).toLowerCase();
        if (aux.length() > NameCalculator.MAX_LOGIN_LENGHT) {
            return aux.substring(0, NameCalculator.MAX_LOGIN_LENGHT);
        }
        return aux;
    }

}
