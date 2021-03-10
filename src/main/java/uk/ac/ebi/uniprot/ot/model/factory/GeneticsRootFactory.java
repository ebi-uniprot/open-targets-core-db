package uk.ac.ebi.uniprot.ot.model.factory;

import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.ACTIVITY_UP_DOWN;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

class GeneticsRootFactory {
  private static final String OMIM = "OMIM:";
  private static final String GENETIC_ASSOCIATION = "genetic_association";
  static final String SNP_SINGLE = "snp single";
  static final String STRUCTURAL_VARIANT = "structural variant";
  static final String SNP_MULTIPLE = "snp multiple";
  private static final Logger LOGGER = LoggerFactory.getLogger(GeneticsRootFactory.class);
  private final DefaultBaseFactory baseFactory;
  public static final String SO_SUBSTITUTION_URI = "SO_0001583";
  public static final String SO_MISSING_URI = "SO_0001822";
  public static final String SO_SEQ_ALTERATION_URI = "SO_0001059";

  GeneticsRootFactory(DefaultBaseFactory baseFactory) {
    this.baseFactory = baseFactory;
  }

  GeneticsRoot createGeneticsRoot(
      UniProtEntry uniProtEntry,
      DiseaseCommentStructured structuredDisease,
      List<EvidenceId> pubmedEvIds,
      String efo,
      Feature variantFeature,
      VariantFeature variant,
      VariantLineInfo vli,
      String dbSNP) {

    GeneticsRoot gr = new GeneticsRoot();
    gr.setDatatypeId(GENETIC_ASSOCIATION);
    gr.setDiseaseFromSource(structuredDisease.getDisease().getDiseaseId().getValue());
    gr.setDiseaseFromSourceMappedId(DefaultBaseFactory.getMappedId(efo)); // need to check
    gr.setTargetFromSourceId(uniProtEntry.getPrimaryUniProtAccession().getValue());
    gr.setTargetModulation(ACTIVITY_UP_DOWN);
    gr.setDatasourceId(DefaultBaseFactory.UNIPROT_VARIANT);
    Set<String> pubMedIdsSet = new HashSet<>();
    pubMedIdsSet.addAll(baseFactory.extractPubMeds(pubmedEvIds));
    gr.setLiterature(pubMedIdsSet);
    gr.setVariantFunctionalConsequenceId(getFunctionalConsequence(vli));
    gr.setDiseaseFromSourceId(
        OMIM + structuredDisease.getDisease().getReference().getDiseaseReferenceId().getValue());
    gr.setConfidence(baseFactory.createConfidence(structuredDisease));
    //    String variantRsId = String.join(",", vli.getDbSNPs());
    gr.setVariantRsId(dbSNP);
    return gr;
  }

  public static String getFunctionalConsequence(VariantLineInfo vli) {
    switch (vli.getType()) {
      case MISSENSE:
        return SO_SUBSTITUTION_URI;
      case DELETION:
        return SO_MISSING_URI;
      case INS_DEL:
        return SO_SEQ_ALTERATION_URI;
      default:
        LOGGER.warn("Unknown variant type: {}", vli.getType().name());
    }
    return null;
  }
}
