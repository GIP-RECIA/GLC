package fr.recia.glc.ldap.repository;

import fr.recia.glc.configuration.bean.CustomLdapProperties;
import fr.recia.glc.ldap.StructureSirenDomain;
import fr.recia.glc.ldap.StructureSirenDomainAttributesMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class LdapStructureDao {

    private LdapTemplate ldapTemplate;
    private StructureSirenDomainAttributesMapper structureSirenDomainAttributesMapper;
    private CustomLdapProperties ldapProperties;

    public List<StructureSirenDomain> structuresFromSiren() {
        return ldapTemplate.search(
            ldapProperties.getStructureBranch().getBase(),
            ldapProperties.getStructureBranch().getAllStructuresBySirenFilter(),
            structureSirenDomainAttributesMapper
        );
    }

}
