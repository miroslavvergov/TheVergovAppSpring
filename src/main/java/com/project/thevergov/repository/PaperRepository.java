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


    // TODO
    //SELECT paper.id,
    // paper.paper_id,
    // paper.name,
    // paper.description,
    // paper.uri,
    // paper.icon,
    // paper.size,
    // paper.formatted_size,
    // paper.extension,
    // paper.reference_id,
    // paper.created_at,
    // paper.updated_at
    // CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name,
    // owner.email AS owner.email,
    // owner.phone AS owner.phone,
    // owner.last_login AS owner_last_login,
    // CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name,
    // FROM papers paper
    // JOIN users owner ON owner.id = paper.created_by
    // JOIN users updater ON updater.id = paper.updated_by
    @Query(countQuery = SELECT_COUNT_PAPERS_QUERY, value = SELECT_PAPERS_QUERY, nativeQuery = true)
    Page<iPaper> findPapers(Pageable pageable);


    @Query(countQuery = SELECT_COUNT_PAPERS_BY_NAME_QUERY, value = SELECT_PAPERS_BY_NAME_QUERY, nativeQuery = true)
    Page<iPaper> findPapersByName(@Param("paperName") String paperName, Pageable pageable);


    @Query(value = SELECT_PAPER_QUERY, nativeQuery = true)
    Optional<iPaper> findPaperByPaperId(String paperId);

    Optional<PaperEntity> findPaperById(String paperId);
}
