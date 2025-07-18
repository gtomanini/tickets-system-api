package com.br.tickets.models;

import com.br.tickets.models.base.UUIDIdEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends UUIDIdEntity implements UserDetails {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String address;
    private String avatar;
    private String zipcode;

    @Column(name = "city_id", insertable = false, updatable = false)
    private Long cityId;

    @Column(name = "state_id", insertable = false, updatable = false)
    private Long stateId;

    private LocalDate birthday;

    private String genre;

    private Boolean mailing;

    @Column(columnDefinition = "TEXT")
    private String extra;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
//
//    @ManyToMany
//    @JoinTable(
//            name = "order_reward",
//            joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
//            inverseJoinColumns = @JoinColumn(name = "reward_id")
//    )
//    private List<RewardEntity> rewards;
//
//    @Transient
//    public String getFirstName() {
//        if (this.name == null) return null;
//        return this.name.split(" ")[0];
//    }
//
//    public void setPassword(String password) {
//        this.password = password; // Placeholder - não use assim em produção!
//    }

}
