package fr.recia.glc.web.dto.restriction;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RestrictionClasse {
    private String classe;
    private ZonedDateTime dateRentreeClasse;
}
