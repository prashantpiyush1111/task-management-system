package com.TaskManagement.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.TaskManagement.Enum.BoardType;

import lombok.*;

@Entity
@Table(name = "boards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true)
	private String projectKey;

	@Enumerated(EnumType.STRING)
	private BoardType boardType;
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToMany(mappedBy = "board")
	@OrderBy("position")

	@Builder.Default
	private List<BoardColumn> columns = new ArrayList<>();
}