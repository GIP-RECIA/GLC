package fr.recia.glc.web.dto.user;

import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserCreation {

    // Commun à tous
    private Long structureRattachement;
    private CategoriePersonne categoriePersonne;
    private String prenom;
    private String nom;
    private Date dateNaissance;
    private Civilite civilite;
    // Tous sauf élève
    private String courriel;
    // Spécifique élève
    private String statut;
    private String regime;
    private boolean majeur;
    private boolean majeurAnticipe;
    private boolean transportScolaire;
    private String classeEleve;
    // Tous sauf élève
    private String fonction;
    private List<String> disciplines; // Liées à une fonction dans l'établissement
    // Spécifique enseignant
    private String enseignement;
    private List<String> classesEns;
    private List<String> groupesEns;

}
