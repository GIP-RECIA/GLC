package fr.recia.glc.web.dto.function;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ListFonctionPossibleDto {
    private Date dateFinDefaut;
    private List<DisciplinesInFillierePossiblesDto> fonctions;
}
