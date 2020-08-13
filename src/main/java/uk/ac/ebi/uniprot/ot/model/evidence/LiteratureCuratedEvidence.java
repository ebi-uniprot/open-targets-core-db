package uk.ac.ebi.uniprot.ot.model.evidence;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiteratureCuratedEvidence extends EvidenceBase {
  @JsonProperty private List<String> evidence_codes;
  @JsonProperty private List<Mutation> known_mutations;
  @JsonProperty private List<LinkOut> urls;
  @JsonProperty private String function_description;

  public List<String> getEvidence_codes() {
    return evidence_codes;
  }

  public void setEvidenceCodes(List<String> evidence_codes) {
    this.evidence_codes = evidence_codes;
  }

  public List<Mutation> getKnown_mutations() {
    return known_mutations;
  }

  public void setKnown_mutations(List<Mutation> known_mutations) {
    this.known_mutations = known_mutations;
  }

  public List<LinkOut> getUrls() {
    return urls;
  }

  public void setUrls(List<LinkOut> urls) {
    this.urls = urls;
  }

  public String getFunction_description() {
    return function_description;
  }

  public void setFunction_description(String function_description) {
    this.function_description = function_description;
  }
}
