package fr.recia.glc.web.dto.function;

import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DisciplinePossibleDto {

    private String libelle;
    private List<DisciplineDisplayDto> disciplines;

    public DisciplinePossibleDto(String libelle){
        this.libelle = libelle;
        this.disciplines = new ArrayList<>();
    }

}
