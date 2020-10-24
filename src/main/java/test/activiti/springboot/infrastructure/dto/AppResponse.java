package test.activiti.springboot.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppResponse {

    private Integer statusCode;
    private HttpStatus status;
    private String message;
    private Object data;
}
