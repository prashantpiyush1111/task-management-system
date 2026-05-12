package com.TaskManagement.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.Entity.Board;
import com.TaskManagement.Entity.BoardCard;
import com.TaskManagement.Entity.BoardColumn;
import com.TaskManagement.Service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

	@PostMapping
	public ResponseEntity<Board> create(@RequestBody Board board) {
		return ResponseEntity.ok(boardService.createBoard(board));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<Board>> getBoardById(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.getByBoardId(id));
	}

	// FIX: URL /{id} tha lekin parameter boardId tha — mismatch
	@GetMapping("/{boardId}/columns")
	public ResponseEntity<List<BoardColumn>> getBoardColumns(@PathVariable Long boardId) {
		return ResponseEntity.ok(boardService.getBoardColumns(boardId));
	}

	// FIX: do alag PathVariable chahiye the
	@GetMapping("/{boardId}/cards/{columnId}")
	public ResponseEntity<List<BoardCard>> getBoardCards(@PathVariable Long boardId, @PathVariable Long columnId) {
		return ResponseEntity.ok(boardService.getBoardByCards(boardId, columnId));
	}

	@PostMapping("/{id}/card")
	public ResponseEntity<BoardCard> addCard(@PathVariable Long id, @RequestBody Map<String, Object> body) {
		Long columnId = Long.valueOf(String.valueOf(body.get("columnId")));
		Long issueId = Long.valueOf(String.valueOf(body.get("issueId")));
		return ResponseEntity.ok(boardService.addIssueToBoard(id, columnId, issueId));
	}

	@PostMapping("/{id}/column")
	public ResponseEntity<BoardColumn> addColumn(
	        @PathVariable Long id,
	        @RequestBody BoardColumn column) {
	    Board board = boardService.findById(id)
	            .orElseThrow(() -> new RuntimeException("Board not found"));
	    column.setBoard(board);
	    return ResponseEntity.ok(boardService.addColumn(column));
	}
}