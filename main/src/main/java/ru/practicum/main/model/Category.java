package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "category")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_name")
    private String name;


    public Category(String name) {
        this.name = name;
    }
}
