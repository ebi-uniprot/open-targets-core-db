package uk.ac.ebi.uniprot.ot.model.factory;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.kraken.interfaces.common.Value;
import uk.ac.ebi.kraken.interfaces.uniprot.HasEvidences;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.citationsNew.Citation;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseNote;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.mapper.Omim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.bioentity.Disease;
import uk.ac.ebi.uniprot.ot.model.bioentity.Target;
import uk.ac.ebi.uniprot.ot.model.evidence.LinkOut;
import uk.ac.ebi.uniprot.ot.model.provenance.DatabaseProvenanceType;
import uk.ac.ebi.uniprot.ot.model.provenance.Literature;
import uk.ac.ebi.uniprot.ot.model.provenance.LiteratureProvenanceType;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created 11/05/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class DefaultBaseFactory implements BaseFactory {
  public static final String UNIPROT_SOMATIC = "uniprot_somatic";
  public static final String DB_SNP_URI_FORMAT = "http://identifiers.org/dbsnp/%s";
  public static final String UNIPROT_LITERATURE = "uniprot_literature";
  public static final String CTTV_SCHEMA_VERSION = "2.0.5";
  // logger
  protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultBaseFactory.class);
  static final String ACCESS_LEVEL = "public";
  static final String UNIPROT = "uniprot";
  static final String LINK_OUT_NICE_NAME_PUBLISHED_REFERENCE = "Published reference";
  static final String ASSOCIATION_SCORE_DEFINITE = "high";
  static final String ASSOCIATION_SCORE_INDEFINITE = "medium";
  static final String SCORE_METHOD_DESCRIPTION = "Curator inference (either 1.0 or 0.5)";
  static final String ASSOCIATIONS_SCORE_METHOD_DESCRIPTION_URL =
      "https://github.com/CTTV/association_score_methods/blob/master/CTTV011_UniProt/description.md";
  private static final String IDENTIFIERS_URI = "http://identifiers.org";
  private static final String UNIPROT_URI = "http://www.uniprot.org/uniprot/%s";
  private static final String EUROPEPMC_URI = "http://europepmc.org";
  private static final String PUBMED_URI_FORMAT = EUROPEPMC_URI + "/abstract/MED/%s";
  private static final String ECO_URI_FORMAT = "http://purl.obolibrary.org/obo/%s";
  static final String CTTV_FAVOURED_CURATED_EVIDENCE_ECO =
      String.format(ECO_URI_FORMAT, "ECO_0000205");
  public static final String SO_SUBSTITUTION_URI = String.format(ECO_URI_FORMAT, "SO_0001583");
  public static final String SO_MISSING_URI = String.format(ECO_URI_FORMAT, "SO_0001822");
  public static final String SO_SEQ_ALTERATION_URI = String.format(ECO_URI_FORMAT, "SO_0001059");
  private static final String UNIPROT_URI_FORMAT = IDENTIFIERS_URI + "/uniprot/%s";
  private static final String UNIPROT_DISEASE_URI_FORMAT = UNIPROT_URI + "#pathology_and_biotech";
  protected static final String ACTIVITY_UP_DOWN = "up_or_down";
  private static final String PROTEIN_TARGET =
      "http://identifiers.org/cttv.target/protein_evidence";
  private static final List<String> INDEFINITE_DISEASE_NOTE_ASSOCIATIONS =
      asList(
          "The disease may be caused by mutations affecting the gene represented in this entry",
          "The disease may be caused by mutations affecting distinct genetic loci, including the gene represented "
              + "in this entry",
          "Disease susceptibility may be associated with variations affecting the gene represented in this entry",
          "The gene represented in this entry may act as a disease modifier",
          "The gene represented in this entry may be involved in disease pathogenesis",
          "The protein represented in this entry may be involved in disease pathogenesis");
  private static final String PUBMED_EVIDENCE_TYPE = "PubMed";
  private static final String ECO_0000269 = "ECO:0000269";
  private static final String ECO_0000303 = "ECO:0000303";
  public static final String UNIPROT_VARIANT = "uniprot_variants";
  private final LiteratureCuratedRootFactory literatureCuratedRootFactory =
      new LiteratureCuratedRootFactory(this);
  private final GeneticsRootFactory geneticsRootFactory = new GeneticsRootFactory(this);

  private Omim2EfoMapper omim2EfoMapper;
  private String uniProtReleaseVersion;

  private SomaticDbSNPMapper somaticDbSNPCache;

  @Inject
  public void setOmim2EfoMapper(Omim2EfoMapper mapper) {
    this.omim2EfoMapper = mapper;
  }

  @Inject
  public void setSomaticDbSNPMapper(SomaticDbSNPMapper somaticDbSNPCache) {
    this.somaticDbSNPCache = somaticDbSNPCache;
  }

  @Override
  public List<Base> createLiteratureCuratedRoot(
      UniProtEntry uniProtEntry, DiseaseCommentStructured structuredDisease) {
    List<Base> lcrs = new ArrayList<>();

    // get all disease evidence ids with pubmed references
    List<EvidenceId> diseasePubmedEvs = extractAllPubMedEvidenceIds(structuredDisease);

    // pubmed evidence ids exist for this disease
    if (!diseasePubmedEvs.isEmpty()) {
      for (String efo : efoMappings(structuredDisease)) {
        // for each variant, of the disease, and if
        for (Feature variantFeature : uniProtEntry.getFeatures(FeatureType.VARIANT)) {
          List<EvidenceId> variantPubmedEvs =
              variantFeature.getEvidenceIds().stream()
                  .filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
                  .collect(Collectors.toList());

          // ... containing pubmed evidence ids
          //          if (!variantPubmedEvs.isEmpty()) {
          //            VariantFeature variant = (VariantFeature) variantFeature;
          //            VariantLineInfo vli = VariantLineInfo.createInstance(variant);
          //
          //            if (isSomatic(uniProtEntry, efo, vli)
          //                && vli.containsDbSNPInfoForDisease(structuredDisease)) {
          //              lcrs.add(
          //                  literatureCuratedRootFactory.createLiteratureCuratedRoot(
          //                      uniProtEntry, structuredDisease, variantPubmedEvs, efo, vli));
          //            }
          //          }
        }

        // always add evidence for the basic disease
        lcrs.add(
            literatureCuratedRootFactory.createLiteratureCuratedRoot(
                uniProtEntry, structuredDisease, diseasePubmedEvs, efo));
      }
    }

    return lcrs;
  }

  @Override
  public List<GeneticsRoot> createGeneticsRoots(
      UniProtEntry uniProtEntry, DiseaseCommentStructured structuredDisease) {
    List<GeneticsRoot> grs = new ArrayList<>();

    efoMappings(structuredDisease)
        .forEach(
            efo -> {
              uniProtEntry
                  .getFeatures(FeatureType.VARIANT)
                  .forEach(
                      variantFeature -> {
                        // for every variant feature ...
                        List<EvidenceId> pubmedEvIds =
                            variantFeature.getEvidenceIds().stream()
                                .filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
                                .collect(Collectors.toList());

                        // ... containing pubmed evidence ids
                        if (!pubmedEvIds.isEmpty()) {
                          VariantFeature variant = (VariantFeature) variantFeature;
                          VariantLineInfo vli = VariantLineInfo.createInstance(variant);

                          // generate only germline genetics info
                          if (!isSomatic(uniProtEntry, efo, vli)
                              && vli.containsDbSNPInfoForDisease(structuredDisease)) {
                            vli.getDbSNPs()
                                .forEach(
                                    dbSNP -> {
                                      grs.add(
                                          geneticsRootFactory.createGeneticsRoot(
                                              uniProtEntry,
                                              structuredDisease,
                                              pubmedEvIds,
                                              efo,
                                              variantFeature,
                                              variant,
                                              vli,
                                              dbSNP));
                                    });
                          }
                        }
                      });
            });

    return grs;
  }

  static String latestPubMed(UniProtEntry uniProtEntry, Collection<String> pubmedIds) {
    for (Citation citation : uniProtEntry.getCitationsNew()) {
      for (String pubmedId : pubmedIds) {
        if (pubmedId.equals(citation.getCitationXrefs().getPubmedId().getValue())) {
          // return first match, as this is the most significant
          return pubmedId;
        }
      }
    }
    if (pubmedIds.isEmpty()) {
      LOGGER.warn(
          "Could not find entry's pubmed for accession {} where pubmeds are {} -- using first one",
          accession(uniProtEntry),
          pubmedIds);
    }
    return pubmedIds.iterator().next();
  }

  static String createUniProtUrl(String accession) {
    return String.format(UNIPROT_URI_FORMAT, accession);
  }

  static String dateString(Date date) {
    return DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault()).format(date.toInstant());
  }

  private static String createEcoUrl(String ecoCode) {
    return String.format(ECO_URI_FORMAT, ecoCode.replace(":", "_"));
  }

  static String createPubMedUrl(String pubmedId) {
    return String.format(PUBMED_URI_FORMAT, pubmedId);
  }

  static String accession(UniProtEntry uniProtEntry) {
    return uniProtEntry.getPrimaryUniProtAccession().getValue();
  }

  static String createUniProtDiseaseUrl(UniProtEntry uniProtEntry) {
    return String.format(UNIPROT_DISEASE_URI_FORMAT, accession(uniProtEntry));
  }

  static LinkOut createLinkOut(String niceName, String url) {
    LinkOut lo = new LinkOut();
    lo.setNice_name(niceName);
    lo.setUrl(url);
    return lo;
  }

  static HashSet<String> createDefaultECOsSet() {
    return new HashSet<>(singletonList(DefaultBaseFactory.CTTV_FAVOURED_CURATED_EVIDENCE_ECO));
  }

  SomaticDbSNPMapper getSomaticDbSNPCache() {
    return somaticDbSNPCache;
  }

  String getUniProtReleaseVersion() {
    return uniProtReleaseVersion;
  }

  @Inject
  public void setUniProtReleaseVersion(
      @Named("uniProtReleaseVersion") String uniProtReleaseVersion) {
    this.uniProtReleaseVersion = uniProtReleaseVersion;
  }

  Target createTarget(UniProtEntry uniProtEntry) {
    Target target = new Target();
    target.setTarget_type(PROTEIN_TARGET);
    target.setId(createUniProtUrl(accession(uniProtEntry)));
    target.setActivity(ACTIVITY_UP_DOWN);
    return target;
  }

  Disease createDisease(DiseaseCommentStructured structuredDisease, String efo) {
    Disease disease = new Disease();
    if (structuredDisease.hasDefinedDisease()) {
      disease.setName(structuredDisease.getDisease().getDiseaseId().getValue());
    }
    disease.setId(efo);
    disease.setAcronym(structuredDisease.getDisease().getAcronym().getValue());

    return disease;
  }

  void extractEcoAndPubMeds(
      Collection<EvidenceId> evidenceIds, Collection<String> ecos, Collection<String> pubmeds) {
    for (EvidenceId evidenceId : evidenceIds) {
      if (evidenceId.useECOCode()) {
        ecos.add(createEcoUrl(evidenceId.getEvidenceCode().getCodeValue()));
      }
      if (evidenceId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE)) {
        if (pubmeds == null) {
          pubmeds = new HashSet<>();
        }
        pubmeds.add(evidenceId.getAttribute().getValue());
      }
    }
  }

  Collection<String> extractPubMeds(Collection<EvidenceId> evidenceIds) {
    List<String> pubmeds = new ArrayList<>();
    extractEcoAndPubMeds(evidenceIds, new ArrayList<>(), pubmeds);
    return pubmeds;
  }

  Set<String> createEcoUrls(Set<String> ecos) {
    String standard269EcoUrl = createEcoUrl(ECO_0000269);
    String standard303EcoUrl = createEcoUrl(ECO_0000303);

    if (ecos.contains(standard269EcoUrl)) {
      ecos.remove(standard269EcoUrl);
      ecos.add(CTTV_FAVOURED_CURATED_EVIDENCE_ECO);

      // if we've just replaced a 269 -> 205, and still there's a 303, remove the 303,
      // because 205
      // is stronger
      if (ecos.contains(standard303EcoUrl)) {
        ecos.remove(standard303EcoUrl);
      }
    }

    return ecos;
  }

  ProvenanceType createProvenanceType(Set<String> pubmeds) {
    ProvenanceType provenanceType = new ProvenanceType();
    LiteratureProvenanceType lpt = createLiteratureProvenanceType(pubmeds);
    provenanceType.setLiterature(lpt);

    DatabaseProvenanceType database = new DatabaseProvenanceType();
    database.setId(UNIPROT);
    database.setVersion(uniProtReleaseVersion);
    provenanceType.setDatabase(database);
    return provenanceType;
  }

  LiteratureProvenanceType createLiteratureProvenanceType(Set<String> pubmeds) {
    List<Literature> lits = new ArrayList<>();

    pubmeds.forEach(
        pubmed -> {
          Literature lit = new Literature();
          lit.setLit_id(createPubMedUrl(pubmed));
          lits.add(lit);
        });

    LiteratureProvenanceType lpt = new LiteratureProvenanceType();
    lpt.setReferences(lits);
    return lpt;
  }

  protected String createConfidence(DiseaseCommentStructured structuredDisease) {
    String confidence = ASSOCIATION_SCORE_DEFINITE;

    if (structuredDisease != null) {
      DiseaseNote note = structuredDisease.getNote();
      if (note != null) {
        Optional<String> val =
            note.getTexts().stream()
                .map(Value::getValue)
                .filter(this::associationScoreForDiseaseNoteIsNotDefinite)
                .findFirst();
        if (val.isPresent()) {
          confidence = ASSOCIATION_SCORE_INDEFINITE;
        }
      }
    }

    return confidence;
  }

  private boolean isSomatic(UniProtEntry uniProtEntry, String efo, VariantLineInfo vli) {
    String accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
    List<String> dbSNPs = vli.getDbSNPs();

    if (dbSNPs.size() == 1) {
      return somaticDbSNPCache.isSomatic(accession, efo, dbSNPs.get(0));
    } else {
      return false;
    }
  }

  private List<EvidenceId> extractAllPubMedEvidenceIds(DiseaseCommentStructured structuredDisease) {
    List<EvidenceId> pubmedEvidenceIds = new ArrayList<>();
    structuredDisease.getNote().getTexts().stream()
        .map(HasEvidences::getEvidenceIds)
        .forEach(
            evIds -> {
              evIds.stream()
                  .filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
                  .forEach(pubmedEvidenceIds::add);
            });
    structuredDisease.getDisease().getDescription().getEvidenceIds().stream()
        .filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
        .forEach(pubmedEvidenceIds::add);
    structuredDisease.getEvidenceIds().stream()
        .filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
        .forEach(pubmedEvidenceIds::add);
    return pubmedEvidenceIds;
  }

  private Set<String> efoMappings(DiseaseCommentStructured structuredDisease) {
    String diseaseIdStr =
        structuredDisease.getDisease().getReference().getDiseaseReferenceId().getValue();
    return omim2EfoMapper.omim2Efo(diseaseIdStr);
  }

  private boolean associationScoreForDiseaseNoteIsNotDefinite(String noteText) {
    for (String indefiniteDiseaseNote : INDEFINITE_DISEASE_NOTE_ASSOCIATIONS) {
      if (noteText.startsWith(indefiniteDiseaseNote)) {
        return true;
      }
    }
    return false;
  }

  public static String getMappedId(String efo) {
    try {
      URI uri = new URI(efo);
      String[] segments = uri.getPath().split("/");
      String idStr = segments[segments.length - 1];
      return idStr;
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }
}
