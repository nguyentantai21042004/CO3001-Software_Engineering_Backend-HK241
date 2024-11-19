package com.project.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "page_allocations")
@Data
@NoArgsConstructor
public class PageAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private byte semester;

    @Column(nullable = false)
    private short year;

    @Column(name = "number_of_pages", nullable = false)
    private int numberOfPages;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 15, nullable = false)
    private String status = "pending";

    @ManyToOne
    @JoinColumn(name = "spso_id", foreignKey = @ForeignKey(name = "page_allocation_spso_id_fk"))
    @OnDelete(action = OnDeleteAction.SET_NULL) // Correct usage of @OnDelete
    private SPSO spso;
}
