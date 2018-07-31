package uk.ac.ebi.uniprot.ot.mapper;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created 26/06/17
 * @author Edd
 */
public class SomaticDbSNPMapper {
    private static final Logger LOGGER = getLogger(SomaticDbSNPMapper.class);
    private static final String SOMATIC_MAPPING_PATTERN =
            "^(rs[0-9]+)[ \\t]+([A-Za-z0-9]+)[ \\t]+\\w+[ \\t]+([A-Za-z0-9_]+).*$";
    private static final Pattern SOMATIC_MAPPING = Pattern.compile(SOMATIC_MAPPING_PATTERN);
    private static final Pattern EFO_RESOURCE_PATTERN = Pattern.compile(".*/(.*)");
    private final Path sourceFile;
    private final Set<SomaticDbSNPInfo> somaticInfoCache;
    private boolean initialised;
    private int somaticDbSNPCount;

    public SomaticDbSNPMapper(File sourceFile) {
        this(sourceFile.toPath());
    }

    public SomaticDbSNPMapper(URL url) throws URISyntaxException {
        this(Paths.get(url.toURI()));
    }

    private SomaticDbSNPMapper(Path sourceFile) {
        this.sourceFile = sourceFile;
        this.somaticInfoCache = new HashSet<>();
        this.initialised = false;
        initMap();
    }

    public boolean isSomatic(String accession, String efo, String dbSNP) {
        String efoId = efo;
        if (initialised) {
            Matcher efoMatcher = EFO_RESOURCE_PATTERN.matcher(efo);
            if (efoMatcher.matches()) {
                efoId = efoMatcher.group(1);
            }

            return this.somaticInfoCache.contains(new SomaticDbSNPInfo(accession, efoId, dbSNP));
        } else {
            LOGGER.warn("Somatic info cache not initialised. Please check why it could not be initialised.");
        }
        return false;
    }

    private void initMap() {
        try (BufferedReader reader = Files.newBufferedReader(sourceFile, Charset.defaultCharset())) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                Matcher mappingMatcher = SOMATIC_MAPPING.matcher(line);
                if (mappingMatcher.matches()) {
                    if (mappingMatcher.groupCount() == 3) {
                        somaticInfoCache.add(
                                new SomaticDbSNPInfo(
                                        mappingMatcher.group(2),
                                        mappingMatcher.group(3),
                                        mappingMatcher.group(1))
                        );
                    }
                    else {
                        LOGGER.warn("Could not understand mapping: {}", line);
                    }
                }
            }
            initialised = true;
            somaticDbSNPCount = somaticInfoCache.size();
            LOGGER.debug("Number of somatic (accession, efo, dbSNP) mappings: {}", somaticDbSNPCount);

        } catch (IOException e) {
            LOGGER.error("Problem reading file: {}", e);
        }
    }

    public static class SomaticDbSNPInfo {
        private final String accession;
        private final String efo;
        private final String dbSNP;

        public SomaticDbSNPInfo(String accession, String efo, String dbSNP) {
            this.accession = accession;
            this.efo = efo;
            this.dbSNP = dbSNP;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SomaticDbSNPInfo that = (SomaticDbSNPInfo) o;

            if (accession != null ? !accession.equals(that.accession) : that.accession != null) {
                return false;
            }
            if (efo != null ? !efo.equals(that.efo) : that.efo != null) {
                return false;
            }
            return dbSNP != null ? dbSNP.equals(that.dbSNP) : that.dbSNP == null;
        }

        @Override public int hashCode() {
            int result = accession != null ? accession.hashCode() : 0;
            result = 31 * result + (efo != null ? efo.hashCode() : 0);
            result = 31 * result + (dbSNP != null ? dbSNP.hashCode() : 0);
            return result;
        }
    }
}
