package com.example.baedanguem.persist;

import com.example.baedanguem.persist.entity.CompanyEntity;
import com.example.baedanguem.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

    List<DividendEntity> findAllByCompanyId(Long companyId);

}
