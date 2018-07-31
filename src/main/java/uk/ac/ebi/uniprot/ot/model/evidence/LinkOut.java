package uk.ac.ebi.uniprot.ot.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkOut {
    @JsonProperty
    private String nice_name;
    @JsonProperty
    private String url;

    public String getNice_name() {
        return nice_name;
    }

    public void setNice_name(String nice_name) {
        this.nice_name = nice_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
