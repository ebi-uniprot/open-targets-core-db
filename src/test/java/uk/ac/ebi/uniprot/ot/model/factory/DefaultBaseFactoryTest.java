package uk.ac.ebi.uniprot.ot.model.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseId;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseReference;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseReferenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseReferenceType;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.model.common.SequenceImpl;
import uk.ac.ebi.kraken.model.uniprot.NcbiTaxonomyIdImpl;
import uk.ac.ebi.kraken.model.uniprot.UniProtEntryImpl;
import uk.ac.ebi.kraken.model.uniprot.UniProtIdImpl;
import uk.ac.ebi.kraken.model.uniprot.accessions.PrimaryUniProtAccessionImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseAcronymImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseCommentStructuredImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseDescriptionImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseIdImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseReferenceIdImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseReferenceImpl;
import uk.ac.ebi.kraken.model.uniprot.evidences.EvidenceIdImpl;
import uk.ac.ebi.kraken.model.uniprot.features.FeatureImpl;
import uk.ac.ebi.kraken.model.uniprot.features.FeatureLocationImpl;
import uk.ac.ebi.kraken.model.uniprot.features.FeatureSequenceImpl;
import uk.ac.ebi.kraken.model.uniprot.features.VariantFeatureImpl;
import uk.ac.ebi.kraken.model.uniprot.features.VariantReportImpl;
import uk.ac.ebi.uniprot.ot.mapper.FFOmim2EfoMapper;
import uk.ac.ebi.uniprot.ot.mapper.SomaticDbSNPMapper;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.base.Base;

public class DefaultBaseFactoryTest {

	private static final String EFO_MAPPINGS = "src/test/resources/mappings/omim2efo.mappings";
	private static final String SOMATIC_MAPPINGS = "src/test/resources/mappings/somatic_census.txt";

	@Test
	void testCreateLiteratureCuratedRoot() {
		UniProtEntryImpl entry = createEntry("P61981", "1433G_HUMAN", 9606);
		Feature feature1 = createFeature(FeatureType.ACT_SITE, "", 15, 15, "test description", "V", "I");
		Feature feature2 = createFeature(FeatureType.BINDING, "dfa", 4, 5, "", "", "");
		String description = "in dbSNP:rs1554616630";
		Feature feature3 = createVariantFeature(description, 15, 15, "T", "M");

		List<Feature> features = Arrays.asList(feature1, feature2, feature3);
		entry.setFeatures(features);

		EvidenceIdImpl evidenceIdImpl = new EvidenceIdImpl();
		evidenceIdImpl.getAttribute().setValue("25533962");
		evidenceIdImpl.setValue("ECO:0000269|PubMed:25533962");

		EvidenceIdImpl evidenceIdImpl1 = new EvidenceIdImpl();
		evidenceIdImpl1.getAttribute().setValue("26168268");
		evidenceIdImpl1.setValue("ECO:0000269|PubMed:26168268");

		List<EvidenceId> evidenceList = new ArrayList<>();
		evidenceList.add(evidenceIdImpl);
		evidenceList.add(evidenceIdImpl1);
		String name = "Mental retardation, autosomal dominant 35";
		String disDesc = "A form of mental retardation, a disorder characterized by significantly "
				+ "below average general intellectual functioning associated with impairments "
				+ "in adaptive behavior and manifested during the developmental period.";
		DiseaseCommentStructuredImpl structuredDisease = createDiseaseComments("EIEE56", name, disDesc, evidenceList,
				"616355");

		DefaultBaseFactory defaultBaseFactory = new DefaultBaseFactory();
		FFOmim2EfoMapper mapper = new FFOmim2EfoMapper(new File(EFO_MAPPINGS));
		defaultBaseFactory.setOmim2EfoMapper(mapper);
		List<Base> bases = defaultBaseFactory.createLiteratureCuratedRoot(entry, structuredDisease);
		assertEquals(1, bases.size());
		assertEquals("OMIM:616355", bases.get(0).getDiseaseFromSourceId());
		assertEquals("P61981", bases.get(0).getTargetFromSourceId());
		assertEquals("Orphanet_178469", bases.get(0).getDiseaseFromSourceMappedId());
		assertEquals("Mental retardation, autosomal dominant 35", bases.get(0).getDiseaseFromSource());
		assertEquals(2, bases.get(0).getLiterature().size());
		assertTrue(bases.get(0).getLiterature().contains("26168268"));
		assertTrue(bases.get(0).getLiterature().contains("25533962"));
	}

