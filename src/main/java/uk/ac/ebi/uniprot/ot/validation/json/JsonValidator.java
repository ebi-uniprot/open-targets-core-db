package uk.ac.ebi.uniprot.ot.validation.json;

import org.everit.json.schema.Schema;

/**
 * Validates a JSON instance against a JSON schema.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@FunctionalInterface
public interface JsonValidator {
    ValidationReport validate(Schema schema, String jsonString);
}
