package uk.ac.ebi.uniprot.ot.model.evidence;

import uk.ac.ebi.uniprot.ot.model.evidence.association_score.ProbabilityAssScore;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceBase {
  @JsonProperty private ProbabilityAssScore resource_score;

  @JsonProperty private String date_asserted;

  @JsonProperty private boolean is_associated;

  @JsonProperty private String unique_experiment_reference;

  @JsonProperty private ProvenanceType provenance_type;

  public ProbabilityAssScore getResource_score() {
    return resource_score;
  }

  public void setResource_score(ProbabilityAssScore resource_score) {
    this.resource_score = resource_score;
  }

  public String getDate_asserted() {
    return date_asserted;
  }

  public void setDate_asserted(String date_asserted) {
    this.date_asserted = date_asserted;
  }

  public boolean getIs_associated() {
    return is_associated;
  }

  public void setIs_associated(boolean is_associated) {
    this.is_associated = is_associated;
  }

  public String getUnique_experiment_reference() {
    return unique_experiment_reference;
  }

  public void setUniqueExperimentReference(String uniqueExperimentReference) {
    this.unique_experiment_reference = uniqueExperimentReference;
  }

  public ProvenanceType getProvenance_type() {
    return provenance_type;
  }

  public void setProvenance_type(ProvenanceType provenance_type) {
    this.provenance_type = provenance_type;
  }
}
