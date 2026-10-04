package opsflow_backend.repository;

import opsflow_backend.entity.ProjectMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, Long> {

    @EntityGraph(attributePaths = {"user", "project"})
    List<ProjectMember> findByProjectId(Long projectId);

    @EntityGraph(attributePaths = {"user", "project"})
    Optional<ProjectMember> findByProjectIdAndUserId(
            Long projectId,
            Long userId);

    boolean existsByProjectIdAndUserId(
            Long projectId,
            Long userId);
}