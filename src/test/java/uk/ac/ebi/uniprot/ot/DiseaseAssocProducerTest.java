package uk.ac.ebi.uniprot.ot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.ebi.kraken.ffwriter.LineType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ot.converter.UniProtDiseaseAssocCollator;
import uk.ac.ebi.uniprot.ot.input.UniProtEvSource;
import uk.ac.ebi.uniprot.ot.mapper.FFOmim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory;

import com.google.common.io.Files;

/**
 * Created 16/06/17
 *
 * @author Edd
 */
public class DiseaseAssocProducerTest {
  private static final String EFO_MAPPINGS = "/mappings/omim2efo.mappings";
  private static final String SOMATIC_MAPPINGS = "/mappings/somatic_census.txt";

  private UniProtEntry P04637;
  private static File tempDir;
  private UniProtEntryMocker entryMocker;

  @BeforeEach
  void createMockEntry() throws IOException {
    tempDir = Files.createTempDir();

    entryMocker = UniProtEntryMocker.createDefaultEntry();

    entryMocker.updateEntryObject(LineType.AC, "AC   P04637; O52764; P21253;");
    entryMocker.updateEntryObject(
        LineType.CC,
        "CC   -!- DISEASE: Li-Fraumeni syndrome (LFS) [MIM:151623]: Autosomal\n"
            + "CC       dominant familial cancer syndrome that in its classic form is\n"
            + "CC       defined by the existence of a proband affected by a sarcoma before\n"
            + "CC       45 years with a first degree relative affected by any tumor before\n"
            + "CC       45 years and another first degree relative with any tumor before\n"
            + "CC       45 years or a sarcoma at any age. Other clinical definitions for\n"
            + "CC       LFS have been proposed (PubMed:8118819 and PubMed:8718514) and\n"
            + "CC       called Li-Fraumeni like syndrome (LFL). In these families affected\n"
            + "CC       relatives develop a diverse set of malignancies at unusually early\n"
            + "CC       ages. Four types of cancers account for 80% of tumors occurring in\n"
            + "CC       TP53 germline mutation carriers: breast cancers, soft tissue and\n"
            + "CC       bone sarcomas, brain tumors (astrocytomas) and adrenocortical\n"
            + "CC       carcinomas. Less frequent tumors include choroid plexus carcinoma\n"
            + "CC       or papilloma before the age of 15, rhabdomyosarcoma before the age\n"
            + "CC       of 5, leukemia, Wilms tumor, malignant phyllodes tumor, colorectal\n"
            + "CC       and gastric cancers. {ECO:0000269|PubMed:10484981,\n"
            + "CC       ECO:0000269|PubMed:1565144, ECO:0000269|PubMed:1737852,\n"
            + "CC       ECO:0000269|PubMed:1933902, ECO:0000269|PubMed:1978757,\n"
            + "CC       ECO:0000269|PubMed:2259385, ECO:0000269|PubMed:7887414,\n"
            + "CC       ECO:0000269|PubMed:8825920, ECO:0000269|PubMed:9452042}. Note=The\n"
            + "CC       disease is caused by mutations affecting the gene represented in\n"
            + "CC       this entry.");

    entryMocker.updateEntryObject(
        LineType.FT,
        "FT   CHAIN         1    393       Cellular tumor antigen p53.\n"
            + "FT                                /FTId=PRO_0000185703.\n"
            + "FT   VARIANT      82     82       P -> L (in LFS; germline mutation and in\n"
            + "FT                                sporadic cancers; somatic mutation).\n"
            + "FT                                /FTId=VAR_044621.");

    P04637 = entryMocker.toUniProtEntry();
  }

  @AfterAll
  static void cleanup() {
    tempDir.deleteOnExit();
  }

  @Test
  void shouldCreateValidEvSource() {
    UniProtEvSource evSource = DiseaseAssocProducer.createEvSource(P04637);
    assertThat(evSource.getEvidenceSource(), is(P04637));
  }

