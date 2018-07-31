package uk.ac.ebi.uniprot.ot.converter;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;
import uk.ac.ebi.uniprot.ot.model.evidence.GeneticsEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.Variant2DiseaseEvidence;
import uk.ac.ebi.uniprot.ot.model.provenance.Literature;
import uk.ac.ebi.uniprot.ot.model.provenance.LiteratureProvenanceType;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.uniprot.ot.converter.UniProtDiseaseAssocCollator.removeDuplicates;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.UNIPROT_LITERATURE;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.UNIPROT_SOMATIC;

/**
 * Created 31/07/18
 *
 * @author Edd
 */
class UniProtDiseaseAssocCollatorTest {
    @Test
    void removeLitRootWhenGenRootHasSameEvidence() {
        List<Literature> litRootLit = new ArrayList<>();
        litRootLit.add(createLitInfo("id1"));
        litRootLit.add(createLitInfo("id2"));
        LiteratureCuratedRoot litRoot = new LiteratureCuratedRoot();
        litRoot.setSourceID(UNIPROT_LITERATURE);
        LiteratureProvenanceType litRootLitProv = new LiteratureProvenanceType();
        litRootLitProv.setReferences(litRootLit);
        litRoot.setLiterature(litRootLitProv);
        List<LiteratureCuratedRoot> litRoots = new ArrayList<>();
        litRoots.add(litRoot);

        List<Literature> genRootLit = new ArrayList<>();
        genRootLit.add(createLitInfo("id2"));
        genRootLit.add(createLitInfo("id1"));
        GeneticsRoot genRoot = new GeneticsRoot();
        GeneticsEvidence genEv = new GeneticsEvidence();
        LiteratureProvenanceType genLitProv = new LiteratureProvenanceType();
        genLitProv.setReferences(genRootLit);
        Variant2DiseaseEvidence variant2DiseaseEvidence = new Variant2DiseaseEvidence();
        ProvenanceType genProvType = new ProvenanceType();
        genProvType.setLiterature(genLitProv);
        variant2DiseaseEvidence.setProvenance_type(genProvType);
        genEv.setVariant2disease(variant2DiseaseEvidence);
        genRoot.setEvidence(genEv);
        List<GeneticsRoot> genRoots = new ArrayList<>();
        genRoots.add(genRoot);

        removeDuplicates(litRoots, genRoots);

        assertThat(litRoots, hasSize(0));
        assertThat(genRoots, hasSize(1));
    }

    @Test
    void removeLitRootWhenSomaticLitRootHasSameEvidence() {
        List<Literature> litRootLit = new ArrayList<>();
        litRootLit.add(createLitInfo("id1"));
        litRootLit.add(createLitInfo("id2"));
        LiteratureCuratedRoot litRoot = new LiteratureCuratedRoot();
        litRoot.setSourceID(UNIPROT_LITERATURE);
        LiteratureProvenanceType litRootLitProv = new LiteratureProvenanceType();
        litRootLitProv.setReferences(litRootLit);
        litRoot.setLiterature(litRootLitProv);

        List<Literature> somaticLitRootLit = new ArrayList<>();
        somaticLitRootLit.add(createLitInfo("id1"));
        somaticLitRootLit.add(createLitInfo("id2"));
        LiteratureCuratedRoot somaticLitRoot = new LiteratureCuratedRoot();
        somaticLitRoot.setSourceID(UNIPROT_SOMATIC);
        LiteratureProvenanceType somaticLitRootLitProv = new LiteratureProvenanceType();
        somaticLitRootLitProv.setReferences(somaticLitRootLit);
        somaticLitRoot.setLiterature(somaticLitRootLitProv);

        List<LiteratureCuratedRoot> litRoots = new ArrayList<>();
        litRoots.add(litRoot);
        litRoots.add(somaticLitRoot);

        removeDuplicates(litRoots, emptyList());

        assertThat(litRoots, hasSize(1));
        assertThat(litRoots, hasItem(somaticLitRoot));
    }

    @Test
    void keepLitRootWhenGenRootHasDifferentEvidence() {
        List<Literature> litRootLit = new ArrayList<>();
        litRootLit.add(createLitInfo("id1"));
        litRootLit.add(createLitInfo("id2"));
        litRootLit.add(createLitInfo("id3"));
        LiteratureCuratedRoot litRoot = new LiteratureCuratedRoot();
        litRoot.setSourceID(UNIPROT_LITERATURE);
        LiteratureProvenanceType litRootLitProv = new LiteratureProvenanceType();
        litRootLitProv.setReferences(litRootLit);
        litRoot.setLiterature(litRootLitProv);
        List<LiteratureCuratedRoot> litRoots = new ArrayList<>();
        litRoots.add(litRoot);

        List<Literature> genRootLit = new ArrayList<>();
        genRootLit.add(createLitInfo("id2"));
        genRootLit.add(createLitInfo("id1"));
        GeneticsRoot genRoot = new GeneticsRoot();
        GeneticsEvidence genEv = new GeneticsEvidence();
        LiteratureProvenanceType genLitProv = new LiteratureProvenanceType();
        genLitProv.setReferences(genRootLit);
        Variant2DiseaseEvidence variant2DiseaseEvidence = new Variant2DiseaseEvidence();
        ProvenanceType genProvType = new ProvenanceType();
        genProvType.setLiterature(genLitProv);
        variant2DiseaseEvidence.setProvenance_type(genProvType);
        genEv.setVariant2disease(variant2DiseaseEvidence);
        genRoot.setEvidence(genEv);
        List<GeneticsRoot> genRoots = new ArrayList<>();
        genRoots.add(genRoot);

        removeDuplicates(litRoots, genRoots);

        assertThat(litRoots, hasSize(1));
        assertThat(genRoots, hasSize(1));
    }

    private Literature createLitInfo(String id) {
        Literature lit = new Literature();
        lit.setLit_id(id);
        return lit;
    }
}