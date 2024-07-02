package com.project.thevergov.repository;


import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


//this annotation is not required
@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

    Optional<ConfirmationEntity> findByTokenKey(String token);

    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
}
