package fr.recia.glc.services.db;

import fr.recia.glc.db.entities.groupe.Classe;
import fr.recia.glc.db.entities.groupe.Groupe;
import fr.recia.glc.db.entities.groupe.MappingAGroupeAPersonneEnseignement;
import fr.recia.glc.db.enums.CategorieGroupe;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.repositories.groupe.ClasseRepository;
import fr.recia.glc.db.repositories.groupe.GroupeRepository;
import fr.recia.glc.db.repositories.groupe.MappingAGroupeAPersonneEnseignementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupeService {

    @Autowired
    private ClasseRepository<Classe> classeRepository;
    @Autowired
    private GroupeRepository<Groupe> groupeRepository;
    @Autowired
    private MappingAGroupeAPersonneEnseignementRepository<MappingAGroupeAPersonneEnseignement> mappingAGroupeAPersonneEnseignementRepository;

    public List<String> getClassesOfPersonne(Long personneId, CategoriePersonne categoriePersonne){
        if(categoriePersonne.equals(CategoriePersonne.Eleve)){
            return classeRepository.findByPersonneId(personneId);
        }
        if(categoriePersonne.equals(CategoriePersonne.Enseignant)){
            return mappingAGroupeAPersonneEnseignementRepository.findByEnseignantIdAndCategorie(personneId, CategorieGroupe.Classe);
        }
        return new ArrayList<>();
    }

    public List<String> getGroupesOfPersonne(Long personneId, CategoriePersonne categoriePersonne){
        if(categoriePersonne.equals(CategoriePersonne.Eleve)){
            return groupeRepository.findByPersonneId(personneId);
        }
        if(categoriePersonne.equals(CategoriePersonne.Enseignant)){
            return mappingAGroupeAPersonneEnseignementRepository.findByEnseignantIdAndCategorie(personneId, CategorieGroupe.Groupe);
        }
        return new ArrayList<>();
    }

}
