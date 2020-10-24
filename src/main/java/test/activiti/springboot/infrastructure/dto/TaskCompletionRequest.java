package test.activiti.springboot.infrastructure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TaskCompletionRequest {
    private boolean approved;
    private String comment;
}
