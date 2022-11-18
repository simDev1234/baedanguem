package com.example.baedanguem.persist;

import com.example.baedanguem.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);

}
