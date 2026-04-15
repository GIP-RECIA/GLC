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
package fr.recia.glc.ldap.repository;

import fr.recia.glc.configuration.bean.CustomLdapProperties;
import fr.recia.glc.ldap.LdapUser;
import fr.recia.glc.ldap.mappers.PeopleResponseAttributesMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Data
@AllArgsConstructor
@Slf4j
public class LdapPeopleDao {

    private LdapTemplate ldapTemplate;
    private CustomLdapProperties ldapProperties;
    private PeopleResponseAttributesMapper peopleResponseAttributesMapper;

    public void lockPerson(String uid) {
        List<Name> dns = ldapTemplate.search(
            query()
                .base(ldapProperties.getUserBranch().getBaseDN())
                .where("uid").is(uid),
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
            query()
                .base(ldapProperties.getUserBranch().getBaseDN())
                .where("uid").is(uid),
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

    public void putInDeleteState(String uid){
        List<Name> dns = ldapTemplate.search(
            query()
                .base(ldapProperties.getUserBranch().getBaseDN())
                .where("uid").is(uid),
            (ContextMapper<Name>) ctx -> ((DirContextAdapter) ctx).getDn()
        );
        if (!dns.isEmpty()) {
            Name dn = dns.get(0);
            ModificationItem[] mods = new ModificationItem[] {
                new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("ESCOPersonEtatCompte", "DELETE")
                )
            };
            ldapTemplate.modifyAttributes(dn, mods);
        }
    }

    public void undoDelete(String uid){
        List<Name> dns = ldapTemplate.search(
            query()
                .base(ldapProperties.getUserBranch().getBaseDN())
                .where("uid").is(uid),
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

    // TODO : cache sur cette méthode ?
    public LdapUser getLdapUser(String uid){
        List<LdapUser> result = ldapTemplate.search(
            query()
                .base(ldapProperties.getUserBranch().getBaseDN())
                .where("uid").is(uid),
            peopleResponseAttributesMapper
        );
        return result.isEmpty() ? null : result.get(0);
    }

}
