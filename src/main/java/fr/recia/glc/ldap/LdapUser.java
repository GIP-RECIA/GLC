package fr.recia.glc.ldap;

import lombok.Data;

import java.util.List;

@Data
public class LdapUser {
    private String sirenCourant;
    private List<String> groups;
}
