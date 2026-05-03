package com.eduproject.modules.roles.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ROLE")
@Builder
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
//    @Enumerated(EnumType.STRING)
    private String name; //i think this should be stored in DB only with upper case.

    @Column(unique = true, nullable = false, length = 100)
    private String description;
}
