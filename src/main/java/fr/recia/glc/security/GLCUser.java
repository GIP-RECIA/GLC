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

    private Map<String, Set<String>> rightsForEtabs;

    public GLCUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Set<String>> rightsForEtabs) {
        super(username, password, authorities);
        this.rightsForEtabs = rightsForEtabs;
    }

    public GLCUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}
