package com.TaskManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity

@Table(name = "board_cards", indexes = { @Index(columnList = "board_id,column_id,position") })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long boardId;
	private Long issueId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_id")
	private BoardColumn column;

	private Integer position;
}