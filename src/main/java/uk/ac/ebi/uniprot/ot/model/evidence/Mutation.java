package uk.ac.ebi.uniprot.ot.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 06/09/17
 *
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mutation {
  @JsonProperty private String preferred_name;

  @JsonProperty private String functional_consequence;

  public String getPreferred_name() {
    return preferred_name;
  }

  public void setPreferred_name(String preferred_name) {
    this.preferred_name = preferred_name;
  }

  public String getFunctional_consequence() {
    return functional_consequence;
  }

  public void setFunctional_consequence(String functional_consequence) {
    this.functional_consequence = functional_consequence;
  }
}
