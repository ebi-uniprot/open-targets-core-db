package uk.ac.ebi.uniprot.ot.model.factory;

import static java.util.Collections.singletonList;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createDefaultECOsSet;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createLinkOut;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createUniProtDiseaseUrl;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.createVariant;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.getFunctionalConsequenceURL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.InfectiousDiseaseEFO;
import uk.ac.ebi.uniprot.ot.model.base.UniqueAssociationFields;
import uk.ac.ebi.uniprot.ot.model.evidence.Gene2VariantEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.GeneticsEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.LinkOut;
import uk.ac.ebi.uniprot.ot.model.evidence.Variant2DiseaseEvidence;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

class InfectiousDiseaseGeneticsRootFactory extends GeneticsRootFactory {
	static final String SNP_SINGLE = "snp single";
	static final String STRUCTURAL_VARIANT = "structural variant";
	static final String SNP_MULTIPLE = "snp multiple";
	private static final Logger LOGGER = LoggerFactory.getLogger(InfectiousDiseaseGeneticsRootFactory.class);
	private static final String SOMATIC_MUTATION = "somatic_mutation";
	private static final String GERMLINE = "germline";
	private final InfectiousDiseaseBaseFactory baseFactory;

	InfectiousDiseaseGeneticsRootFactory(InfectiousDiseaseBaseFactory baseFactory) {
		super(baseFactory);
		this.baseFactory = baseFactory;
	}

	GeneticsRoot createGeneticsRoot(UniProtEntry uniProtEntry, InfectiousDisease disease,
			List<EvidenceId> pubmedEvIds, InfectiousDiseaseEFO efo, Feature variantFeature, VariantFeature variant,
			VariantLineInfo vli) {

		GeneticsRoot gr = new GeneticsRoot();
		gr.setAccess_level(DefaultBaseFactory.ACCESS_LEVEL);
		gr.setSourceID(DefaultBaseFactory.UNIPROT);
		gr.setValidated_against_schema_version(DefaultBaseFactory.CTTV_SCHEMA_VERSION);
		gr.setUnique_association_fields(
				createGeneticsUniqueAssociationFields(uniProtEntry, disease, variantFeature, efo, vli));
		gr.setDisease(baseFactory.createDisease(disease, efo));
		gr.setTarget(baseFactory.createTarget(uniProtEntry));
		if (vli.getDbSNPs() != null) {
			gr.setVariant(createVariant(vli));
		}

		gr.setEvidence(createGeneticsEvidence(uniProtEntry, disease, pubmedEvIds, variant, vli));

		insertSomaticInfo(gr, uniProtEntry, efo, vli);

		return gr;
	}

	private void insertSomaticInfo(GeneticsRoot gr, UniProtEntry uniProtEntry, InfectiousDiseaseEFO efo, VariantLineInfo vli) {
		String accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
		List<String> dbSNPs = vli.getDbSNPs();

		if (dbSNPs.size() == 1) {
			boolean isSomatic = baseFactory.getSomaticDbSNPCache().isSomatic(accession, efo.getId(), dbSNPs.get(0));

			if (isSomatic) {
				gr.setType(SOMATIC_MUTATION);
				gr.getUnique_association_fields().setAlleleOrigin(SOMATIC_MUTATION);
			} else {
				gr.getUnique_association_fields().setAlleleOrigin(GERMLINE);
			}
		}
	}

	private UniqueAssociationFields createGeneticsUniqueAssociationFields(UniProtEntry uniProtEntry,
			InfectiousDisease disease, Feature variantFeature, InfectiousDiseaseEFO efo, VariantLineInfo vli) {
		String accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
		List<String> dbSNPs = vli.getDbSNPs();

		UniqueAssociationFields uaf = new UniqueAssociationFields();
		// if (dbSNPs.size() == 1) {
		// boolean isSomatic = baseFactory.getSomaticDbSNPCache().isSomatic(accession,
		// efo,
		// dbSNPs.get(0));
		// uaf.setAlleleOrigin(isSomatic ? "somatic_mutation" : "germline");
		// }

		uaf.setTarget(accession);
		uaf.setDisease_acronym(efo.getName());
		uaf.setUniprot_release(baseFactory.getUniProtReleaseVersion());
		uaf.setDisease_uri(efo.getId());
		uaf.setDbSnps(dbSNPs.stream().collect(Collectors.joining(", ")));
		uaf.setVariant_id(((VariantFeature) variantFeature).getFeatureId().getValue());
		return uaf;
	}

