package uk.ac.ebi.uniprot.ot.model.base;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniqueAssociationFields {
  @JsonProperty private String target;
  @JsonProperty private List<String> publicationIDs;
  @JsonProperty private String disease_acronym;
  @JsonProperty private String disease_uri;
  @JsonProperty private String uniprot_release;
  @JsonProperty private String variant_id;
  @JsonProperty private String dbSnps;
  @JsonProperty private String alleleOrigin;
  @JsonProperty private String mutationDescription;

  public String getAlleleOrigin() {
    return alleleOrigin;
  }

  public void setAlleleOrigin(String alleleOrigin) {
    this.alleleOrigin = alleleOrigin;
  }

  public String getDisease_acronym() {
    return disease_acronym;
  }

  public void setDisease_acronym(String disease_acronym) {
    this.disease_acronym = disease_acronym;
  }

  public String getDbSnps() {
    return dbSnps;
  }

  public void setDbSnps(String dbSnps) {
    this.dbSnps = dbSnps;
  }

  public String getUniprot_release() {
    return uniprot_release;
  }

  public void setUniprot_release(String uniprot_release) {
    this.uniprot_release = uniprot_release;
  }

  public String getVariant_id() {
    return variant_id;
  }

  public void setVariant_id(String variant_id) {
    this.variant_id = variant_id;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public List<String> getPublicationIDs() {
    return publicationIDs;
  }

  public void setPublicationIDs(List<String> publicationIDs) {
    this.publicationIDs = publicationIDs;
  }

  public String getDisease_uri() {
    return disease_uri;
  }

  public void setDisease_uri(String disease_uri) {
    this.disease_uri = disease_uri;
  }

  public void setMutationDescription(String mutationDescription) {
    this.mutationDescription = mutationDescription;
  }

  public String getMutationDescription() {
    return mutationDescription;
  }

  @Override
  public String toString() {
    return "UniqueAssociationFields{"
        + "target='"
        + target
        + '\''
        + ", publicationIDs="
        + publicationIDs
        + ", disease_acronym='"
        + disease_acronym
        + '\''
        + ", disease_uri='"
        + disease_uri
        + '\''
        + ", uniprot_release='"
        + uniprot_release
        + '\''
        + ", variant_id='"
        + variant_id
        + '\''
        + ", dbSnps='"
        + dbSnps
        + '\''
        + '}';
  }
}
