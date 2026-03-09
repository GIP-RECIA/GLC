package fr.recia.glc.web.dto.restriction;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class RestrictionEtab {
    private String uai;
    private ZonedDateTime dateRentreeEtab;
    private List<RestrictionNiveau> niveaux;
}
