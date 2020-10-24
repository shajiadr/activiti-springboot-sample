package test.activiti.springboot.infrastructure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationRequest {
    private String userId;
    private String approverId;
}
