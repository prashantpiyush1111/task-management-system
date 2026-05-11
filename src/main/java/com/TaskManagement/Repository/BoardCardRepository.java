package com.TaskManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.BoardCard;

@Repository
public interface BoardCardRepository extends JpaRepository<BoardCard, Long> {

	List<BoardCard> findByBoardIdAndColumnIdOrderByPosition(Long boardId, Long columnId);

	Optional<BoardCard> findByIssueId(Long issueId);

	long countByBoardIdAndColumnId(Long boardId, Long columnId);
}