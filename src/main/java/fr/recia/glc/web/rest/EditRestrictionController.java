package fr.recia.glc.web.rest;

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

    public EditRestrictionController(RestrictionService restrictionService) {
        this.restrictionService = restrictionService;
    }

    @PostMapping("/etab/{uai}")
    public ResponseEntity<Void> editRestriction(@PathVariable String uai, @RequestBody RestrictionEtab request) {
        restrictionService.setNewRestriction(uai, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/etab/{uai}")
    public ResponseEntity<RestrictionEtab> listRestrictions(@PathVariable String uai){
        return restrictionService.getRestrictions(uai);
    }



}