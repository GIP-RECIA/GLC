package fr.recia.glc.ldap.repository;

import fr.recia.glc.ldap.StructureSirenDomain;
import fr.recia.glc.ldap.StructureSirenDomainAttributesMapper;
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
    private StructureSirenDomainAttributesMapper structureSirenDomainAttributesMapper;

    public List<StructureSirenDomain> structuresFromSiren(){
        // TODO : paramétriser le filtre et la base
        String base = "ou=structures";
        String filter = "(!(objectClass=ENTEntreprise))";
        return ldapTemplate.search(
                base,
                filter,
                structureSirenDomainAttributesMapper
        );
    }

}
