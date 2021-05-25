package uk.ac.ebi.uniprot.ot.model.factory;

import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.ACTIVITY_UP_DOWN;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.UNIPROT_SOMATIC;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

class LiteratureCuratedRootFactory {
  private static final String OMIM = "OMIM:";
  private static final String GENETIC_LITERATURE = "genetic_literature";
  private final DefaultBaseFactory baseFactory;

  LiteratureCuratedRootFactory(DefaultBaseFactory baseFactory) {
    this.baseFactory = baseFactory;
  }

  Base createLiteratureCuratedRoot(
      UniProtEntry uniProtEntry,
      DiseaseCommentStructured disease,
      List<EvidenceId> pubmedEvidenceIds,
      String efo,
      VariantLineInfo vli) {

    Base lcr = createLitCuratedRoot(uniProtEntry, disease, efo);
    lcr.setDatasourceId(UNIPROT_SOMATIC);
    return lcr;
  }

  Base createLiteratureCuratedRoot(
      UniProtEntry uniProtEntry,
      DiseaseCommentStructured structuredDisease,
      List<EvidenceId> pubmedEvidenceIds,
      String efoMapping) {
    Base lcr = createLitCuratedRoot(uniProtEntry, structuredDisease, efoMapping);
    lcr.setDatasourceId(DefaultBaseFactory.UNIPROT_LITERATURE);
    Set<String> pubMedIds = new HashSet<>();
    pubMedIds.addAll(baseFactory.extractPubMeds(pubmedEvidenceIds));
    lcr.setLiterature(pubMedIds);
    lcr.setConfidence(baseFactory.createConfidence(structuredDisease));
    lcr.setDiseaseFromSourceId(
        OMIM + structuredDisease.getDisease().getReference().getDiseaseReferenceId().getValue());
    return lcr;
  }

  private Base createLitCuratedRoot(
      UniProtEntry uniProtEntry, DiseaseCommentStructured structuredDisease, String efoMapping) {
    Base lcr = new Base();
    lcr.setDatatypeId(GENETIC_LITERATURE);
    lcr.setDiseaseFromSource(structuredDisease.getDisease().getDiseaseId().getValue());
    if (efoMapping != null) {
      lcr.setDiseaseFromSourceMappedId(DefaultBaseFactory.getMappedId(efoMapping));
    }
    lcr.setTargetFromSourceId(uniProtEntry.getPrimaryUniProtAccession().getValue());
    lcr.setTargetModulation(ACTIVITY_UP_DOWN);
    return lcr;
  }
}
