package com.example.baedanguem.persist.entity;

import com.example.baedanguem.model.Company;
import lombok.*;

import javax.persistence.*;

@Entity(name = "COMPANY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company) {

        this.ticker = company.getTicker();
        this.name = company.getName();

    }
}
