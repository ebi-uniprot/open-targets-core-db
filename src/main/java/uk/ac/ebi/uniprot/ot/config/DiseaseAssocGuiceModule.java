package uk.ac.ebi.uniprot.ot.config;

import java.io.File;
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
	Map<String, List<InfectiousDiseaseEFO>> provideAccession2EfoMapper() {
		List<InfectiousDiseaseEFO> list = new ArrayList<InfectiousDiseaseEFO>();
		InfectiousDiseaseEFO efo = new InfectiousDiseaseEFO();
		efo.setText(
				"<text evidence=\"13 14 15 16 17 18 21\">(Microbial infection) Facilitates human coronaviruses SARS-CoV and SARS-CoV-2 infections via two independent mechanisms, proteolytic cleavage of ACE2 receptor which promotes viral uptake, and cleavage of coronavirus spike glycoproteins which activates the glycoprotein for host cell entry (PubMed:24227843, PubMed:32142651). Proteolytically cleaves and activates the spike glycoproteins of human coronavirus 229E (HCoV-229E) and human coronavirus EMC (HCoV-EMC) and the fusion glycoproteins F0 of Sendai virus (SeV), human metapneumovirus (HMPV), human parainfluenza 1, 2, 3, 4a and 4b viruses (HPIV). Essential for spread and pathogenesis of influenza A virus (strains H1N1, H3N2 and H7N9); involved in proteolytic cleavage and activation of hemagglutinin (HA) protein which is essential for viral infectivity.</text>");
		efo.setId("http://purl.obolibrary.org/obo/MONDO_0100096");
		efo.setName("COVID-19");
		list.add(efo);
		Map<String, List<InfectiousDiseaseEFO>> map = new HashMap<String, List<InfectiousDiseaseEFO>>();
		map.put("O15393", list);
		return map;
	}

	@Provides
	SomaticDbSNPMapper provideSomaticDbSNPMapper() {
		return new SomaticDbSNPMapper(new File(config.getSomaticDbSNPFile()));
	}
}
