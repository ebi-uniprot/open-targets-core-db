package uk.ac.ebi.uniprot.ot.validation.json;

// import com.github.fge.jackson.JsonLoader;
// import org.junit.jupiter.api.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import uk.ac.ebi.uniprot.ot.model.base.Base;
// import uk.ac.ebi.uniprot.ot.model.base.UniqueAssociationFields;
//
// import java.io.IOException;
// import java.net.URL;
//
// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.*;
// import static org.hamcrest.core.Is.is;
// import static org.hamcrest.core.IsNot.not;
//
/// **
// * Created 31/07/18
// *
// * @author Edd
// */
// class JsonSchema4ValidatorTest {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchema4ValidatorTest.class);
//
//    @Test
//    void shouldCreateValidCuratedLiteratureSchema() throws IOException {
//        JsonNode schemaNode = JsonLoader
//                .fromURL(new
// URL("https://raw.githubusercontent.com/CTTV/json_schema/master/src/literature_curated.json"));
//        assertThat(schemaNode, is(not(nullValue())));
//    }
//
//    @Test
//    void shouldCreateValidGeneticsSchema() throws IOException {
//        JsonNode schemaNode = JsonLoader.fromURL(new
// URL("https://raw.githubusercontent.com/CTTV/json_schema/master/src/genetics.json"));
//        assertThat(schemaNode, is(not(nullValue())));
//    }
//
//    @Test
//    void shouldValidateInstanceWithSchema() throws IOException {
//        Base es = new Base();
//        es.setAccess_level("public");
//        es.setValidated_against_schema_version("0.0.0");
//        es.setSourceID("accession");
//        UniqueAssociationFields uaf = new UniqueAssociationFields();
//        es.setUnique_association_fields(uaf);
//
//        JsonSchema4Validator validator = new JsonSchema4Validator();
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode schemaNode = JsonLoader
//                .fromURL(new
// URL("https://raw.githubusercontent.com/CTTV/json_schema/master/src/base.json"));
//        ValidationReport validate = validator.validate(schemaNode, mapper.valueToTree(es));
//        LOGGER.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(es));
//        assertThat(validate.getMessages(), hasSize(greaterThan(0)));
//    }
// }
