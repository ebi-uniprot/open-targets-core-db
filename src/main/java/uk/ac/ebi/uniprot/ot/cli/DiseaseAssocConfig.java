package uk.ac.ebi.uniprot.ot.cli;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Command line interface for producing the CTTV evidence string JSON
 *
 * <p>Created 21/05/15
 *
 * @author Edd
 */
@Parameters(
    commandDescription =
        "Generate a JSON gene <-> disease association data dump for the Open Targets project.")
public class DiseaseAssocConfig {
  // logger
  private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseAssocGenerator.class);

  private DiseaseAssocConfig() {
    this.count = -1;
  }

  @Parameter(
      names = "-o",
      required = true,
      description = "Name of the data dump file to contain the evidence strings")
  private String outputFilePath;

  @Parameter(names = "-efo", required = true, description = "Path to the EFO CSV file")
  private String efoFile;

  @Parameter(
      names = "-somaticDbSNP",
      required = true,
      description = "Path to the file containing somatic dbSNPS")
  private String somaticDbSNPFile;

  @Parameter(
      names = "-count",
      required = false,
      description = "Maximum number of evidence strings to generate")
  private int count;

  @Parameter(names = "-version", required = true, description = "UniProt release version number")
  private String uniProtReleaseVersion;

  @Parameter(
      names = "-validate",
      required = false,
      description = "Validate against OpenTargets schemas")
  private boolean validate;

  @Parameter(
      names = "-verbose",
      required = false,
      description = "Print JSON also to standard output")
  private boolean verbose;

  public String getUniProtReleaseVersion() {
    return uniProtReleaseVersion;
  }

  public boolean isVerbose() {
    return this.verbose;
  }

  public String getOutputFilePath() {
    return outputFilePath;
  }

  public int getCount() {
    return count;
  }

  public String getEfoFile() {
    return efoFile;
  }

  public String getSomaticDbSNPFile() {
    return somaticDbSNPFile;
  }

  public boolean isValidate() {
    return validate;
  }

  public void setSomaticDbSNPFile(String somaticDbSNPFile) {
    this.somaticDbSNPFile = somaticDbSNPFile;
  }

  public void setOutputFilePath(String outputFilePath) {
    this.outputFilePath = outputFilePath;
  }

  public void setEfoFile(String efoFile) {
    this.efoFile = efoFile;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setUniProtReleaseVersion(String uniProtReleaseVersion) {
    this.uniProtReleaseVersion = uniProtReleaseVersion;
  }

  public void setValidate(boolean validate) {
    this.validate = validate;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  private boolean validateArguments(String[] args) {
    JCommander cmder = new JCommander(this);

    cmder.parse(args);

    // validate paths
    File output = new File(outputFilePath);
    if (output.exists()) {
      LOGGER.warn("Output file already exists ({}) and will be overwritten", outputFilePath);
    }

    LOGGER.debug("command line arguments are valid");
    return true;
  }

  static DiseaseAssocConfig fromCommandLine(String[] args) {
    DiseaseAssocConfig commandLineHandler = new DiseaseAssocConfig();

    if (commandLineHandler.validateArguments(args)) {
      return commandLineHandler;
    } else {
      throw new IllegalArgumentException("Invalid arguments supplied to DiseaseAssocConfig");
    }
  }
}
