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
public class StructureAttributesMapper implements AttributesMapper<Structure> {

    @Override
    public Structure mapFromAttributes(Attributes attrs) throws NamingException {
        Structure entry = new Structure();
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
