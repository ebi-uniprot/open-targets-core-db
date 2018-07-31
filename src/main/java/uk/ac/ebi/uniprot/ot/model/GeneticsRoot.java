package uk.ac.ebi.uniprot.ot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.bioentity.Variant;
import uk.ac.ebi.uniprot.ot.model.evidence.GeneticsEvidence;

/**
 * Created 08/05/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneticsRoot extends Base {
    @JsonProperty
    private String type = "genetic_association";
    @JsonProperty
    private Variant variant;
    @JsonProperty
    private GeneticsEvidence evidence;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public GeneticsEvidence getEvidence() {
        return evidence;
    }

    public void setEvidence(GeneticsEvidence evidence) {
        this.evidence = evidence;
    }
}