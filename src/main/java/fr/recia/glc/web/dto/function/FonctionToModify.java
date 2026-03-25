package fr.recia.glc.web.dto.function;

import lombok.Data;

import java.util.Date;

@Data
public class FonctionToModify {
    private Long filiere;
    private Long discipline;
    private Date dateFin;
}