	private GeneticsEvidence createGeneticsEvidence(UniProtEntry uniProtEntry,
			InfectiousDisease disease, List<EvidenceId> pubmedEvIds, VariantFeature variant,
			VariantLineInfo vli) {
		GeneticsEvidence ge = new GeneticsEvidence();

		// gene to variant
		ge.setGene2variant(createGene2VariantEvidence(uniProtEntry, variant, vli));

		// variant to disease
		ge.setVariant2disease(createVariant2DiseaseEvidence(uniProtEntry, disease, pubmedEvIds));

		return ge;
	}

	private Gene2VariantEvidence createGene2VariantEvidence(UniProtEntry uniProtEntry, VariantFeature variant,
			VariantLineInfo vli) {
		Gene2VariantEvidence g2ve = new Gene2VariantEvidence();

		// date asserted
		g2ve.setDate_asserted(
				DefaultBaseFactory.dateString(uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate()));

		// associated
		g2ve.setIs_associated(true);

		// evidence codes
		Set<String> ecos = createDefaultECOsSet();
		Set<String> pubmeds = new HashSet<>();

		baseFactory.extractEcoAndPubMeds(variant.getEvidenceIds(), ecos, pubmeds);

		g2ve.setEvidenceCodes(new ArrayList<>(baseFactory.createEcoUrls(ecos)));

		// provenance
		if (!pubmeds.isEmpty()) {
			ProvenanceType provenanceType = baseFactory.createProvenanceType(pubmeds);
			g2ve.setProvenance_type(provenanceType);
		}

		// functional consequences
		g2ve.setFunctional_consequence(getFunctionalConsequenceURL(vli));

		// urls
		g2ve.setUrls(singletonList(
				createLinkOut("Further details in UniProt database", createUniProtDiseaseUrl(uniProtEntry))));

		return g2ve;
	}

	private Variant2DiseaseEvidence createVariant2DiseaseEvidence(UniProtEntry uniProtEntry,
			InfectiousDisease disease, List<EvidenceId> pubmedEvIds) {
		Variant2DiseaseEvidence v2de = new Variant2DiseaseEvidence();

		// association score
		v2de.setResource_score(baseFactory.createAssociationScore(disease));

		// is associated
		v2de.setIs_associated(true);

		// date asserted
		v2de.setDate_asserted(
				DefaultBaseFactory.dateString(uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate()));

		// evidence codes
		v2de.setEvidence_codes(singletonList(DefaultBaseFactory.CTTV_FAVOURED_CURATED_EVIDENCE_ECO));

		// provenance
		Set<String> pubmeds = pubmedEvIds.stream().map(evidenceId -> evidenceId.getAttribute().getValue())
				.collect(Collectors.toSet());

		if (!pubmeds.isEmpty()) {
			ProvenanceType provenanceType = baseFactory.createProvenanceType(pubmeds);
			v2de.setProvenance_type(provenanceType);
		}

		// unique experiment reference (refer to latest publication)
		v2de.setUniqueExperimentReference(
				DefaultBaseFactory.createPubMedUrl(DefaultBaseFactory.latestPubMed(uniProtEntry, pubmeds)));

		// urls to contain links to all the publications
		List<LinkOut> linkouts = new ArrayList<>();
		linkouts.add(createLinkOut("Further details in UniProt database", createUniProtDiseaseUrl(uniProtEntry)));
		pubmeds.stream().map(this::createLinkOutForPubMed).forEach(linkouts::add);
		v2de.setUrls(linkouts);

		return v2de;
	}

	private LinkOut createLinkOutForPubMed(String pubmed) {
		return createLinkOut(DefaultBaseFactory.LINK_OUT_NICE_NAME_PUBLISHED_REFERENCE,
				DefaultBaseFactory.createPubMedUrl(pubmed));
	}
}
