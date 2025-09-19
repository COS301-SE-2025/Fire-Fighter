package com.apex.firefighter.service.changelog.repository;

import com.apex.firefighter.service.changelog.model.Changelog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangelogRepository extends JpaRepository<Changelog, Long> {
}
