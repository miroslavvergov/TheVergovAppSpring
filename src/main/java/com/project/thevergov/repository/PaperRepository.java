package com.project.thevergov.repository;

import com.project.thevergov.dto.api.iPaper;
import com.project.thevergov.entity.PaperEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.project.thevergov.constant.Constants.*;

public interface PaperRepository extends JpaRepository<PaperEntity, Long> {



    @Query(countQuery = SELECT_COUNT_PAPERS_QUERY, value = SELECT_PAPERS_QUERY, nativeQuery = true)
    Page<iPaper> findPapers(Pageable pageable);


    @Query(countQuery = SELECT_COUNT_PAPERS_BY_NAME_QUERY, value = SELECT_PAPERS_BY_NAME_QUERY, nativeQuery = true)
    Page<iPaper> findPapersByName(@Param("paperName") String paperName, Pageable pageable);


    @Query(value = SELECT_PAPER_QUERY, nativeQuery = true)
    Optional<iPaper> findPaperByPaperId(String paperId);

    Optional<PaperEntity> findByPaperId(String paperId);

    Optional<PaperEntity> findByName(String name);
}
