package uk.ac.ebi.uniprot.ot.model.factory;

import static java.util.Collections.singletonList;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.accession;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createDefaultECOsSet;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createLinkOut;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createUniProtDiseaseUrl;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.createUniProtUrl;
import static uk.ac.ebi.uniprot.ot.model.factory.GeneticsRootFactory.SNP_SINGLE;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.createDbSnpUrls;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.createVariant;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.getFunctionalConsequenceURL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.uniprot.ot.model.InfectiousDiseaseEFO;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;
import uk.ac.ebi.uniprot.ot.model.base.UniqueAssociationFields;
import uk.ac.ebi.uniprot.ot.model.bioentity.Variant;
import uk.ac.ebi.uniprot.ot.model.evidence.LinkOut;
import uk.ac.ebi.uniprot.ot.model.evidence.LiteratureCuratedEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.Mutation;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

class InfectiousLiteratureCuratedRootFactory extends LiteratureCuratedRootFactory {
	private static final String INFECTION_LITERATURE = "infection_literature";
	private static final String SOMATIC = "somatic";
	private static final String SOMATIC_MUTATION = "somatic_mutation";
	private final InfectiousDiseaseBaseFactory baseFactory;

	InfectiousLiteratureCuratedRootFactory(InfectiousDiseaseBaseFactory baseFactory) {
		super(baseFactory);
		this.baseFactory = baseFactory;
	}

	LiteratureCuratedRoot createLiteratureCuratedRoot(UniProtEntry uniProtEntry, InfectiousDisease disease,
			List<EvidenceId> pubmedEvidenceIds, InfectiousDiseaseEFO efo, VariantLineInfo vli) {

		LiteratureCuratedRoot lcr = new LiteratureCuratedRoot();
		lcr.setAccess_level(DefaultBaseFactory.ACCESS_LEVEL);
		lcr.setSourceID(DefaultBaseFactory.UNIPROT_SOMATIC);
		lcr.setValidated_against_schema_version(DefaultBaseFactory.CTTV_SCHEMA_VERSION);

		UniqueAssociationFields associationFields = createLiteratureUniqueAssociationFields(uniProtEntry, disease, efo);
		associationFields.setAlleleOrigin(SOMATIC);
		associationFields.setDbSnps(vli.getDbSNPs().stream().collect(Collectors.joining(",")));
		associationFields.setMutationDescription(vli.getMutationTransformation());
		lcr.setUnique_association_fields(associationFields);

		lcr.setType(SOMATIC_MUTATION);
		lcr.setDisease(baseFactory.createDisease(disease, efo));
		lcr.setTarget(baseFactory.createTarget(uniProtEntry));
		lcr.setLiterature(baseFactory
				.createLiteratureProvenanceType(new HashSet<>(baseFactory.extractPubMeds(pubmedEvidenceIds))));

		lcr.setEvidence(createLitEvidence(uniProtEntry, disease, pubmedEvidenceIds, vli));

		return lcr;
	}

	LiteratureCuratedRoot createLiteratureCuratedRoot(UniProtEntry uniProtEntry, InfectiousDisease disease,
			List<EvidenceId> pubmedEvidenceIds, InfectiousDiseaseEFO efoMapping) {

		LiteratureCuratedRoot lcr = new LiteratureCuratedRoot();
		lcr.setAccess_level(DefaultBaseFactory.ACCESS_LEVEL);
		lcr.setSourceID(DefaultBaseFactory.UNIPROT_INFECTIOUS_LITERATURE);
		lcr.setType(INFECTION_LITERATURE);
		lcr.setValidated_against_schema_version(DefaultBaseFactory.CTTV_SCHEMA_VERSION);
		lcr.setUnique_association_fields(createLiteratureUniqueAssociationFields(uniProtEntry, disease, efoMapping));
		lcr.setTarget(baseFactory.createTarget(uniProtEntry));
		lcr.setDisease(baseFactory.createDisease(disease, efoMapping));
		lcr.setEvidence(createLitEvidence(uniProtEntry, disease, pubmedEvidenceIds));

		lcr.setLiterature(baseFactory
				.createLiteratureProvenanceType(new HashSet<>(baseFactory.extractPubMeds(pubmedEvidenceIds))));

		return lcr;
	}

