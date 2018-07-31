package uk.ac.ebi.uniprot.ot.validation.json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Validates against a JSON schema a specified JSON instance.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@FunctionalInterface
public interface JsonValidator {
    ValidationReport validate(JsonNode schema, JsonNode instance);
}
