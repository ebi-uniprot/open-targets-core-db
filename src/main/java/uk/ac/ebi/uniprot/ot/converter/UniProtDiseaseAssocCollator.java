package uk.ac.ebi.uniprot.ot.converter;

import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.UNIPROT_LITERATURE;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.UNIPROT_SOMATIC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.uniprot.ot.input.UniProtEvSource;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;
import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.factory.BaseFactory;
import uk.ac.ebi.uniprot.ot.model.provenance.Literature;
import uk.ac.ebi.uniprot.ot.validation.json.JsonSchema4Validator;
import uk.ac.ebi.uniprot.ot.validation.json.JsonValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Transforms an evidence string source object into one or more {@link Base} instances.
 *
 * @author Edd
 */
public class UniProtDiseaseAssocCollator implements Converter<UniProtEvSource, Collection<Base>> {
  // logger
  private static final Logger LOGGER = LoggerFactory.getLogger(UniProtDiseaseAssocCollator.class);

  private static final String SCHEMA_ADDRESS =
      "https://raw.githubusercontent.com/opentargets/json_schema/1.6.2/opentargets.json";

  private final JsonValidator validator;
  private final Schema jsonSchema;
  private ObjectMapper objectMapper;
  private ConversionReport conversionReport;

  private BaseFactory baseFactory;

  private boolean validate;

  @Inject
  public UniProtDiseaseAssocCollator(BaseFactory baseFactory) throws IOException {
    jsonSchema = getSchemaLoader();

    this.baseFactory = baseFactory;
    this.objectMapper = new ObjectMapper();
    this.validator = new JsonSchema4Validator();
    this.conversionReport = new ConversionReport();
    this.conversionReport.setMessage("UniProt -> Disease Association Conversion Report");
  }

  private Schema getSchemaLoader() throws IOException {
    File schemaFile = new File("local-schema.json");
    FileUtils.copyURLToFile(new URL(SCHEMA_ADDRESS), schemaFile);

    JSONObject rawSchema;
    try (InputStream inputStream = new FileInputStream(schemaFile.getAbsolutePath())) {
      rawSchema = new JSONObject(new JSONTokener(inputStream));
    }

    return SchemaLoader.builder().schemaJson(rawSchema).draftV7Support().build().load().build();
  }

  @Override
  public Collection<Base> convert(UniProtEvSource source) {
    Collection<Base> bases = new ArrayList<>();
    UniProtEntry uniProtEntry = source.getEvidenceSource();

    // iterate through diseases of entry and create evidence strings for each
    getStructuredDiseasesStream(uniProtEntry)
        .forEach(
            disease -> {
              // create the disease association pojos
              List<LiteratureCuratedRoot> litRoots =
                  this.baseFactory.createLiteratureCuratedRoot(uniProtEntry, disease);
              List<GeneticsRoot> genRoots =
                  this.baseFactory.createGeneticsRoots(uniProtEntry, disease);

              removeDuplicates(litRoots, genRoots);

              if (validate) {
                recordValidResults(bases, uniProtEntry, disease, litRoots);
                recordValidResults(bases, uniProtEntry, disease, genRoots);
              } else {
                recordResultsWithoutValidating(bases, litRoots);
                recordResultsWithoutValidating(bases, genRoots);
              }
            });

    return bases;
  }

  /**
   * If a {@link GeneticsRoot} germline contains the same evidences as a {@link
   * LiteratureCuratedRoot} (non-somatic), for the same disease, then omit the latter.
   *
   * @param litRoots the {@link LiteratureCuratedRoot} instances
   * @param genRoots the {@link GeneticsRoot} instances
   */
  static void removeDuplicates(List<LiteratureCuratedRoot> litRoots, List<GeneticsRoot> genRoots) {
    Set<LiteratureCuratedRoot> obsoleteLitRoots = new HashSet<>();
    Map<String, List<LiteratureCuratedRoot>> litRootsMap =
        litRoots.stream().collect(Collectors.groupingBy(LiteratureCuratedRoot::getSourceID));

    List<LiteratureCuratedRoot> uniprotLit =
        litRootsMap.getOrDefault(UNIPROT_LITERATURE, Collections.emptyList());
    List<LiteratureCuratedRoot> somaticLit =
        litRootsMap.getOrDefault(UNIPROT_SOMATIC, Collections.emptyList());

    for (LiteratureCuratedRoot litRoot : uniprotLit) {
      Set<Literature> litRefs = new HashSet<>(litRoot.getLiterature().getReferences());
      for (LiteratureCuratedRoot somaticLitRoot : somaticLit) {
        Set<Literature> somaticLitRefs =
            new HashSet<>(somaticLitRoot.getLiterature().getReferences());
        if (somaticLitRefs.equals(litRefs)) {
          obsoleteLitRoots.add(litRoot);
        }
      }

      for (GeneticsRoot genRoot : genRoots) {
        Set<Literature> genRefs =
            new HashSet<>(
                genRoot
                    .getEvidence()
                    .getVariant2disease()
                    .getProvenance_type()
                    .getLiterature()
                    .getReferences());
        if (genRefs.equals(litRefs)) {
          obsoleteLitRoots.add(litRoot);
        }
      }
    }

    if (!obsoleteLitRoots.isEmpty()) {
      for (LiteratureCuratedRoot obsoleteLitRoot : obsoleteLitRoots) {
        LOGGER.debug(
            "Removing obsolete literature curated root [{}]",
            obsoleteLitRoot.getUnique_association_fields());
      }
    }
    litRoots.removeAll(obsoleteLitRoots);
  }

  @Inject
  public void setValidate(@Named("validate") boolean validate) {
    this.validate = validate;
  }

  public ConversionReport getConversionReport() {
    return conversionReport;
  }

  private Stream<DiseaseCommentStructured> getStructuredDiseasesStream(UniProtEntry uniProtEntry) {
    return uniProtEntry.getComments(CommentType.DISEASE).stream()
        .filter(x -> x instanceof DiseaseCommentStructured)
        .map(d -> (DiseaseCommentStructured) d)
        .filter(DiseaseCommentStructured::hasDefinedDisease);
  }

  private void recordResultsWithoutValidating(
      Collection<Base> bases, List<? extends Base> basesSubset) {
    bases.addAll(basesSubset);
    this.conversionReport.getTotalItemsSucceeded().getAndAdd(basesSubset.size());
  }

  private void recordValidResults(
      Collection<Base> bases,
      UniProtEntry uniProtEntry,
      DiseaseCommentStructured d,
      List<? extends Base> basesSubset) {
    basesSubset.stream()
        .filter(
            base ->
                recordValidResults(
                    uniProtEntry.getPrimaryUniProtAccession().getValue(),
                    d.getDisease().getDiseaseId().getValue(),
                    base))
        .forEach(
            base -> {
              bases.add(base);
              this.conversionReport.getTotalItemsSucceeded().getAndIncrement();
            });
  }

  private boolean recordValidResults(String accession, String disease, Base base) {
    boolean succeeded = false;
    String message = "Invalid literature evidence JSON for: ({}, {})";
    try {
      succeeded = validator.validate(jsonSchema, objectMapper.writeValueAsString(base)).succeeded();
    } catch (JsonProcessingException e) {
      message += " -- problem converting object to JSON";
    }
    if (!succeeded) {
      LOGGER.warn(message, accession, disease);
      this.conversionReport.getTotalItemsFailed().getAndIncrement();
    }
    return succeeded;
  }
}
