package fr.recia.glc.web.dto.function;

import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FonctionPossibleDto {

    private FiliereDisplayDto filiere;
    private DisciplineDisplayDto discipline;

    public FonctionPossibleDto(TypeFonctionFiliere filiere, Discipline discipline) {
        this.filiere = new FiliereDisplayDto(filiere.getId(), filiere.getLibelleFiliere());
        this.discipline = new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste());
    }
}
