package uk.ac.ebi.uniprot.ot.model.variant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureSequence;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.model.bioentity.Variant;
import uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory;

/**
 * Class that extracts variant information from FT VAR lines
 *
 * @author Edd
 */
public class VariantLineInfo {
  static final String SNP_SINGLE = "snp single";
  static final String STRUCTURAL_VARIANT = "structural variant";
  static final String SNP_MULTIPLE = "snp multiple";
  private static final Logger LOGGER = LoggerFactory.getLogger(VariantLineInfo.class);
  private List<String> diseaseAcronyms;
  private String description;
  private List<String> dbSNPs;
  private VariantType type;
  private String transformation;

  private VariantLineInfo() {
    this.dbSNPs = new ArrayList<>();
    this.diseaseAcronyms = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "VariantLineInfo{"
        + "diseaseAcronyms="
        + diseaseAcronyms
        + ", description='"
        + description
        + '\''
        + ", dbSNPs="
        + dbSNPs
        + ", type="
        + type
        + '}';
  }

  public VariantType getType() {
    return type;
  }

  public List<String> getDiseaseAcronyms() {
    return diseaseAcronyms;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getDbSNPs() {
    return dbSNPs;
  }

  public String getMutationTransformation() {
    return transformation;
  }

  public boolean containsDbSNPInfoForDisease(DiseaseCommentStructured disease) {
    String diseaseAcronym = disease.getDisease().getAcronym().getValue();
    return this.getDiseaseAcronyms().contains(diseaseAcronym) && !this.getDbSNPs().isEmpty();
  }

  public static VariantLineInfo createInstance(VariantFeature variantFeature) {
    String variantText = variantFeature.getVariantReport().getValue();

    // todo add unique info about variant here
    VariantLineInfo vli = new VariantLineInfo();

    // variant type
    FeatureSequence origFSeq = variantFeature.getOriginalSequence();
    List<FeatureSequence> newFSeqs = variantFeature.getAlternativeSequences();
    if (origFSeq != null && newFSeqs != null) {
      if (origFSeq.getValue().length() == 1 && newFSeqs.get(0).getValue().length() == 1) {
        vli.type = VariantType.MISSENSE;
      } else if (origFSeq.getValue().length() > 1 && newFSeqs.get(0).getValue().length() > 1) {
        vli.type = VariantType.INS_DEL;
      } else {
        vli.type = VariantType.DELETION;
      }
    } else {
      vli.type = VariantType.DELETION;
    }

    vli.transformation = getMutationString(variantFeature, vli);

    String[] infoComponents = variantText.split(";");

    for (int i = 0; i < infoComponents.length; i++) {
      infoComponents[i] = infoComponents[i].trim();

      // handling diseases
      if (infoComponents[i].startsWith("in ")) {
        String[] diseaseAcros = infoComponents[i].substring(3).split(",|( and )");
        List<String> dAcros = new ArrayList<>();
        for (String diseaseAcro : diseaseAcros) {
          dAcros.add(diseaseAcro.trim());
        }
        vli.diseaseAcronyms = dAcros;
      } else if (infoComponents[i].startsWith("dbSNP:")) {
        String[] dbSnps = infoComponents[i].split(",|( and )");
        List<String> dbSnpList = new ArrayList<>();
        for (String dbSnp : dbSnps) {
          dbSnpList.add(dbSnp.substring(6).trim());
        }
        vli.dbSNPs = dbSnpList;
      } else {
        vli.description = infoComponents[i];
      }
    }

    return vli;
  }

  private static String getMutationString(VariantFeature variantFeature, VariantLineInfo vli) {
    switch (vli.getType()) {
      case MISSENSE:
        return String.format(
            "%s -> %s",
            variantFeature.getOriginalSequence().getValue(),
            variantFeature.getAlternativeSequences().get(0).getValue());
      case DELETION:
        return String.format(
            "%d %s -> del",
            variantFeature.getFeatureLocation().getStart(),
            variantFeature.getOriginalSequence().getValue());
      case INS_DEL:
        return String.format(
            "%s -> delins%s",
            variantFeature.getOriginalSequence().getValue(),
            variantFeature.getAlternativeSequences().get(0).getValue());
      default:
        LOGGER.warn("Unknown variant type: {}", vli.getType().name());
    }
    return null;
  }

  public static Variant createVariant(VariantLineInfo variantLineInfo) {
    Variant modelVariant = new Variant();
    modelVariant.setId(createDbSnpUrls(variantLineInfo));
    switch (variantLineInfo.getType()) {
      case MISSENSE:
        modelVariant.setType(SNP_SINGLE);
        break;
      case DELETION:
        modelVariant.setType(STRUCTURAL_VARIANT);
        break;
      case INS_DEL:
        modelVariant.setType(SNP_MULTIPLE);
        break;
      default:
        LOGGER.warn("Unknown variant type: " + variantLineInfo.getType().name());
    }
    return modelVariant;
  }

  public static String getFunctionalConsequenceURL(VariantLineInfo vli) {
    switch (vli.getType()) {
      case MISSENSE:
        return DefaultBaseFactory.SO_SUBSTITUTION_URI;
      case DELETION:
        return DefaultBaseFactory.SO_MISSING_URI;
      case INS_DEL:
        return DefaultBaseFactory.SO_SEQ_ALTERATION_URI;
      default:
        LOGGER.warn("Unknown variant type: {}", vli.getType().name());
    }
    return null;
  }

  public static String createDbSnpUrls(VariantLineInfo variantLineInfo) {
    List<String> dbSNPs =
        variantLineInfo.getDbSNPs().stream()
            .map(dbsnp -> String.format(DefaultBaseFactory.DB_SNP_URI_FORMAT, dbsnp))
            .collect(Collectors.toList());

    if (dbSNPs.isEmpty() || dbSNPs.size() > 1) {
      LOGGER.warn("Expected variant line information to contain only 1 dbsnp: {}", dbSNPs);
    }
    return dbSNPs.get(0);
  }
}