	@Test
	void testCreateGeneticsRoots() {
		EvidenceIdImpl evidenceIdImpl = new EvidenceIdImpl();
		evidenceIdImpl.getAttribute().setValue("25533962");
		evidenceIdImpl.setValue("ECO:0000269|PubMed:25533962");

		EvidenceIdImpl evidenceIdImpl1 = new EvidenceIdImpl();
		evidenceIdImpl1.getAttribute().setValue("26168268");
		evidenceIdImpl1.setValue("ECO:0000269|PubMed:26168268");

		List<EvidenceId> evidenceList = new ArrayList<>();
		evidenceList.add(evidenceIdImpl);
		evidenceList.add(evidenceIdImpl1);

		UniProtEntryImpl entry = createEntry("P04637", "1433G_HUMAN", 9606);
		Feature feature1 = createFeature(FeatureType.ACT_SITE, "", 15, 15, "test description", "V", "I");
		Feature feature2 = createFeature(FeatureType.BINDING, "dfa", 4, 5, "", "", "");
		String description = "in EIEE56; associated with hyperlipoproteinemia and atherosclerosis; increased binding to LDL receptor; dbSNP:rs104886003";
		Feature feature3 = createVariantFeature(description, 15, 15, "T", "M");
		feature3.setEvidenceIds(evidenceList);

		VariantFeatureImpl feature4 = createVariantFeature(description, 201, 201, "P", "R");
		List<Feature> features = Arrays.asList(feature1, feature2, feature3, feature4);

		entry.setFeatures(features);

		String name = "Mental retardation, autosomal dominant 35";
		String disDesc = "A form of mental retardation, a disorder characterized by significantly "
				+ "below average general intellectual functioning associated with impairments "
				+ "in adaptive behavior and manifested during the developmental period.";
		DiseaseCommentStructuredImpl structuredDisease = createDiseaseComments("EIEE56", name, disDesc, evidenceList,
				"616355");

		DefaultBaseFactory defaultBaseFactory = new DefaultBaseFactory();
		FFOmim2EfoMapper mapper = new FFOmim2EfoMapper(new File(EFO_MAPPINGS));
		defaultBaseFactory.setOmim2EfoMapper(mapper);
		SomaticDbSNPMapper somaticDbSNPCache = new SomaticDbSNPMapper(new File(SOMATIC_MAPPINGS));
		defaultBaseFactory.setSomaticDbSNPMapper(somaticDbSNPCache);
		List<GeneticsRoot> bases = defaultBaseFactory.createGeneticsRoots(entry, structuredDisease);
		assertEquals(1, bases.size());
		assertEquals("OMIM:616355", bases.get(0).getDiseaseFromSourceId());
		assertEquals("P04637", bases.get(0).getTargetFromSourceId());
		assertEquals("Orphanet_178469", bases.get(0).getDiseaseFromSourceMappedId());
		assertEquals("Mental retardation, autosomal dominant 35", bases.get(0).getDiseaseFromSource());
		assertEquals(2, bases.get(0).getLiterature().size());
		assertTrue(bases.get(0).getLiterature().contains("26168268"));
		assertTrue(bases.get(0).getLiterature().contains("25533962"));
	}
	
