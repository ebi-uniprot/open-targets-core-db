package uk.ac.ebi.uniprot.ot.model.evidence.association_score;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProbabilityAssScore {
  @JsonProperty private String type = "probability";
  @JsonProperty private double value;
  @JsonProperty private AssScoreMethod method;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public AssScoreMethod getMethod() {
    return method;
  }

  public void setMethod(AssScoreMethod method) {
    this.method = method;
  }
}
