package uk.ac.ebi.uniprot.ot.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File based implementation of the OMIM -> EFO mapper. In this iteration, the file format is
 * simpler than last time, and contains two columns, OMIM and EFO.
 *
 * @author Edd
 */
public class FFOmim2EfoMapper implements Omim2EfoMapper {
  // logger
  private static final Logger LOGGER = LoggerFactory.getLogger(FFOmim2EfoMapper.class);
  private static final String OMIM_EFO_MAPPING_PATTERN = "^OMIM:([0-9]+)\\s+(http://[^\\s]+).*";
  private static final Pattern OMIM_MAPPING = Pattern.compile(OMIM_EFO_MAPPING_PATTERN);

  private HashMap<String, Set<String>> omim2efoMap;
  private Path sourceFile;
  private boolean initialised;
  private int keysCount;

  public FFOmim2EfoMapper(File sourceFile) {
    this(sourceFile.toPath());
  }

  public FFOmim2EfoMapper(URL url) throws URISyntaxException {
    this(Paths.get(url.toURI()));
  }

  private FFOmim2EfoMapper(Path sourceFile) {
    this.sourceFile = sourceFile;
    this.omim2efoMap = new HashMap<>();
    this.initialised = false;
    initMap();
  }

  public int getKeysCount() {
    return keysCount;
  }

  @Override
  public Set<String> omim2Efo(String omim) {
    if (initialised) {
      if (this.omim2efoMap.containsKey(omim)) {
        return this.omim2efoMap.get(omim);
      } else {
        LOGGER.warn("No mapping found for OMIM: {}", omim);
      }
    } else {
      LOGGER.warn("Omim2Efo map not initialised. Please check why it could not be initialised.");
    }
    return Collections.emptySet();
  }

  private void insertToOmim2EfoMap(String key, String value) {
    if (!this.omim2efoMap.containsKey(key)) {
      this.omim2efoMap.put(key, new HashSet<>());
    }
    this.omim2efoMap.get(key).add(value);
  }

  private void initMap() {
    try (BufferedReader reader = Files.newBufferedReader(sourceFile, Charset.defaultCharset())) {
      String line = "";
      while ((line = reader.readLine()) != null) {
        Matcher mappingMatcher = OMIM_MAPPING.matcher(line);
        if (mappingMatcher.matches()) {
          if (mappingMatcher.groupCount() == 2) {
            insertToOmim2EfoMap(mappingMatcher.group(1), mappingMatcher.group(2));
          } else {
            LOGGER.warn("Could not understand mapping: {}", line);
          }
        }
      }
      initialised = true;
      keysCount = omim2efoMap.keySet().size();
      LOGGER.debug("Number of (omim, efo) mappings: {}", keysCount);

    } catch (IOException e) {
      LOGGER.error("Problem reading file: {}", e);
    }
  }
}
