/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.glc.services.db;

import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.common.CleJointure;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.gestion.AnneeScolaire;
import fr.recia.glc.db.entities.gestion.GenUID;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.Eleve;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.Sexe;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.gestion.AnneeScolaireRepository;
import fr.recia.glc.db.repositories.gestion.GenUIDRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.services.NameCalculator;
import fr.recia.glc.services.PasswordGenerator;
import fr.recia.glc.services.UidFactory;
import fr.recia.glc.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PersonneService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private GenUIDRepository<GenUID> genUIDRepository;
    @Autowired
    private AnneeScolaireRepository<AnneeScolaire> anneeScolaireRepository;
    @Autowired
    private UidFactory uidFactory;
    @Autowired
    private NameCalculator nameCalculator;
    @Autowired
    private PasswordGenerator passwordGenerator;

    public List<SimplePersonneDto> searchPersonne(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdmin(name) : aPersonneRepository.findByNameLike(name);
    }

    @Cacheable(value = "personnesByEtablissement")
    public List<SimplePersonneDto> getPersonnes(Long structureId) {
        log.trace("getPersonnes for {}", structureId);
        final List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(structureId);
        if (personnesIds.isEmpty()) return List.of();
        return aPersonneRepository.findByPersonneIds(new HashSet<>(personnesIds));
    }

    @Cacheable(value = "personne")
    public PersonneDto getPersonne(Long id) {
        log.trace("getPersonne for {}", id);
        return aPersonneRepository.findByPersonneId(id);
    }

    /**
     * Création d'une personne dans la base sarapis
     * @param userCreation Le DTO venant du front qui contient les informations nécéssaires à la création
     */
    public void addPersonne(UserCreation userCreation){
        // Récupération de l'année scolaire actuelle
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findAll().get(anneeScolaireRepository.findAll().size()-1);
        final String anneeEnCours = anneeScolaire.getAnneeEnCours().toString().split("-")[0];
        // Génération de l'UID
        // 1. Récupération du genUID correspondant en fonction du domaine de l'établissement
        // TODO : codeGenerateur variable
        GenUID genUID = genUIDRepository.findByCAndLAndXx(uidFactory.getCodeGenerateur(), uidFactory.getCodeRegion(), uidFactory.codeAnnee(anneeEnCours)).get();
        // 2. Création de l'uid
        String uid = uidFactory.uid(anneeEnCours, genUID.getIiii()+1);
        // 3. Récupération de la clé de jointure créée
        String cle = uidFactory.clee(uid);
        // Récupération de l'établissement
        AStructure aStructure = aStructureRepository.getReferenceById(userCreation.getStructureRattachement());
        String source = uidFactory.getSource(aStructure.getCleJointure().getSource());
        // Création de la personne avec les bons attributs
        CategoriePersonne catPer = userCreation.getCategoriePersonne();
        Civilite civilite = userCreation.getCivilite();
        // TODO : instancier le bon type d'objet
        APersonne apersonne = new Eleve();
        Instant date = new Date().toInstant();
        apersonne.setDateCreation(date);
        apersonne.setDateModification(date);
        apersonne.setAnneeScolaire(anneeScolaire.getAnneeEnCours());
        apersonne.setCategorie(catPer);
        apersonne.setCleJointure(new CleJointure(source, cle));
        apersonne.setDateNaissance(userCreation.getDateNaissance());
        apersonne.setDisplayName(userCreation.getPrenom());
        apersonne.setCivilite(civilite);
        if(apersonne.getCivilite() == Civilite.M){
            apersonne.setSexe(Sexe.M);
        } else {
            apersonne.setSexe(Sexe.F);
        }
        // Le mail des eleves est géré différement
        if (!catPer.equals(CategoriePersonne.Eleve)) {
            apersonne.setEmail(userCreation.getCourriel());
        } else {
            apersonne.setEmailPersonnel(userCreation.getCourriel());
        }
        apersonne.setEtat(Etat.Invalide);
        apersonne.setGivenName(userCreation.getPrenom());
        apersonne.setSn(userCreation.getNom());
        apersonne.setUid(uid);
        apersonne.setStructRattachement(aStructure);
        apersonne.setDoForward(false);
        apersonne.setUuid(UUID.randomUUID().toString());
        // Champs calculés
        apersonne.setPassword(passwordGenerator.genPassword());
        apersonne.setCn(nameCalculator.cn(userCreation.getNom(), userCreation.getPrenom()));
        apersonne.setDisplayName(nameCalculator.display(userCreation.getNom(), userCreation.getPrenom()));
        // TODO : login
        //apersonne.setLogin(nameCalculator.login(userCreation.getNom(), userCreation.getPrenom()));

        // Sauvegarder la personne
        aPersonneRepository.saveAndFlush(apersonne);
        log.info("local personne {} created", uid);
        // Si c'est bon pour la personne alors on met aussi à jour le genuid
        genUID.setIiii(genUID.getIiii()+1);
        genUIDRepository.saveAndFlush(genUID);
    }

    public SimplePersonneDto getPersonneSimple(Long id) {
        return aPersonneRepository.findByPersonneIdSimple(id);
    }

    public boolean isInStructure(Long id, Long structureId) {
        return aPersonneAStructureRepository.isInStructure(id, structureId) > 0;
    }

    public boolean hasOfficialFunctionsInStructure(Long id, Long structureId) {
        return fonctionRepository.nbOfficialFonctionsInStructure(id, structureId) > 0;
    }

    public boolean hasAdditionalFunctionsInStructure(Long id, Long structureId) {
        return fonctionRepository.nbAdditionalFonctionsInStructure(id, structureId) > 0;
    }

    public boolean hasAdditionalFunctionsInStructure(Long id, Long structureId, List<Long> fonctionsIds) {
        return fonctionRepository.nbAdditionalFonctionsInStructure(id, structureId, fonctionsIds) > 0;
    }

    public boolean hasFunctionsInStructure(Long id, Long structureId, List<Long> fonctionsIds) {
        return this.hasOfficialFunctionsInStructure(id, structureId) ||
                this.hasAdditionalFunctionsInStructure(id, structureId, fonctionsIds);
    }

}
