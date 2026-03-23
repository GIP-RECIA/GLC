package fr.recia.glc.services.db;

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.db.entities.common.CleJointure;
import fr.recia.glc.db.entities.education.Enseignement;
import fr.recia.glc.db.entities.education.MEF;
import fr.recia.glc.db.entities.education.MappingEleveEnseignement;
import fr.recia.glc.db.entities.gestion.AnneeScolaire;
import fr.recia.glc.db.entities.gestion.GenUID;
import fr.recia.glc.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import fr.recia.glc.db.entities.groupe.Classe;
import fr.recia.glc.db.entities.groupe.MappingAGroupeAPersonne;
import fr.recia.glc.db.entities.groupe.MappingAGroupeAPersonneEnseignement;
import fr.recia.glc.db.entities.groupe.MappingAGroupeAPersonneEnseignementId;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.Eleve;
import fr.recia.glc.db.entities.personne.Enseignant;
import fr.recia.glc.db.entities.personne.Login;
import fr.recia.glc.db.entities.personne.NonEnseignantEtablissement;
import fr.recia.glc.db.entities.personne.NonEnseignantServiceAcademique;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.Sexe;
import fr.recia.glc.db.repositories.education.EnseignementRepository;
import fr.recia.glc.db.repositories.education.MEFRepository;
import fr.recia.glc.db.repositories.gestion.AnneeScolaireRepository;
import fr.recia.glc.db.repositories.gestion.GenUIDRepository;
import fr.recia.glc.db.repositories.groupe.AGroupeOfFoncClasseGroupeRepository;
import fr.recia.glc.db.repositories.groupe.ClasseRepository;
import fr.recia.glc.db.repositories.groupe.MappingAGroupeAPersonneEnseignementRepository;
import fr.recia.glc.db.repositories.groupe.MappingAGroupeAPersonneRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.personne.LoginRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.ldap.Structure;
import fr.recia.glc.ldap.repository.LdapStructureDao;
import fr.recia.glc.services.NameCalculator;
import fr.recia.glc.services.PasswordGenerator;
import fr.recia.glc.services.UidFactory;
import fr.recia.glc.web.dto.FonctionAction;
import fr.recia.glc.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AddPersonneService {

    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private GenUIDRepository<GenUID> genUIDRepository;
    @Autowired
    private AnneeScolaireRepository<AnneeScolaire> anneeScolaireRepository;
    @Autowired
    private LoginRepository<Login> loginRepository;
    @Autowired
    private MEFRepository<MEF> mefRepository;
    @Autowired
    private EnseignementRepository<Enseignement> enseignementRepository;
    @Autowired
    private ClasseRepository<Classe> classeRepository;
    @Autowired
    private MappingAGroupeAPersonneRepository<MappingAGroupeAPersonne> mappingAGroupeAPersonneRepository;
    @Autowired
    private MappingAGroupeAPersonneEnseignementRepository<MappingAGroupeAPersonneEnseignement> mappingAGroupeAPersonneEnseignementRepository;
    @Autowired
    private AGroupeOfFoncClasseGroupeRepository<AGroupeOfFoncClasseGroupe> aGroupeOfFoncClasseGroupeRepository;
    @Autowired
    private UidFactory uidFactory;
    @Autowired
    private NameCalculator nameCalculator;
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private LdapStructureDao ldapStructureDao;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private GLCProperties glcProperties;

    /**
     * Création d'une personne dans la base sarapis
     * @param userCreation Le DTO venant du front qui contient les informations nécéssaires à la création
     */
    public void addPersonne(UserCreation userCreation){
        log.debug("Trying to create local user {}", userCreation);
        // 1. Récupération de la date
        Instant date = new Date().toInstant();
        // 2. Récupération de l'année scolaire actuelle (on suppose que c'est la dernière)
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findAll().get(anneeScolaireRepository.findAll().size()-1);
        final String anneeEnCours = anneeScolaire.getAnneeEnCours().toString().split("-")[0];
        final String codeAnnee = uidFactory.codeAnnee(anneeEnCours);
        log.debug("Annee en cours : {}", codeAnnee);
        // 3. Récupération de l'établissement
        AStructure aStructure = aStructureRepository.getReferenceById(userCreation.getStructureRattachement());
        String source = uidFactory.getSource(aStructure.getCleJointure().getSource());
        log.debug("Source : {}", source);
        // 4. Génération de l'UID
        // 4.1. Récupération du genUID correspondant en fonction du domaine de l'établissement
        // TODO : cache
        Structure structure = ldapStructureDao.structureFromSiren(aStructure.getSiren());
        String codeGenerateur;
        if(structure.getDomains().size()==1){
            codeGenerateur = glcProperties.getUidFactory().getDomainToCodeGenerateur().get(structure.getDomains().get(0));
        } else {
            codeGenerateur = glcProperties.getUidFactory().getDefaultCodeGenerateur();
        }
        log.debug("codeGenerateur : {}", codeGenerateur);
        // 4.2. Créer un nouveau genUID si il n'y a pas encore de genUID pour le codeGenerateur cette année
        GenUID genUID = genUIDRepository.findByCAndLAndXx(codeGenerateur, uidFactory.getCodeRegion(), codeAnnee).orElseGet(() -> {
            log.debug("No genUID : creating a new one");
            GenUID newGenUID = new GenUID();
            newGenUID.setDateCreation(date);
            newGenUID.setDateModification(date);
            newGenUID.setIiii(0);
            newGenUID.setXx(codeAnnee);
            newGenUID.setL(uidFactory.getCodeRegion());
            newGenUID.setC(codeGenerateur);
            return genUIDRepository.saveAndFlush(newGenUID);
        });
        log.debug("genUID : {}", genUID);
        // 4.3. Création de l'uid
        final int increment = genUID.getIiii()+1;
        log.debug("increment {}", increment);
        String uid = uidFactory.uid(anneeEnCours, increment, codeGenerateur);
        log.debug("uid generated : {}", uid);
        // 4.4. Récupération de la clé de jointure créée
        String cle = uidFactory.clee(uid);
        log.debug("cle : {}", cle);
        // 5. Création de la personne avec les bons attributs
        CategoriePersonne catPer = userCreation.getCategoriePersonne();
        Civilite civilite = userCreation.getCivilite();
        // TODO : instancier le bon type d'objet
        APersonne apersonne;
        if(catPer == CategoriePersonne.Eleve){
            apersonne = new Eleve();
        }
        else if(catPer == CategoriePersonne.Non_enseignant_etablissement){
            apersonne = new NonEnseignantEtablissement();
        }
        else if(catPer == CategoriePersonne.Non_enseignant_service_academique){
            apersonne = new NonEnseignantServiceAcademique();
        }
        else if(catPer == CategoriePersonne.Enseignant){
            apersonne = new Enseignant();
        }
        // Type invalide
        else {
            throw new RuntimeException("Invalid CategoriePersonne");
        }
        apersonne.setDateCreation(date);
        apersonne.setDateModification(date);
        apersonne.setAnneeScolaire(anneeScolaire.getAnneeEnCours());
        apersonne.setCategorie(catPer);
        apersonne.setCleJointure(new CleJointure(source, cle));
        apersonne.setDateNaissance(userCreation.getDateNaissance());
        apersonne.setDisplayName(userCreation.getPrenom());
        apersonne.setCivilite(civilite);
        Set<AStructure> listeStructures = new HashSet<>();
        listeStructures.add(aStructure);
        apersonne.setListeStructures(listeStructures);
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
        // 6. Sauvegarder la personne
        aPersonneRepository.saveAndFlush(apersonne);
        log.debug("local personne {} created", uid);
        // Si c'est bon pour la personne alors on met aussi à jour le genuid
        genUID.setIiii(increment);
        genUIDRepository.saveAndFlush(genUID);
        // Gestion du login
        updateLogin(nameCalculator.login(userCreation.getNom(), userCreation.getPrenom()), apersonne);
        // TODO : champs spécfiques pour chaque catégorie de personne
        if(apersonne.getCategorie() == CategoriePersonne.Eleve){
            updateEleve((Eleve) apersonne, userCreation);
        }
        if(apersonne.getCategorie() == CategoriePersonne.Non_enseignant_etablissement){
            updateNonEnsEtablissement((NonEnseignantEtablissement) apersonne, aStructure, userCreation);
        }
        if(apersonne.getCategorie() == CategoriePersonne.Non_enseignant_service_academique){
            updateNonEnsServiceAcad((NonEnseignantServiceAcademique) apersonne, aStructure, userCreation);
        }
        if(apersonne.getCategorie() == CategoriePersonne.Enseignant){
            updateEnseignant((Enseignant) apersonne, aStructure, userCreation);
        }
    }

    /**
     * Ajout des attributs spécifiques à un nonEnseignantEtablissement
     */
    private void updateNonEnsEtablissement(final NonEnseignantEtablissement nonEnseignantEtablissement, final AStructure aStructure, final UserCreation userCreation){
        log.debug("updating nonEnseignantEtablissement {}", nonEnseignantEtablissement.getUid());
        fonctionService.saveAdditionalFonctions(nonEnseignantEtablissement.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save);
    }

    /**
     * Ajout des attributs spécifiques à un nonEnseignantServiceAcademique
     */
    private void updateNonEnsServiceAcad(final NonEnseignantServiceAcademique nonEnseignantServiceAcademique, final AStructure aStructure, final UserCreation userCreation){
        log.debug("updating nonEnseignantServiceAcademique {}", nonEnseignantServiceAcademique.getUid());
        fonctionService.saveAdditionalFonctions(nonEnseignantServiceAcademique.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save);
    }

    /**
     * Ajout des attributs spécifiques à un enseignant
     */
    private void updateEnseignant(final Enseignant enseignant, final AStructure aStructure, final UserCreation userCreation){
        log.debug("updating enseignant {}", enseignant.getUid());
        fonctionService.saveAdditionalFonctions(enseignant.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save);
        List<MappingAGroupeAPersonneEnseignement> personneEnseignements = new ArrayList<>();
        for(Long groupId : userCreation.getGroupesEns()){
            MappingAGroupeAPersonneEnseignement personneEnseignement = new MappingAGroupeAPersonneEnseignement();
            MappingAGroupeAPersonneEnseignementId pk = new MappingAGroupeAPersonneEnseignementId();
            pk.setEnseignant(enseignant);
            Enseignement enseignement = enseignementRepository.getReferenceById(userCreation.getEnseignementProf());
            pk.setEnseignement(enseignement);
            AGroupeOfFoncClasseGroupe aGroupeOfFoncClasseGroupe = aGroupeOfFoncClasseGroupeRepository.getReferenceById(groupId);
            pk.setGroupe(aGroupeOfFoncClasseGroupe);
            personneEnseignement.setPk(pk);
            personneEnseignement.setSource(enseignant.getCleJointure().getSource());
            personneEnseignements.add(personneEnseignement);
        }
        mappingAGroupeAPersonneEnseignementRepository.saveAllAndFlush(personneEnseignements);
    }

    /**
     * Ajout des attributs spécifiques à un élève
     */
    private void updateEleve(final Eleve eleve, final UserCreation userCreation) {
        log.debug("updating eleve {}", eleve.getUid());
        eleve.setStatut(userCreation.getStatut());
        eleve.setRegime(userCreation.getRegime());
        eleve.setMajeur(userCreation.isMajeur());
        eleve.setMajeurAnticipe(userCreation.isMajeurAnticipe());
        eleve.setTransport(userCreation.isTransportScolaire());
        MEF mef = mefRepository.getReferenceById(userCreation.getMefEleve());
        log.debug("mef for eleve is {}", mef.getId());
        eleve.setMef(mef);
        Set<MappingEleveEnseignement> mappingEleveEnseignements = new HashSet<>();
        for(Long enseignementId: userCreation.getEnseignements()){
            Enseignement enseignement = enseignementRepository.getReferenceById(enseignementId);
            log.debug("Adding new enseignement {}", enseignement);
            mappingEleveEnseignements.add(new MappingEleveEnseignement(eleve.getCleJointure().getSource(), enseignement));
        }
        log.debug("All enseignements are {}", mappingEleveEnseignements);
        eleve.setEnseignements(mappingEleveEnseignements);
        aPersonneRepository.saveAndFlush(eleve);
        log.debug("Saved enseignements...");
        // On fait par le MappingAGroupeAPersonne car on ne peut pas faire via la classe
        Classe classe = classeRepository.getReferenceById(userCreation.getClasse());
        log.debug("Adding {} to class {}", eleve.getUid(), classe.getId());
        MappingAGroupeAPersonne mappingAGroupeAPersonne = new MappingAGroupeAPersonne(eleve.getCleJointure().getSource(), eleve, classe);
        mappingAGroupeAPersonneRepository.saveAndFlush(mappingAGroupeAPersonne);
        log.debug("Saved classe...");
    }

    /**
     * Trouve et modifie le login d'une personne.
     * Le login passé en paramêtre ne doit pas avoir de compteur à la fin
     * Il doit être de la forme prenom.nom
     */
    private void updateLogin(final String loginPrefix, final APersonne aPersonne) {
        Pattern p = Pattern.compile(loginPrefix + "(\\d*)");
        Matcher m;
        Instant date = new Date().toInstant();
        List<Login> loginList = loginRepository.findByNomLike(loginPrefix+"%");
        String newLogin = loginPrefix;
        int nbMax = 0;
        int nb = 0;
        boolean[] cptUtilise = new boolean[100];
        if (loginList != null) {
            for (Login l : loginList) {
                m = p.matcher(l.getNom());
                if (m.matches()) {
                    if ("".equals(m.group(1))) {
                        nb = 0;
                    } else {
                        nb = Integer.parseInt(m.group(1));
                    }
                    if (nb < 100) {
                        cptUtilise[nb++] = true;
                    }

                    if (nbMax < nb) {
                        nbMax = nb;
                    }
                }
            }
        }
        if (nbMax > 0) {
            if (nbMax < 100) {
                newLogin = String.format("%s%d", newLogin, nbMax);
            } else {
                // on cherche le premier disponible.
                for (int i = 0; i < cptUtilise.length; i++) {
                    if (!cptUtilise[i]){
                        nbMax = i;
                        newLogin = String.format("%s%d", newLogin, i);
                        break;
                    }
                }
                if (nbMax > 100) {
                    // TODO : throw exception
                    log.error("Login too high");
                }
            }
        }
        Login login = new Login(newLogin);
        login.setApersonneLogin(aPersonne);
        login.setDateCreation(date);
        login.setDateModification(date);
        loginRepository.saveAndFlush(login);
    }

}
