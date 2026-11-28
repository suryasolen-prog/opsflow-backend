package opsflow_backend.repository;

import opsflow_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByNameAndDescriptionOrderByCreatedAtAscIdAsc(
            String name, String description);
}