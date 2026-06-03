package fr.recia.db.dto.gestion;

import fr.recia.db.enums.Etat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DatabaseIncertainDto {
    private String attribut;
    private String value;
    private String texte;
    private boolean obligatoire;
    private Long id;
    private Etat etat;
    private String source;
    private String cn;
    private Date dateModification;
    private Date dateAcquittement;
}
