package uk.ac.ebi.uniprot.ot.model;

import lombok.Getter;
import lombok.Setter;
import uk.ac.ebi.uniprot.ot.model.base.Base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneticsRoot extends Base {
  @JsonProperty private String variantFunctionalConsequenceId;
  @JsonProperty private String variantRsId;
}
