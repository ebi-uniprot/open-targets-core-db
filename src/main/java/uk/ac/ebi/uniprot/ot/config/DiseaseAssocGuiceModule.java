package uk.ac.ebi.uniprot.ot.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;
import uk.ac.ebi.uniprot.ot.cli.DiseaseAssocConfig;
import uk.ac.ebi.uniprot.ot.mapper.FFOmim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.Omim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.factory.BaseFactory;
import uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Configuration bindings for generating disease associations.
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class DiseaseAssocGuiceModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseAssocGuiceModule.class);
    private static final int ENTRY_ITERATOR_THREAD_COUNT = 16;
    private static final int ENTRY_ITERATOR_ENTRY_QUEUESIZE = 10000;
    private static final int ENTRY_ITERATOR_FF_QUEUE_SIZE = 50000;
    private final DiseaseAssocConfig config;

    public DiseaseAssocGuiceModule(DiseaseAssocConfig config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(BaseFactory.class).to(DefaultBaseFactory.class);

        bind(File.class).annotatedWith(Names.named("outputFile")).toInstance(
                new File(config.getOutputFilePath()));
        bindConstant().annotatedWith(Names.named("uniProtReleaseVersion")).to(config.getUniProtReleaseVersion());
        bindConstant().annotatedWith(Names.named("validate")).to(config.isValidate());
        bindConstant().annotatedWith(Names.named("verbose")).to(config.isVerbose());
        bindConstant().annotatedWith(Names.named("max")).to(config.getCount());
    }

    @Provides
    Iterator<UniProtEntry> getUniProtEntryIterator(UniProtService uniProtService) throws FileNotFoundException, ServiceException {
//        NewEntryIterator entryIterator = new NewEntryIterator(ENTRY_ITERATOR_THREAD_COUNT,
//                                                              ENTRY_ITERATOR_ENTRY_QUEUESIZE,
//                                                              ENTRY_ITERATOR_FF_QUEUE_SIZE);
//        entryIterator.setInput(config.getUniprotFFPath());
//        return entryIterator;

//        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
//        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
//        uniProtService.start();

        Query swissprot = UniProtQueryBuilder.swissprot();

        return uniProtService.getEntries(swissprot);
    }

    @Provides
    UniProtService getUniProtService() {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        uniProtService.start();
        return uniProtService;
    }

    @Provides
    Omim2EfoMapper provideOmim2EfoMapper() {
        return new FFOmim2EfoMapper(new File(config.getEfoFile()));
    }

    @Provides
    SomaticDbSNPMapper provideSomaticDbSNPMapper() {
        return new SomaticDbSNPMapper(new File(config.getSomaticDbSNPFile()));
    }

}
