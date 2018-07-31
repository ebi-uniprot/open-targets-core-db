package uk.ac.ebi.uniprot.ot.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.parser.NewEntryIterator;
import uk.ac.ebi.uniprot.tools.opentargets.cli.DiseaseAssocConfig;
import uk.ac.ebi.uniprot.tools.opentargets.mapper.FFOmim2EfoMapper;
import uk.ac.ebi.uniprot.tools.opentargets.mapper.Omim2EfoMapper;
import uk.ac.ebi.uniprot.tools.opentargets.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.tools.opentargets.model.factory.BaseFactory;
import uk.ac.ebi.uniprot.tools.opentargets.model.factory.DefaultBaseFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Configuration bindings for a {@link DiseaseAssocProducer}.
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

    @Override protected void configure() {
        bind(BaseFactory.class).to(DefaultBaseFactory.class);

        bind(File.class).annotatedWith(Names.named("outputFile")).toInstance(
                new File(config.getOutputFilePath()));
        bindConstant().annotatedWith(Names.named("uniProtReleaseVersion")).to(config.getUniProtReleaseVersion());
        bindConstant().annotatedWith(Names.named("validate")).to(config.isValidate());
        bindConstant().annotatedWith(Names.named("verbose")).to(config.isVerbose());
        bindConstant().annotatedWith(Names.named("max")).to(config.getCount());
    }

    @Provides
    private Iterator<UniProtEntry> getUniProtEntryIterator() throws FileNotFoundException {
        NewEntryIterator entryIterator = new NewEntryIterator(ENTRY_ITERATOR_THREAD_COUNT,
                                                              ENTRY_ITERATOR_ENTRY_QUEUESIZE,
                                                              ENTRY_ITERATOR_FF_QUEUE_SIZE);
        entryIterator.setInput(config.getUniprotFFPath());
        return entryIterator;
    }

    @Provides Omim2EfoMapper provideOmim2EfoMapper() {
        return new FFOmim2EfoMapper(new File(config.getEfoFile()));
    }

    @Provides SomaticDbSNPMapper provideSomaticDbSNPMapper() {
        return new SomaticDbSNPMapper(new File(config.getSomaticDbSNPFile()));
    }

}
