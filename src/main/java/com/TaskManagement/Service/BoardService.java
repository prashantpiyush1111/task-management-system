package com.TaskManagement.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagement.Entity.Board;
import com.TaskManagement.Entity.BoardCard;
import com.TaskManagement.Entity.BoardColumn;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Repository.BoardCardRepository;
import com.TaskManagement.Repository.BoardColumnRepository;
import com.TaskManagement.Repository.BoardRepository;
import com.TaskManagement.Repository.IssueRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepo;

	@Autowired
	private BoardColumnRepository boardColumnRepo;

	@Autowired
	private BoardCardRepository boardCardRepo;

	@Autowired
	private IssueRepository issueRepo;

	public Board createBoard(Board board) {
		return boardRepo.save(board);
	}

	public Optional<Board> getByBoardId(Long id) {
		return boardRepo.findById(id);
	}

	public List<BoardColumn> getBoardColumns(Long boardId) {
		return boardColumnRepo.findByBoardIdOrderByPosition(boardId);
	}

	public List<BoardCard> getBoardByCards(Long boardId, Long columnId) {
		return boardCardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, columnId);
	}

	public Optional<Board> findById(Long id) {
		return boardRepo.findById(id);
	}

	@Transactional
	public BoardCard addIssueToBoard(Long boardId, Long columnId, Long issueId) {

		issueRepo.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found"));

		boardCardRepo.findByIssueId(issueId).ifPresent(boardCardRepo::delete);

		BoardColumn column = boardColumnRepo.findById(columnId)
				.orElseThrow(() -> new RuntimeException("Column not found"));

		if (column.getWipLimit() != null && column.getWipLimit() > 0) {
			long count = boardCardRepo.countByBoardIdAndColumnId(boardId, columnId);
			if (count >= column.getWipLimit()) {
				throw new RuntimeException("WIP limit exceeded for column: " + column.getName());
			}
		}

		List<BoardCard> existing = boardCardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, columnId);
		int position = existing.size();

		BoardCard card = new BoardCard();
		card.setBoardId(boardId);
		card.setColumn(column);
		card.setIssueId(issueId);
		card.setPosition(position);

		return boardCardRepo.save(card);
	}

	@Transactional
	public void moveCards(Long boardId, Long columnId, Long cardId, int position, String performedBy) {

		BoardCard card = boardCardRepo.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));

		BoardColumn fromColumn = card.getColumn();
		BoardColumn toColumn = boardColumnRepo.findById(columnId)
				.orElseThrow(() -> new RuntimeException("Column not found"));

		if (toColumn.getWipLimit() != null && toColumn.getWipLimit() > 0) {
			long count = boardCardRepo.countByBoardIdAndColumnId(boardId, columnId);
			if (!Objects.equals(fromColumn.getId(), toColumn.getId()) && count >= toColumn.getWipLimit()) {
				throw new RuntimeException("WIP limit exceeded for column: " + toColumn.getName());
			}
		}

		List<BoardCard> fromCards = boardCardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, fromColumn.getId());
		for (BoardCard c : fromCards) {
			if (c.getPosition() > card.getPosition()) {
				c.setPosition(c.getPosition() - 1);
				boardCardRepo.save(c);
			}
		}

		List<BoardCard> toCards = boardCardRepo.findByBoardIdAndColumnIdOrderByPosition(boardId, toColumn.getId());
		for (BoardCard c : toCards) {
			if (c.getPosition() >= position) {
				c.setPosition(c.getPosition() + 1);
				boardCardRepo.save(c);
			}
		}

		card.setColumn(toColumn);
		card.setPosition(position);
		boardCardRepo.save(card);

		issueRepo.findById(card.getIssueId()).ifPresent(issue -> updateIssueStatus(issue, toColumn.getStatusKey()));
	}

	private void updateIssueStatus(Issue issue, IssueStatus issueStatus) {
		if (issueStatus == null) {
			return;
		}
		try {
			issue.setIssueStatus(issueStatus);
			issueRepo.save(issue);
		} catch (Exception e) {
			throw new RuntimeException("Invalid statusKey mapping: " + issueStatus, e);
		}
	}

	@Transactional
	public void recordColumn(Long boardId, Long columnId, List<Long> orderedByCardIds) {
		int position = 0;
		for (Long cid : orderedByCardIds) {
			BoardCard card = boardCardRepo.findById(cid).orElseThrow(() -> new RuntimeException("Card not found"));
			card.setPosition(position++);
			boardCardRepo.save(card);
		}
	}

	@Transactional
	public void startSprint(Long sprintId) {
	}

	@Transactional
	public void completeSprint(Long sprintId) {
	}
	public BoardColumn addColumn(BoardColumn column) {
	    return boardColumnRepo.save(column);
	}
}