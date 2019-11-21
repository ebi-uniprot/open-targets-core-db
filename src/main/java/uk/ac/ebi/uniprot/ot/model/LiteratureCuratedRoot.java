package uk.ac.ebi.uniprot.ot.model;

import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.evidence.LiteratureCuratedEvidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiteratureCuratedRoot extends Base {
  @JsonProperty private String type = "genetic_literature";
  @JsonProperty private LiteratureCuratedEvidence evidence;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LiteratureCuratedEvidence getEvidence() {
    return evidence;
  }

  public void setEvidence(LiteratureCuratedEvidence evidence) {
    this.evidence = evidence;
  }

  @Override
  public String toString() {
    return "LiteratureCuratedRoot{" + "type='" + type + '\'' + ", evidence=" + evidence + '}';
  }
}
