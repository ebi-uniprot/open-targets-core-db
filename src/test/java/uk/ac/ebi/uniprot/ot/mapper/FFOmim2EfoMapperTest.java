package uk.ac.ebi.uniprot.ot.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * Created 16/06/17
 * @author Edd
 */
class FFOmim2EfoMapperTest {
    private static final String EFO_MAPPINGS = "/mappings/omim2efo.mappings";

    private FFOmim2EfoMapper mapper;

    @BeforeEach
    void shouldInitialiseMapperOkay() throws URISyntaxException {
        mapper = new FFOmim2EfoMapper(FFOmim2EfoMapperTest.class.getResource(EFO_MAPPINGS));
    }

    @Test
    void mapIsInitialised() {
        assertThat(mapper, is(notNullValue()));
        assertThat(mapper.getKeysCount(), is(greaterThan(0)));
    }

    @Test
    void shouldFindSingleMapping() {
        Set<String> efos = mapper.omim2Efo("100050");
        assertThat(efos, contains("http://www.orpha.net/ORDO/Orphanet_915"));
    }

    @Test
    void shouldFindMultiMapping() {
        Set<String> efos = mapper.omim2Efo("101200");
        assertThat(efos, containsInAnyOrder(
                "http://www.ebi.ac.uk/efo/EFO_0004123",
                "http://www.orpha.net/ORDO/Orphanet_87"));
    }
}