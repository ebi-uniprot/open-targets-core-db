package uk.ac.ebi.uniprot.ot.model.provenance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiteratureProvenanceType {
  @JsonProperty private List<Literature> references;

  public List<Literature> getReferences() {
    return references;
  }

  public void setReferences(List<Literature> references) {
    this.references = references;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LiteratureProvenanceType that = (LiteratureProvenanceType) o;

    return references != null ? references.equals(that.references) : that.references == null;
  }

  @Override
  public int hashCode() {
    return references != null ? references.hashCode() : 0;
  }
}
