package com.mirrors.mirrorsbackend.marketplaceuser;

import com.mirrors.mirrorsbackend.page.settings.CountryEnum;
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
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class MarketplaceUser implements UserDetails {

    @Id
    @Column(columnDefinition = "VARCHAR(255)")
    private String id = UUID.randomUUID().toString();
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private MarketplaceUserRole marketplaceUserRole;

    private String displayName;
    private String firstName;
    private String lastName;

    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private CountryEnum country;
    private String city;
    private String streetAddress;
    private String zipCode;

    private boolean locked = false;
    private boolean enabled = false;

    public MarketplaceUser(
                           String email,
                           String password,
                           MarketplaceUserRole marketplaceUserRole) {
        this.email = email;
        this.password = password;
        this.marketplaceUserRole = marketplaceUserRole;
    }

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
