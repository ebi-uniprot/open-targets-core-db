package uk.ac.ebi.uniprot.ot.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.ot.cli.DiseaseAssocConfig;
import uk.ac.ebi.uniprot.ot.mapper.FFOmim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.Omim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.InfectiousDiseaseEFO;
import uk.ac.ebi.uniprot.ot.model.factory.BaseFactory;
import uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory;

/**
 * Configuration bindings for generating disease associations.
 *
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

		bind(File.class).annotatedWith(Names.named("outputFile")).toInstance(new File(config.getOutputFilePath()));
		bindConstant().annotatedWith(Names.named("uniProtReleaseVersion")).to(config.getUniProtReleaseVersion());
		bindConstant().annotatedWith(Names.named("validate")).to(config.isValidate());
		bindConstant().annotatedWith(Names.named("verbose")).to(config.isVerbose());
		bindConstant().annotatedWith(Names.named("max")).to(config.getCount());
	}

	@Provides
	Iterator<UniProtEntry> getUniProtEntryIterator(UniProtService uniProtService) throws ServiceException {
		// return uniProtService.getEntries(UniProtQueryBuilder.accession("O14672"));
		return uniProtService.getEntries(UniProtQueryBuilder.swissprot());
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
	Map<String, List<InfectiousDiseaseEFO>> provideAccession2EfoMapper() throws IOException {
		File file = new File("src/bin/infectiousDiseaseMapping");
		FileReader fr = new FileReader(file); // reads the file
		BufferedReader br = new BufferedReader(fr);
		String line;
		Map<String, List<InfectiousDiseaseEFO>> map = new HashMap<String, List<InfectiousDiseaseEFO>>();
		while ((line = br.readLine()) != null) {
			List<InfectiousDiseaseEFO> list = new ArrayList<InfectiousDiseaseEFO>();
			String[] mapping = line.split("\\|");
			InfectiousDiseaseEFO efo = new InfectiousDiseaseEFO();
			efo.setText(mapping[1]);
			efo.setId(mapping[2]);
			efo.setName(mapping[3]);
			list.add(efo);
			map.put(mapping[0], list);
		}
		return map;
	}

	@Provides
	SomaticDbSNPMapper provideSomaticDbSNPMapper() {
		return new SomaticDbSNPMapper(new File(config.getSomaticDbSNPFile()));
	}
}
