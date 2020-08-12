package uk.ac.ebi.uniprot.ot.model.factory;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.InfectiousDiseaseEFO;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;
import uk.ac.ebi.uniprot.ot.model.bioentity.Disease;
import uk.ac.ebi.uniprot.ot.model.evidence.association_score.AssScoreMethod;
import uk.ac.ebi.uniprot.ot.model.evidence.association_score.ProbabilityAssScore;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

public class InfectiousDiseaseBaseFactory extends DefaultBaseFactory {

	protected static final Logger LOGGER = LoggerFactory.getLogger(InfectiousDiseaseBaseFactory.class);

	private final InfectiousLiteratureCuratedRootFactory literatureCuratedRootFactory = new InfectiousLiteratureCuratedRootFactory(
			this);

	private Map<String, List<InfectiousDiseaseEFO>> accession2EfoMapper;

	@Inject
	public void setAccession2EfoMapper(Map<String, List<InfectiousDiseaseEFO>> mapper) {
		this.accession2EfoMapper = mapper;
	}

	@Inject
	public void setSomaticDbSNPMapper(SomaticDbSNPMapper somaticDbSNPCache) {
		this.somaticDbSNPCache = somaticDbSNPCache;
	}

	public List<LiteratureCuratedRoot> createLiteratureCuratedRoot(UniProtEntry uniProtEntry,
			InfectiousDisease infectiousDisease) {
		List<LiteratureCuratedRoot> lcrs = new ArrayList<>();

		// get all disease evidence ids with pubmed references
		List<EvidenceId> diseasePubmedEvs = infectiousDisease.getEvidenceIds();

		// pubmed evidence ids exist for this disease
		if (!diseasePubmedEvs.isEmpty()) {
			for (InfectiousDiseaseEFO efo : efoMappings(uniProtEntry, infectiousDisease)) {
				// for each variant, of the disease, and if
				for (Feature variantFeature : uniProtEntry.getFeatures(FeatureType.VARIANT)) {
					List<EvidenceId> variantPubmedEvs = variantFeature.getEvidenceIds().stream()
							.filter(evId -> evId.getTypeValue().equals(PUBMED_EVIDENCE_TYPE))
							.collect(Collectors.toList());

					// ... containing pubmed evidence ids
					if (!variantPubmedEvs.isEmpty()) {
						VariantFeature variant = (VariantFeature) variantFeature;
						VariantLineInfo vli = VariantLineInfo.createInstance(variant);

						if (isSomatic(uniProtEntry, efo.getId(), vli)) {
							lcrs.add(literatureCuratedRootFactory.createLiteratureCuratedRoot(uniProtEntry,
									infectiousDisease, variantPubmedEvs, efo, vli));
						}
					}
				}

				// always add evidence for the basic disease
				lcrs.add(literatureCuratedRootFactory.createLiteratureCuratedRoot(uniProtEntry, infectiousDisease,
						diseasePubmedEvs, efo));
			}
		}

		return lcrs;
	}


	static HashSet<String> createDefaultECOsSet() {
		return new HashSet<>(singletonList(InfectiousDiseaseBaseFactory.CTTV_FAVOURED_CURATED_EVIDENCE_ECO));
	}

	@Inject
	public void setUniProtReleaseVersion(@Named("uniProtReleaseVersion") String uniProtReleaseVersion) {
		this.uniProtReleaseVersion = uniProtReleaseVersion;
	}

	Disease createDisease(InfectiousDisease infectiousDisease, InfectiousDiseaseEFO efo) {
		Disease disease = new Disease();
		disease.setName(efo.getName());
		disease.setId(efo.getId());
		return disease;
	}


	ProbabilityAssScore createAssociationScore(InfectiousDisease structuredDisease) {
		AtomicDouble assocScore = new AtomicDouble(ASSOCIATION_SCORE_DEFINITE);
		ProbabilityAssScore score = new ProbabilityAssScore();
		AssScoreMethod scoreMethod = new AssScoreMethod();
		scoreMethod.setDescription(SCORE_METHOD_DESCRIPTION);
		scoreMethod.setUrl(ASSOCIATIONS_SCORE_METHOD_DESCRIPTION_URL);
		score.setMethod(scoreMethod);
		score.setValue(assocScore.get());
		return score;
	}

	private List<InfectiousDiseaseEFO> efoMappings(UniProtEntry entry, InfectiousDisease disease) {
		if (accession2EfoMapper.get(entry.getPrimaryUniProtAccession().getValue()) == null) {
			return new ArrayList<InfectiousDiseaseEFO>();
		}
		return accession2EfoMapper.get(entry.getPrimaryUniProtAccession().getValue()).stream()
				.filter(efo -> efo.getText().contains(disease.getComment())).collect(Collectors.toList());
	}

}
