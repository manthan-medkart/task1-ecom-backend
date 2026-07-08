package com.task.e_commerce.entities;

import com.task.e_commerce.entities.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Set<Roles> roles;
    private Long phoneNo;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(roles == null){
            return List.of();
        }
        List<SimpleGrantedAuthority> list =  roles.stream()
                .map(roles -> new SimpleGrantedAuthority("ROLE_" +roles.name()))
                .toList();

        System.out.println(list);

        return list;

    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @OneToMany(mappedBy = "userEntity")
    private List<CartEntity> cartEntity;

    @OneToMany(mappedBy = "userEntity")
    private List<OrderEntity> orderEntities;
}
