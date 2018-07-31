package uk.ac.ebi.uniprot.ot.model.evidence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ebi.uniprot.ot.model.evidence.association_score.ProbabilityAssScore;

/**
 * Created 08/05/15
 * @author Edd <eddturner@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneticsEvidence {
    @JsonProperty
    private Gene2VariantEvidence gene2variant;

    @JsonProperty
    private Variant2DiseaseEvidence variant2disease;

    @JsonProperty
    private ProbabilityAssScore resource_score;

    public Gene2VariantEvidence getGene2variant() {
        return gene2variant;
    }

    public void setGene2variant(Gene2VariantEvidence gene2variant) {
        this.gene2variant = gene2variant;
    }

    public Variant2DiseaseEvidence getVariant2disease() {
        return variant2disease;
    }

    public void setVariant2disease(
            Variant2DiseaseEvidence variant2disease) {
        this.variant2disease = variant2disease;
    }

    public ProbabilityAssScore getResource_score() {
        return resource_score;
    }

    public void setResource_score(ProbabilityAssScore resource_score) {
        this.resource_score = resource_score;
    }
}
