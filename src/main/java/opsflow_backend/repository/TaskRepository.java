package opsflow_backend.repository;

import opsflow_backend.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"project", "assignee"})
    List<Task> findByProjectId(Long projectId);

    @EntityGraph(attributePaths = {"project", "assignee"})
    Optional<Task> findById(Long id);

    @EntityGraph(attributePaths = {"project", "assignee"})
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}