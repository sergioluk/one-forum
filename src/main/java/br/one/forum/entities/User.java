package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public final class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @Size(max = 255)
    @Column(unique = true, nullable = false)
    @ToString.Include
    private String email;

    @NotNull
    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Instant createdAt = Instant.now();

    @Column(name = "update_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updateAt = Instant.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @ManyToMany(mappedBy = "likedBy", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<Topic> likedTopics = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Topic> createdTopics = new HashSet<>();

    public User(@NotNull String email, @NotNull String password, @NotNull Profile profile) {
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public User(String login, String password) {
        this.email = login;
        this.password = password;
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null)
            createdAt = Instant.now();
    }

    @PreUpdate
    private void onUpdate() {
        updateAt = Instant.now();
    }

    @ToString.Include(name = "password")
    private String maskedPassword() {
        return "[PROTECTED]";
    }



    private UserRole role;

    public User( String login, String password, UserRole role) {
        this.email = login;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //return List.of();
        /*if (this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));*/
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        //return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        //return UserDetails.super.isEnabled();
        return true;
    }
}
