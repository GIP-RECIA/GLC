package fr.recia.glc.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class GLCUser extends User {

    private Map<GLCRole, Set<String>> rightsForEtabs;
    private Set<GLCRole> rightsForColl;

    public GLCUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<GLCRole, Set<String>> rightsForEtabs, Set<GLCRole> rightsForColl) {
        super(username, password, authorities);
        this.rightsForEtabs = rightsForEtabs;
        this.rightsForColl = rightsForColl;
    }

    public GLCUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}
