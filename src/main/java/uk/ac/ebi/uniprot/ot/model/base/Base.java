package uk.ac.ebi.uniprot.ot.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ebi.uniprot.ot.model.bioentity.Disease;
import uk.ac.ebi.uniprot.ot.model.bioentity.Target;
import uk.ac.ebi.uniprot.ot.model.provenance.LiteratureProvenanceType;

/**
 * Base POJO class representing the evidence string.
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Base {
    @JsonProperty
    private String sourceID;
    @JsonProperty
    private String access_level;
    @JsonProperty
    private String validated_against_schema_version;
    @JsonProperty
    private UniqueAssociationFields unique_association_fields;
    @JsonProperty
    private Target target;
    @JsonProperty
    private Disease disease;
    @JsonProperty
    private LiteratureProvenanceType literature;

    public LiteratureProvenanceType getLiterature() {
        return literature;
    }

    public void setLiterature(LiteratureProvenanceType literature) {
        this.literature = literature;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getAccess_level() {
        return access_level;
    }

    public void setAccess_level(String access_level) {
        this.access_level = access_level;
    }

    public String getValidated_against_schema_version() {
        return validated_against_schema_version;
    }

    public void setValidated_against_schema_version(String validated_against_schema_version) {
        this.validated_against_schema_version = validated_against_schema_version;
    }

    public UniqueAssociationFields getUnique_association_fields() {
        return unique_association_fields;
    }

    public void setUnique_association_fields(UniqueAssociationFields unique_association_fields) {
        this.unique_association_fields = unique_association_fields;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }
}
