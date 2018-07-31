package uk.ac.ebi.uniprot.ot.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import uk.ac.ebi.uniprot.tools.opentargets.DiseaseAssocProducer;
import uk.ac.ebi.uniprot.tools.opentargets.config.DiseaseAssocGuiceModule;

import java.io.IOException;

/**
 * Main class for CTTV evidence string generation
 * data dump.
 *
 * @author Edd
 */
public class DiseaseAssocGenerator {
    private DiseaseAssocGenerator(){}

    public static void main(String[] args) throws IOException {
        DiseaseAssocConfig arguments2Configuration = DiseaseAssocConfig.fromCommandLine(args);

        Injector injector = Guice.createInjector(new DiseaseAssocGuiceModule(arguments2Configuration));
        DiseaseAssocProducer diseaseAssocProducer = injector.getInstance(DiseaseAssocProducer.class);
        diseaseAssocProducer.produce();
    }

}
