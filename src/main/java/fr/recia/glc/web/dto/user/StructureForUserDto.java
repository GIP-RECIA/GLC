package fr.recia.glc.web.dto.user;

import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.entities.structure.ServiceAcademique;
import fr.recia.glc.db.enums.CategorieStructure;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StructureForUserDto {

    private Long id;
    private String uai;
    private CategorieStructure categorie;
    private String type;
    private String nom;
    private String nomCourt;
    private String ville;
    private String siren;
    private List<FonctionDto> fonctions;
    private List<FonctionDto> additionalFonctions;
    private boolean structureRattachement;
    private boolean structureCourante;

    public StructureForUserDto(AStructure aStructure) {
        String[] split = aStructure.getNom().split("\\$");
        if (split.length > 2) {
            this.type = split[0];
            this.nom = split[1];
            this.ville = split[2];
        } else {
            this.nom = aStructure.getNom();
            this.ville = aStructure.getAdresse().getVille();
        }
        this.id = aStructure.getId();
        if(aStructure.getCategorie().equals(CategorieStructure.Etablissement)){
            this.uai = ((Etablissement) aStructure).getUai();
        }
        else if(aStructure.getCategorie().equals(CategorieStructure.Service_academique)){
            this.uai = ((ServiceAcademique) aStructure).getUai();
        }
        this.categorie = aStructure.getCategorie();
        this.nomCourt = aStructure.getNomCourt();
        this.siren = aStructure.getSiren();
        this.fonctions = new ArrayList<>();
        this.additionalFonctions = new ArrayList<>();
        this.structureCourante = false;
        this.structureRattachement = false;
    }

}
