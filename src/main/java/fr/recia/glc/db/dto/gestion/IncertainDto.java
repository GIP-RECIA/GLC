package fr.recia.glc.db.dto.gestion;

import fr.recia.glc.db.entities.gestion.Incertain;
import fr.recia.glc.web.dto.user.AlertPersonneDto;
import lombok.Data;


@Data
public class IncertainDto {

    private String attribut;
    private String value;

    private String texte;
    private boolean obligatoire;

    public IncertainDto(Incertain incertain){
        this.attribut = incertain.getAttribut();
        this.value = incertain.getValue();
        this.texte = incertain.getTexte();
        this.obligatoire = incertain.isObligatoire();
    }
}
