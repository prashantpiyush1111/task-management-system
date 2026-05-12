package com.TaskManagement.Entity;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 
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
 
	@ManyToOne(fetch = FetchType.EAGER)  
	@JoinColumn(name = "column_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private BoardColumn column;
 
	private Integer position;
}
 