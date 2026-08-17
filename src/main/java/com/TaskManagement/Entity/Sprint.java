package com.TaskManagement.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.TaskManagement.Enum.SprintState;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "sprints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sprintName;
	private Long projectId;
	@Column(nullable = false)
	private Long organizationId;
	private LocalDate startDate;
	private LocalDate endDate;
	@Enumerated(EnumType.STRING)
	private SprintState sprintState;
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	public SprintState getState() {
		return sprintState;
	}
	public void setState(SprintState sprintState) {
		this.sprintState = sprintState;
	}
}