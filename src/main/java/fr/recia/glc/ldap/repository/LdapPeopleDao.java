package fr.recia.glc.ldap.repository;

import fr.recia.glc.configuration.bean.CustomLdapProperties;
import fr.recia.glc.ldap.StructureSirenDomain;
import fr.recia.glc.ldap.StructureSirenDomainAttributesMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class LdapPeopleDao {

    private LdapTemplate ldapTemplate;
    private CustomLdapProperties ldapProperties;

    public void lockPerson(String uid) {
        List<Name> dns = ldapTemplate.search(
            "ou=people",
            "(uid=" + uid + ")",
            (ContextMapper<Name>) ctx -> ((DirContextAdapter) ctx).getDn()
        );
        if (!dns.isEmpty()) {
            Name dn = dns.get(0);
            ModificationItem[] mods = new ModificationItem[] {
                new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("ESCOPersonEtatCompte", "BLOQUE")
                )
            };
            ldapTemplate.modifyAttributes(dn, mods);
        }
    }

    public void unlockPerson(String uid) {
        List<Name> dns = ldapTemplate.search(
            "ou=people",
            "(uid=" + uid + ")",
            (ContextMapper<Name>) ctx -> ((DirContextAdapter) ctx).getDn()
        );
        if (!dns.isEmpty()) {
            Name dn = dns.get(0);
            ModificationItem[] mods = new ModificationItem[] {
                new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("ESCOPersonEtatCompte", "VALIDE")
                )
            };
            ldapTemplate.modifyAttributes(dn, mods);
        }
    }

}
