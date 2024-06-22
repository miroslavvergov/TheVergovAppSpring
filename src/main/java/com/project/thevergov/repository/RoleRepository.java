package com.project.thevergov.repository;

import com.project.thevergov.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//this annotation is not required
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByNameIgnoreCase(String name);

    Optional<RoleEntity> findRoleEntityByName(String name);
}
