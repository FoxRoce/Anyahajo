package com.project.anyahajo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ah_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    @Embedded
    private Name name;
    private String email;
    private String password;

    private String phoneNumber;
    private Boolean locked = false;
    private Boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    private LocalDateTime tokenExpiration;

    @ElementCollection
    @Column(name = "item_id")
    @CollectionTable(name = "ah_user_basket", joinColumns = @JoinColumn(name = "owner_id"))
    private Set<Long> basket = new LinkedHashSet<>();


    public void addItemToBasket(Item item) {
        basket.add(item.getItem_id());
    }
    public void deleteItemFromBasket(Item item) {
        basket.remove(item.getItem_id());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> authorities = new LinkedList<>();
        if (enabled) {
            if (this.role.equals(Role.ADMIN)) {
                authorities.add(new SimpleGrantedAuthority(Role.Code.ADMIN));
            }
            authorities.add(new SimpleGrantedAuthority(Role.Code.USER));
        }
        return authorities;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public Set<Long> getBasket() {
        return basket;
    }

    public void setBasket(Set<Long> basket) {
        this.basket = basket;
    }
}
