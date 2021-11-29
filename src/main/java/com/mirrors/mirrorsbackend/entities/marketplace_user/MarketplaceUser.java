package com.mirrors.mirrorsbackend.entities.marketplace_user;

import com.mirrors.mirrorsbackend.mvc.profile.CountryEnum;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MarketplaceUser implements UserDetails {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id = UUID.randomUUID().toString();
    @Column(columnDefinition = "VARCHAR(60)", nullable = false)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private MarketplaceUserRole marketplaceUserRole;

    @Column(columnDefinition = "VARCHAR(32)", nullable = false)
    private String displayName;
    @Column(columnDefinition = "VARCHAR(25)")
    private String firstName;
    @Column(columnDefinition = "VARCHAR(25)")
    private String lastName;

    @Column(columnDefinition = "VARCHAR(15)")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private CountryEnum country;
    @Column(columnDefinition = "VARCHAR(85)")
    private String city;

    private boolean locked = false;
    private boolean enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(marketplaceUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
