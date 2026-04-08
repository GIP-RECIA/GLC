package fr.recia.glc.db.dto.relation;

import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.enums.CategorieRelation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelationDto {
    private CategorieRelation categorieRelation;
    private SimplePersonneDto personneEnRelation;
    private boolean holder;
}