	@Test
	void testCreateGeneticsRootsWith2VariantFeatures() {
		EvidenceIdImpl evidenceIdImpl = new EvidenceIdImpl();
		evidenceIdImpl.getAttribute().setValue("25533962");
		evidenceIdImpl.setValue("ECO:0000269|PubMed:25533962");

		EvidenceIdImpl evidenceIdImpl1 = new EvidenceIdImpl();
		evidenceIdImpl1.getAttribute().setValue("26168268");
		evidenceIdImpl1.setValue("ECO:0000269|PubMed:26168268");

		List<EvidenceId> evidenceList = new ArrayList<>();
		evidenceList.add(evidenceIdImpl);
		evidenceList.add(evidenceIdImpl1);

		UniProtEntryImpl entry = createEntry("Q9UM73", "1433G_HUMAN", 9606);
		Feature feature1 = createFeature(FeatureType.ACT_SITE, "", 15, 15, "test description", "V", "I");
		Feature feature2 = createFeature(FeatureType.BINDING, "dfa", 4, 5, "", "", "");
		String description = "in EIEE56; associated with hyperlipoproteinemia and atherosclerosis; increased binding to LDL receptor; dbSNP:rs113994087";
		Feature feature3 = createVariantFeature(description, 15, 15, "T", "M");
		feature3.setEvidenceIds(evidenceList);

		description = "in EIEE56; ApoE4 Philadelphia, ApoE5 French-\n" + 
				" Canadian and ApoE5-type; only ApoE4 Philadelphia is\n" + 
				" associated with HLPP3; dbSNP:rs113994088";

		VariantFeatureImpl feature4 = createVariantFeature(description, 201, 201, "P", "R");
		feature4.setEvidenceIds(evidenceList);
		List<Feature> features = Arrays.asList(feature1, feature2, feature3, feature4);

		entry.setFeatures(features);

		String name = "Mental retardation, autosomal dominant 35";
		String disDesc = "A form of mental retardation, a disorder characterized by significantly "
				+ "below average general intellectual functioning associated with impairments "
				+ "in adaptive behavior and manifested during the developmental period.";
		DiseaseCommentStructuredImpl structuredDisease = createDiseaseComments("EIEE56", name, disDesc, evidenceList,
				"616355");

		DefaultBaseFactory defaultBaseFactory = new DefaultBaseFactory();
		FFOmim2EfoMapper mapper = new FFOmim2EfoMapper(new File(EFO_MAPPINGS));
		defaultBaseFactory.setOmim2EfoMapper(mapper);
		SomaticDbSNPMapper somaticDbSNPCache = new SomaticDbSNPMapper(new File(SOMATIC_MAPPINGS));
		defaultBaseFactory.setSomaticDbSNPMapper(somaticDbSNPCache);
		List<GeneticsRoot> bases = defaultBaseFactory.createGeneticsRoots(entry, structuredDisease);
		assertEquals(2, bases.size());
		assertEquals("OMIM:616355", bases.get(0).getDiseaseFromSourceId());
		assertEquals("Q9UM73", bases.get(0).getTargetFromSourceId());
		assertEquals("Orphanet_178469", bases.get(0).getDiseaseFromSourceMappedId());
		assertEquals("Mental retardation, autosomal dominant 35", bases.get(0).getDiseaseFromSource());
		assertEquals(2, bases.get(0).getLiterature().size());
		assertTrue(bases.get(0).getLiterature().contains("26168268"));
		assertTrue(bases.get(0).getLiterature().contains("25533962"));
		assertEquals("rs113994087", bases.get(0).getVariantRsId());
		
		assertEquals("OMIM:616355", bases.get(1).getDiseaseFromSourceId());
		assertEquals("Q9UM73", bases.get(1).getTargetFromSourceId());
		assertEquals("Orphanet_178469", bases.get(1).getDiseaseFromSourceMappedId());
		assertEquals("Mental retardation, autosomal dominant 35", bases.get(1).getDiseaseFromSource());
		assertEquals(2, bases.get(1).getLiterature().size());
		assertTrue(bases.get(1).getLiterature().contains("26168268"));
		assertTrue(bases.get(1).getLiterature().contains("25533962"));
		assertEquals("rs113994088", bases.get(1).getVariantRsId());
	}
	
