package uk.ac.ebi.uniprot.ot.model.bioentity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target {
  @JsonProperty private String target_type;
  @JsonProperty private String activity;
  @JsonProperty private String id;

  public String getTarget_type() {
    return target_type;
  }

  public void setTarget_type(String target_type) {
    this.target_type = target_type;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
