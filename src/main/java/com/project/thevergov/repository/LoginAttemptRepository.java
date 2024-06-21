package com.project.thevergov.repository;


import com.project.thevergov.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {


    @Query("SELECT la FROM LoginAttempt la WHERE la.email = :email ORDER BY la.createdAt DESC")
    List<LoginAttempt> findRecent(@Param("email") String username);

}
