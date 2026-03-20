package fr.recia.glc.ldap.repository;

import fr.recia.glc.ldap.Structure;
import fr.recia.glc.ldap.StructureAttributesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LdapStructureDao {

    @Autowired
    private LdapTemplate ldapTemplate;
    @Autowired
    private StructureAttributesMapper structureAttributesMapper;

    public Structure structureFromSiren(String siren) {
        // TODO : paramétriser le filtre et la base
        String base = "ou=structures";
        String filter = "(ENTStructureSIREN="+siren+")";
        List<Structure> results = ldapTemplate.search(
                base,
                filter,
                structureAttributesMapper
        );
        return results.get(0);
    }

}
