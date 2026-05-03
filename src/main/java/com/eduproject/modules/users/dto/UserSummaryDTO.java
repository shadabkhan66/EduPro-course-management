package com.eduproject.modules.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Safe, read-only snapshot for nesting in other API responses (e.g. course instructor).
 * No password, no internal JPA graph.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
