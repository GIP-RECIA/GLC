package fr.recia.glc.services.db;

import fr.recia.glc.db.entities.groupe.Classe;
import fr.recia.glc.db.entities.groupe.Groupe;
import fr.recia.glc.db.repositories.groupe.ClasseRepository;
import fr.recia.glc.db.repositories.groupe.GroupeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupeService {

    @Autowired
    private ClasseRepository<Classe> classeRepository;
    @Autowired
    private GroupeRepository<Groupe> groupeRepository;

    public List<String> getClassesOfPersonne(Long personneId){
        return classeRepository.findByPersonneId(personneId);
    }

    public List<String> getGroupesOfPersonne(Long personneId){
        return groupeRepository.findByPersonneId(personneId);
    }

}
