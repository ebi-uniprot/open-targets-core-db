package uk.ac.ebi.uniprot.ot.validation.json;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON validator.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class JsonSchemaValidator implements JsonValidator {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidator.class);

  @Override
  public ValidationReport validate(Schema schema, String jsonString) {

    JacksonValidationReport report = new JacksonValidationReport();
    try {
      schema.validate(new JSONObject(jsonString));
    } catch (ValidationException validationException) {
      report.setSucceeded(false);
      validationException.getCausingExceptions().stream()
          .map(ValidationException::getMessage)
          .forEach(
              message -> {
                LOGGER.error(message);
                report.addMessage(message);
              });
    }

    report.setSucceeded(true);
    return report;
  }
}
