package com.josh_demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Column(name = "name",length = 100, unique = true, nullable = false)
    private String name;
}
