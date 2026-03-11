package fr.recia.glc.web.rest;

import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.services.access.RestrictionService;
import fr.recia.glc.web.dto.restriction.RestrictionEtab;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restriction")
public class EditRestrictionController {

    private final RestrictionService restrictionService;
    private final AStructureRepository<AStructure> aStructureRepository;

    public EditRestrictionController(RestrictionService restrictionService, AStructureRepository<AStructure> aStructureRepository) {
        this.restrictionService = restrictionService;
        this.aStructureRepository = aStructureRepository;
    }

    @PostMapping("/etab/{id}")
    public ResponseEntity<Void> editRestriction(@PathVariable Long id, @RequestBody RestrictionEtab request) {
        final AStructure aStructure = aStructureRepository.findById(id).orElse(null);
        restrictionService.setNewRestriction(((Etablissement)aStructure).getUai(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/etab/{id}")
    public ResponseEntity<RestrictionEtab> listRestrictions(@PathVariable Long id){
        final AStructure aStructure = aStructureRepository.findById(id).orElse(null);
        return ResponseEntity.ok(restrictionService.getRestrictions(((Etablissement)aStructure).getUai()));
    }



}