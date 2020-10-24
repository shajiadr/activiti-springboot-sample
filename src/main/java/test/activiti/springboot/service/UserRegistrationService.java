package test.activiti.springboot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.process.runtime.connector.Connector;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import test.activiti.springboot.infrastructure.dto.TaskCompletionRequest;
import test.activiti.springboot.infrastructure.dto.UserRegistrationRequest;
import test.activiti.springboot.service.exception.UserRegistrationTaskException;

import java.util.List;
import java.util.Map;

@Service
public class UserRegistrationService {
    private Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    private static final String USER_REGISTRATION_PROCESS = "userRegistrationProcess";

    private static final String VAR_REGISTRATION_REQUEST = "registrationRequest";

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    public UserRegistrationService() {
    }

    public ProcessInstance startRegistrationProcess(UserRegistrationRequest userRegistrationRequest) {
        return processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(USER_REGISTRATION_PROCESS)
                .withVariable(VAR_REGISTRATION_REQUEST,
                        userRegistrationRequest)
                .build());
    }

    public List<Task> getApprovalTasks() {
        return taskRuntime.tasks(Pageable.of(0, 10)).getContent();
    }

    public Task completeTask(String taskId, TaskCompletionRequest taskRequest) {
        Task task = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<VariableInstance> variables = taskRuntime.variables(TaskPayloadBuilder.variables().withTaskId(taskId).build());

            JsonNode contentToProcessNode = variables.stream().filter(var -> VAR_REGISTRATION_REQUEST.equals(var.getName())).findFirst().orElse(null).getValue();
            UserRegistrationRequest contentToProcess = objectMapper.treeToValue(contentToProcessNode, UserRegistrationRequest.class);

            task = taskRuntime.complete(TaskPayloadBuilder.complete()
                    .withTaskId(taskId)
                    .withVariable(VAR_REGISTRATION_REQUEST, contentToProcess)
                    .withVariable("comment", taskRequest.getComment())
                    .withVariable("approved", taskRequest.isApproved()).build());
        } catch (Exception e) {
            throw new UserRegistrationTaskException(e.getMessage());
        }

        return task;
    }

    @Bean
    public Connector updateRegistrationStatusConnector() {
        return integrationContext -> {
            Map<String, Object> inBoundVariables = integrationContext.getInBoundVariables();

            if (logger.isDebugEnabled()) {
                logger.debug("inBoundVariables --> ", inBoundVariables);
            }

            UserRegistrationRequest registrationRequest = (UserRegistrationRequest) inBoundVariables.get(VAR_REGISTRATION_REQUEST);
            String comment = (String) inBoundVariables.get("comment");
            Boolean approved = (Boolean) inBoundVariables.get("approved");

            saveRegistrationStatus(registrationRequest.getUserId(), comment, approved);
            return integrationContext;
        };
    }

    private void saveRegistrationStatus(String userId, String comment, boolean approved) {
        // Call API to save registration status here
    }
}
