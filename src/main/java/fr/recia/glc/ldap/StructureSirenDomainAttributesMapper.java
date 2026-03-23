package fr.recia.glc.ldap;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

@Service
public class StructureSirenDomainAttributesMapper implements AttributesMapper<StructureSirenDomain> {

    @Override
    public StructureSirenDomain mapFromAttributes(Attributes attrs) throws NamingException {
        StructureSirenDomain entry = new StructureSirenDomain();
        Attribute sirenAttr = attrs.get("ENTStructureSIREN");
        if (sirenAttr != null) {
            entry.setSiren((String) sirenAttr.get());
        }
        Attribute domainesAttr = attrs.get("ESCODomaines");
        if (domainesAttr != null) {
            List<String> domaines = new ArrayList<>();
            NamingEnumeration<?> allValues = domainesAttr.getAll();
            while (allValues.hasMore()) {
                domaines.add((String) allValues.next());
            }
            entry.setDomains(domaines);
        }

        return entry;
    }
}
