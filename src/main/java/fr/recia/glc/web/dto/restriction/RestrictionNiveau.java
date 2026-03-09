package fr.recia.glc.web.dto.restriction;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class RestrictionNiveau {
    private String niveau;
    private ZonedDateTime dateRentreeNiveau;
    private List<RestrictionClasse> classes;
}
