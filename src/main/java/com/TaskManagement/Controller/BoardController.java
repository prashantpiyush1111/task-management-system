package com.TaskManagement.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.Entity.Board;
import com.TaskManagement.Entity.BoardCard;
import com.TaskManagement.Entity.BoardColumn;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @Autowired
    private UserAuthRepository userAuthRepository;

    private Long resolveOrganizationId(Authentication authentication) {
        UserAuth user = userAuthRepository.findByUserOfficialEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getOrganization().getId();
    }

	@PostMapping
	public ResponseEntity<Board> create(@RequestBody Board board, Authentication authentication) {
		board.setOrganizationId(resolveOrganizationId(authentication));
		return ResponseEntity.ok(boardService.createBoard(board));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Board> getBoardById(@PathVariable Long id, Authentication authentication) {
	    return boardService.getByBoardId(id, resolveOrganizationId(authentication))
	        .map(ResponseEntity::ok)
	        .orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{boardId}/columns")
	public ResponseEntity<List<BoardColumn>> getBoardColumns(@PathVariable Long boardId) {
		return ResponseEntity.ok(boardService.getBoardColumns(boardId));
	}


	@GetMapping("/{boardId}/cards/{columnId}")
	public ResponseEntity<List<BoardCard>> getBoardCards(@PathVariable Long boardId, @PathVariable Long columnId) {
		return ResponseEntity.ok(boardService.getBoardByCards(boardId, columnId));
	}

	@PostMapping("/{id}/card")
	public ResponseEntity<BoardCard> addCard(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication authentication) {
		Long columnId = Long.valueOf(String.valueOf(body.get("columnId")));
		Long issueId = Long.valueOf(String.valueOf(body.get("issueId")));
		return ResponseEntity.ok(boardService.addIssueToBoard(id, columnId, issueId, resolveOrganizationId(authentication)));
	}

	@PostMapping("/{id}/column")
	public ResponseEntity<BoardColumn> addColumn(
	        @PathVariable Long id,
	        @RequestBody BoardColumn column,
	        Authentication authentication) {
	    Board board = boardService.findById(id, resolveOrganizationId(authentication))
	            .orElseThrow(() -> new RuntimeException("Board not found"));
	    column.setBoard(board);
	    return ResponseEntity.ok(boardService.addColumn(column));
	}
	
	@PutMapping("/{boardId}/card/{cardId}/move")
	public ResponseEntity<Void> moveCard(
	        @PathVariable Long boardId,
	        @PathVariable Long cardId,
	        @RequestBody Map<String, Object> body,
	        Authentication authentication) {
	    Long columnId = Long.valueOf(String.valueOf(body.get("columnId")));
	    int position = body.get("position") != null
	            ? Integer.parseInt(String.valueOf(body.get("position"))) : 0;
	    String performedBy = authentication.getName();
	    boardService.moveCards(boardId, columnId, cardId, position, performedBy, resolveOrganizationId(authentication));
	    return ResponseEntity.ok().build();
	}
 
}