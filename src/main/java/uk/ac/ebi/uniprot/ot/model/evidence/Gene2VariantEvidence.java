package uk.ac.ebi.uniprot.ot.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gene2VariantEvidence extends EvidenceBase {
    @JsonProperty
    private List<String> evidence_codes;

    @JsonProperty
    private String functional_consequence;

    @JsonProperty
    private List<LinkOut> urls;

    public List<String> getEvidence_codes() {
        return evidence_codes;
    }

    public void setEvidenceCodes(List<String> evidence_codes) {
        this.evidence_codes = evidence_codes;
    }

    public String getFunctional_consequence() {
        return functional_consequence;
    }

    public void setFunctional_consequence(String functional_consequence) {
        this.functional_consequence = functional_consequence;
    }

    public List<LinkOut> getUrls() {
        return urls;
    }

    public void setUrls(List<LinkOut> urls) {
        this.urls = urls;
    }
}
