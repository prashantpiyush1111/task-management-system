package com.TaskManagement.Entity;

import com.TaskManagement.Enum.IssueStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_columns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	private String name;

	private Integer position;

	private Integer wipLimit;

	@Enumerated(EnumType.STRING)
	private IssueStatus statusKey;
}