  @Test
  void shouldCreateValidInstance() throws IOException, URISyntaxException {
    Iterator<UniProtEntry> entryIterator = getSingleEntryIterator(P04637);

    File outFile = new File(tempDir.getAbsolutePath() + "/outFile");
    DefaultBaseFactory baseFactory = new DefaultBaseFactory();
    baseFactory.setOmim2EfoMapper(
        new FFOmim2EfoMapper(DiseaseAssocProducer.class.getResource(EFO_MAPPINGS)));
    UniProtDiseaseAssocCollator sourceConverter = new UniProtDiseaseAssocCollator(baseFactory);
    DiseaseAssocProducer producer =
        new DiseaseAssocProducer(entryIterator, outFile, sourceConverter);
    producer.produce();
    assertThat(producer, is(not(nullValue())));
    assertThat(outFile.length(), is(greaterThan(0L)));
  }

  @Test
  void shouldCreateValidSomaticMutationInstance() throws URISyntaxException, IOException {
    ensureEntryIsSomatic();
    Iterator<UniProtEntry> entryIterator = getSingleEntryIterator(entryMocker.toUniProtEntry());

    File outFile = new File(tempDir.getAbsolutePath() + "/outFile");
    DefaultBaseFactory baseFactory = new DefaultBaseFactory();
    baseFactory.setOmim2EfoMapper(
        new FFOmim2EfoMapper(DiseaseAssocProducer.class.getResource(EFO_MAPPINGS)));
    baseFactory.setSomaticDbSNPMapper(
        new SomaticDbSNPMapper(DiseaseAssocProducer.class.getResource(SOMATIC_MAPPINGS)));
    UniProtDiseaseAssocCollator sourceConverter = new UniProtDiseaseAssocCollator(baseFactory);
    DiseaseAssocProducer producer =
        new DiseaseAssocProducer(entryIterator, outFile, sourceConverter);
    producer.produce();
    assertThat(producer, is(not(nullValue())));
    assertThat(outFile.length(), is(greaterThan(0L)));
  }

  private void ensureEntryIsSomatic() {
    entryMocker.updateEntryObject(
        LineType.FT,
        "FT   VARIANT     83    83       E -> K (in LFS; somatic "
            + "mutation);\n"
            + "FT                                shows an increase in lipid kinase\n"
            + "FT                                activity; oncogenic in vivo; occurs in\n"
            + "FT                                the interface between the PI3K helical\n"
            + "FT                                domain and the nSH2 (N-terminal SH2)\n"
            + "FT                                region of the p85 regulatory subunit and\n"
            + "FT                                may reduce the inhibitory effect of p85;\n"
            + "FT                                requires interaction with RAS to induce\n"
            + "FT                                cellular transformation; enhances\n"
            + "FT                                invadopodia-mediated extracellular matrix\n"
            + "FT                                degradation and invasion in breast cancer\n"
            + "FT                                cells; dbSNP:rs104886003).\n"
            + "FT                                {ECO:0000269|PubMed:15289301,\n"
            + "FT                                ECO:0000269|PubMed:15520168,\n"
            + "FT                                ECO:0000269|PubMed:15712344,\n"
            + "FT                                ECO:0000269|PubMed:15784156,\n"
            + "FT                                ECO:0000269|PubMed:15930273,\n"
            + "FT                                ECO:0000269|PubMed:15994075,\n"
            + "FT                                ECO:0000269|PubMed:16322209,\n"
            + "FT                                ECO:0000269|PubMed:16353168,\n"
            + "FT                                ECO:0000269|PubMed:16432179,\n"
            + "FT                                ECO:0000269|PubMed:16533766,\n"
            + "FT                                ECO:0000269|PubMed:17673550,\n"
            + "FT                                ECO:0000269|PubMed:21708979,\n"
            + "FT                                ECO:0000269|PubMed:22729224}.");
  }

  private Iterator<UniProtEntry> getSingleEntryIterator(UniProtEntry entry) {
    return new Iterator<UniProtEntry>() {
      int entryCounter = 0;

      @Override
      public boolean hasNext() {
        return entryCounter++ < 1;
      }

      @Override
      public UniProtEntry next() {
        return entry;
      }
    };
  }
}
