package uk.ac.ebi.uniprot.ot.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ebi.uniprot.ot.model.evidence.EvidenceBase;

import java.util.List;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Variant2DiseaseEvidence extends EvidenceBase {
    private static final int GWAS_PANEL_RESOLUTION = 1;
    private static final int GWAS_SAMPLE_SIZE = 1;

    @JsonProperty
    private int gwas_panel_resolution = GWAS_PANEL_RESOLUTION;

    @JsonProperty
    private int gwas_sample_size = GWAS_SAMPLE_SIZE;

    @JsonProperty
    private List<String> evidence_codes;

    @JsonProperty
    private List<LinkOut> urls;

    public List<String> getEvidence_codes() {
        return evidence_codes;
    }

    public void setEvidence_codes(List<String> evidence_codes) {
        this.evidence_codes = evidence_codes;
    }

    public List<LinkOut> getUrls() {
        return urls;
    }

    public void setUrls(List<LinkOut> urls) {
        this.urls = urls;
    }

    public int getGwas_panel_resolution() {
        return gwas_panel_resolution;
    }

    public int getGwas_sample_size() {
        return gwas_sample_size;
    }
}
