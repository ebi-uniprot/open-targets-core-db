package uk.ac.ebi.uniprot.ot.model.provenance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpertProvenanceType {
    @JsonProperty
    private String statement;

    @JsonProperty
    private boolean status;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
