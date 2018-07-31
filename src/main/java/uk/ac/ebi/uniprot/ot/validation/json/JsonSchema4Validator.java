package uk.ac.ebi.uniprot.ot.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSON validator using the Jackson Faster XML framework.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class JsonSchema4Validator implements JsonValidator {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchema4Validator.class);

    @Override
    public ValidationReport validate(JsonNode schemaNode, JsonNode instanceNode) {
        ObjectMapper mapper = new ObjectMapper();
        JacksonValidationReport report = new JacksonValidationReport();
        try {
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);

            ProcessingReport processingReport = schema.validate(instanceNode);
            if (processingReport != null) {
                for (ProcessingMessage pm : processingReport) {
                    adaptMessages(report, pm);
                }
                if (!report.getMessages().isEmpty()) {
                    LOGGER.error(mapper
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(((ListProcessingReport) processingReport)
                                    .asJson()));
                }

                // succeeded if there are no FATAL or ERROR messages
                report.setSucceeded(report.getMessages().isEmpty());
            }
        } catch (IOException | ProcessingException e) {
            LOGGER.warn("Exception encountered during jackson json validation: ", e);
        }

        return report;
    }

    private void adaptMessages(JacksonValidationReport report, ProcessingMessage pm) {
        switch (pm.getLogLevel()) {
            case WARNING:
                LOGGER.warn(pm.getMessage());
                break;
            case INFO:
                LOGGER.info(pm.getMessage());
                break;
            case DEBUG:
                LOGGER.debug(pm.getMessage());
                break;
            case FATAL:
            case ERROR:
            default:
                report.addMessage(pm.getMessage());
                break;
        }
    }
}
