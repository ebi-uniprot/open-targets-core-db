package uk.ac.ebi.uniprot.ot.model.base;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base POJO class representing the evidence string.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Base {
  @JsonProperty private String datasourceId;
  @JsonProperty private String datatypeId;
  @JsonProperty private String diseaseFromSource;
  @JsonProperty private String diseaseFromSourceId;
  @JsonProperty private String confidence;
  @JsonProperty private String diseaseFromSourceMappedId;
  @JsonProperty private Set<String> literature;
  @JsonProperty private String targetFromSourceId;
  @JsonProperty private String targetModulation;
}
