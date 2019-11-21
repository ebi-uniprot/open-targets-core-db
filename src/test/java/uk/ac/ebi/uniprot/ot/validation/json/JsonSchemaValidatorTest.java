package uk.ac.ebi.uniprot.ot.validation.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.CTTV_SCHEMA_VERSION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.base.UniqueAssociationFields;

import com.fasterxml.jackson.databind.ObjectMapper;

class JsonSchemaValidatorTest {

  private static final String SCHEMA_ADDRESS =
      "https://raw.githubusercontent.com/opentargets/json_schema/"
          + CTTV_SCHEMA_VERSION
          + "/opentargets.json";

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidatorTest.class);
  private static Schema schema;

  @BeforeAll
  static void setUp() throws IOException {
    File schemaFile = new File("json-schema-v" + CTTV_SCHEMA_VERSION + ".json");
    FileUtils.copyURLToFile(new URL(SCHEMA_ADDRESS), schemaFile);

    JSONObject rawSchema;
    try (InputStream inputStream = new FileInputStream(schemaFile.getAbsolutePath())) {
      rawSchema = new JSONObject(new JSONTokener(inputStream));
    }

    schema = SchemaLoader.builder().schemaJson(rawSchema).draftV7Support().build().load().build();
  }

  @Test
  void createdSchemaIsNotNull() {
    assertThat(schema, is(not(nullValue())));
  }

  @Test
  void shouldValidateInstanceWithSchema() throws IOException {
    Base es = new Base();
    es.setAccess_level("public");
    es.setValidated_against_schema_version("0.0.0");
    es.setSourceID("accession");
    UniqueAssociationFields uaf = new UniqueAssociationFields();
    es.setUnique_association_fields(uaf);

    JsonSchemaValidator validator = new JsonSchemaValidator();
    ObjectMapper mapper = new ObjectMapper();

    ValidationReport validationReport = validator.validate(schema, mapper.writeValueAsString(es));

    assertThat(validationReport.getMessages(), hasSize(greaterThan(0)));
  }
}
