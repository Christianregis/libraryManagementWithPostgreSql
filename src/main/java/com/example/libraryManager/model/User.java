package com.example.libraryManager.model;

import com.example.libraryManager.dto.UserDto;
import com.example.libraryManager.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Entity(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String prenom;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String confirm_password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    private LocalDate dateInscription = LocalDate.now();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Borrow> borrows;

    public UserDto toDto(){
        return new UserDto(getName(), getPrenom(), getEmail(), getPassword(), getId(), getRole().name(), getConfirm_password());
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" +role.name()));
    }

    @Override
    @NullMarked
    public String getUsername() {
        return email;
    }

    @Override
    @NullMarked
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @NullMarked
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @NullMarked
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @NullMarked
    public boolean isEnabled() {
        return true;
    }
}
