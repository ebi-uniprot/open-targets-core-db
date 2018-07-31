package uk.ac.ebi.uniprot.ot.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.ot.DiseaseAssocProducer;
import uk.ac.ebi.uniprot.ot.config.DiseaseAssocGuiceModule;

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
        registerJAPIServiceShutdown(injector);
        DiseaseAssocProducer diseaseAssocProducer = injector.getInstance(DiseaseAssocProducer.class);
        diseaseAssocProducer.produce();
    }

    private static void registerJAPIServiceShutdown(Injector injector) {
        UniProtService uniProtService = injector.getInstance(UniProtService.class);
        Runtime.getRuntime().addShutdownHook(new Thread(uniProtService::stop));
    }
}
