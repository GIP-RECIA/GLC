package fr.recia.glc.web.dto.user;

import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.web.dto.function.FonctionToModify;
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
    private Long mefEleve;
    private Long classe;
    private List<Long> enseignements;
    // Tous sauf élève
    private List<FonctionToModify> fonctions;
    // Spécifique enseignant
    private Long enseignementProf;
    private List<Long> groupesEns;

}