	private UniqueAssociationFields createLiteratureUniqueAssociationFields(UniProtEntry uniProtEntry,
			InfectiousDisease disease, InfectiousDiseaseEFO efo) {
		UniqueAssociationFields uaf = new UniqueAssociationFields();
		uaf.setUniprot_release(baseFactory.getUniProtReleaseVersion());
		uaf.setDisease_uri(efo.getId());
		// TODO
//		uaf.setDisease_acronym(structuredDisease.getDisease().getAcronym().getValue());
		uaf.setTarget(createUniProtUrl(accession(uniProtEntry)));
		return uaf;
	}

	private LiteratureCuratedEvidence createLitEvidence(UniProtEntry uniProtEntry, InfectiousDisease structuredDisease,
			List<EvidenceId> evidenceIds, VariantLineInfo vli) {
		LiteratureCuratedEvidence lce = new LiteratureCuratedEvidence();

		// associated
		lce.setIs_associated(true);

		// known mutations
		Mutation mutation = new Mutation();

		Variant variant = createVariant(vli);
		String preferredName = variant.getType();
		if (variant.getType().equals(SNP_SINGLE)) {
			preferredName = "missense_variant"; // last minute requirement from CK -- inconsistent with other
			// things they want ...
		}
		mutation.setPreferred_name(preferredName);
		mutation.setFunctional_consequence(getFunctionalConsequenceURL(vli));
		lce.setKnown_mutations(singletonList(mutation));

		// urls
		List<LinkOut> urls = singletonList(createLinkOut("Further details in UniProt database", createDbSnpUrls(vli)));
		lce.setUrls(urls);

		// evidences
		Set<String> ecos = createDefaultECOsSet();
		Set<String> pubmeds = new HashSet<>();

		baseFactory.extractEcoAndPubMeds(evidenceIds, ecos, pubmeds);

		lce.setEvidenceCodes(new ArrayList<>(baseFactory.createEcoUrls(ecos)));

		// literature
		if (!pubmeds.isEmpty()) {
			ProvenanceType provenanceType = baseFactory.createProvenanceType(pubmeds);
			lce.setProvenance_type(provenanceType);
		}

		// association score
		lce.setResource_score(baseFactory.createAssociationScore(structuredDisease));

		// date asserted
		lce.setDate_asserted(DefaultBaseFactory.dateString(uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate()));

		return lce;
	}

	private LiteratureCuratedEvidence createLitEvidence(UniProtEntry uniProtEntry, InfectiousDisease structuredDisease,
			Collection<EvidenceId> evidenceIds) {
		LiteratureCuratedEvidence lce = new LiteratureCuratedEvidence();

		// urls
		lce.setUrls(singletonList(
				createLinkOut("Further details in UniProt database", createUniProtDiseaseUrl(uniProtEntry))));

		// evidences
		Set<String> ecos = createDefaultECOsSet();
		Set<String> pubmeds = new HashSet<>();

		baseFactory.extractEcoAndPubMeds(evidenceIds, ecos, pubmeds);

		lce.setEvidenceCodes(new ArrayList<>(baseFactory.createEcoUrls(ecos)));

		// literature
		if (!pubmeds.isEmpty()) {
			ProvenanceType provenanceType = baseFactory.createProvenanceType(pubmeds);
			lce.setProvenance_type(provenanceType);
		}

		// associated
		lce.setIs_associated(true);

		// association score
		lce.setResource_score(baseFactory.createAssociationScore(structuredDisease));

		// date asserted
		lce.setDate_asserted(DefaultBaseFactory.dateString(uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate()));

		return lce;
	}
}
