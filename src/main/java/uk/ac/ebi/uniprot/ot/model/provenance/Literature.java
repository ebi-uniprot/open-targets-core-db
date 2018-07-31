package uk.ac.ebi.uniprot.ot.model.provenance;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 13/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class Literature {
    @JsonProperty
    private String lit_id;

    public String getLit_id() {
        return lit_id;
    }

    public void setLit_id(String id) {
        this.lit_id = id;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Literature that = (Literature) o;

        return lit_id != null ? lit_id.equals(that.lit_id) : that.lit_id == null;
    }

    @Override public int hashCode() {
        return lit_id != null ? lit_id.hashCode() : 0;
    }
}
