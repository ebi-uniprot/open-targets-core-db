package uk.ac.ebi.uniprot.ot.model.provenance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvenanceType {
    @JsonProperty
    private ExpertProvenanceType expert;
    @JsonProperty
    private LiteratureProvenanceType literature;
    @JsonProperty
    private DatabaseProvenanceType database;

    public ExpertProvenanceType getExpert() {
        return expert;
    }

    public void setExpert(ExpertProvenanceType expert) {
        this.expert = expert;
    }

    public LiteratureProvenanceType getLiterature() {
        return literature;
    }

    public void setLiterature(
            LiteratureProvenanceType literature) {
        this.literature = literature;
    }

    public DatabaseProvenanceType getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseProvenanceType database) {
        this.database = database;
    }
}