	private FeatureImpl createFeature(FeatureType type, String description, int segStart, int segEnd, String desc,
			String alt, String origSeq) {
		FeatureImpl featureImpl = new FeatureImpl() {
			@Override
			public uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType getType() {
				return type;
			}
		};
		FeatureLocation location = new FeatureLocationImpl();
		location.setStart(15);
		location.setEnd(15);
		location.setSequence("");
		featureImpl.setFeatureLocation(location);
		return featureImpl;
	}

	private VariantFeatureImpl createVariantFeature(String description, int segStart, int segEnd, String origSeq,
			String alt) {
		VariantFeatureImpl feature = new VariantFeatureImpl();
		FeatureSequenceImpl alternativeSeq = new FeatureSequenceImpl();
		if (alt != null)
			alternativeSeq.setValue(alt);
		FeatureSequenceImpl originalSeq = new FeatureSequenceImpl();
		if (origSeq != null)
			originalSeq.setValue(origSeq);
		feature.setAlternativeSequences(Arrays.asList(alternativeSeq));
		feature.setOriginalSequence(originalSeq);
		VariantReportImpl report = new VariantReportImpl();
		report.setValue(description);
		feature.setVariantReports(Arrays.asList(report));
		FeatureLocation location = new FeatureLocationImpl();
		location.setStart(segStart);
		location.setEnd(segEnd);
		feature.setFeatureLocation(location);
		return feature;
	}

	private UniProtEntryImpl createEntry(String accession, String name, Integer taxId) {
		UniProtEntryImpl entry = new UniProtEntryImpl();
		PrimaryUniProtAccessionImpl uniProtAccession = new PrimaryUniProtAccessionImpl();
		uniProtAccession.setValue(accession);
		entry.setPrimaryUniProtAccession(uniProtAccession);
		NcbiTaxonomyIdImpl ncbiTaxonomyId = new NcbiTaxonomyIdImpl();
		ncbiTaxonomyId.setId(taxId);
		ncbiTaxonomyId.setValue(taxId.toString());
		entry.setNcbiTaxonomyIds(Arrays.asList(ncbiTaxonomyId));
		UniProtIdImpl uniProtId = new UniProtIdImpl();
		uniProtId.setValue(name);
		entry.setUniProtId(uniProtId);
		SequenceImpl sequence = new SequenceImpl();
//		sequence.setValue(createSequence().getSequence());
		entry.setSequence(sequence);
		return entry;
	}

	private DiseaseCommentStructuredImpl createDiseaseComments(String acronymVal, String name, String desc,
			List<EvidenceId> evidenceList, String mimId) {
		DiseaseCommentStructuredImpl comment = new DiseaseCommentStructuredImpl();
		Disease disease = new DiseaseImpl();
		DiseaseAcronymImpl acronym = new DiseaseAcronymImpl();
		acronym.setValue(acronymVal);
		disease.setDiseaseAcronym(acronym);
		DiseaseId diseaseId = new DiseaseIdImpl();
		diseaseId.setValue(name);
		disease.setDiseaseId(diseaseId);
		DiseaseDescription diseaseDesc = new DiseaseDescriptionImpl();
		diseaseDesc.setValue(desc);
		diseaseDesc.setEvidenceIds(evidenceList);
		disease.setDiseaseDescription(diseaseDesc);
		DiseaseReference ref = new DiseaseReferenceImpl();
		DiseaseReferenceId refId = new DiseaseReferenceIdImpl();
		refId.setValue(mimId);
		ref.setDiseaseReferenceId(refId);
		ref.setDiseaseReferenceType(DiseaseReferenceType.MIM);
		disease.setDiseaseReference(ref);
		comment.setDisease(disease);
		return comment;
	}

}
