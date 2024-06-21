package com.project.thevergov.repository;

import com.project.thevergov.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//this annotation is not required
@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {


    Optional<CredentialEntity> getCredentialByUserEntityId(Long userId);
}
