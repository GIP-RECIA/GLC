package fr.recia.glc.db.dto.gestion;

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.web.dto.user.AlertPersonneDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class IncertainPersonneDto {

    private AlertPersonneDto personne;
    private List<IncertainDto> incertains;

    public IncertainPersonneDto(APersonne aPersonne){
        this.personne = new AlertPersonneDto(aPersonne);
        this.incertains = new ArrayList<>();
    }
}
