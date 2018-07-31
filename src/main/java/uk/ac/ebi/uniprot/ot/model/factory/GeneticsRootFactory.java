package uk.ac.ebi.uniprot.ot.model.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.base.UniqueAssociationFields;
import uk.ac.ebi.uniprot.ot.model.evidence.Gene2VariantEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.GeneticsEvidence;
import uk.ac.ebi.uniprot.ot.model.evidence.LinkOut;
import uk.ac.ebi.uniprot.ot.model.evidence.Variant2DiseaseEvidence;
import uk.ac.ebi.uniprot.ot.model.provenance.ProvenanceType;
import uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.*;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.createVariant;
import static uk.ac.ebi.uniprot.ot.model.variant.VariantLineInfo.getFunctionalConsequenceURL;

class GeneticsRootFactory {
    static final String SNP_SINGLE = "snp single";
    static final String STRUCTURAL_VARIANT = "structural variant";
    static final String SNP_MULTIPLE = "snp multiple";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneticsRootFactory.class);
    private static final String SOMATIC_MUTATION = "somatic_mutation";
    private static final String GERMLINE = "germline";
    private final DefaultBaseFactory baseFactory;

    GeneticsRootFactory(DefaultBaseFactory baseFactory) {
        this.baseFactory = baseFactory;
    }

    GeneticsRoot createGeneticsRoot(UniProtEntry uniProtEntry,
            DiseaseCommentStructured structuredDisease,
            List<EvidenceId> pubmedEvIds,
            String efo,
            Feature variantFeature,
            VariantFeature variant,
            VariantLineInfo vli) {

        GeneticsRoot gr = new GeneticsRoot();
        gr.setAccess_level(DefaultBaseFactory.ACCESS_LEVEL);
        gr.setSourceID(DefaultBaseFactory.UNIPROT);
        gr.setValidated_against_schema_version(DefaultBaseFactory.CTTV_SCHEMA_VERSION);
        gr.setUnique_association_fields(
                createGeneticsUniqueAssociationFields(uniProtEntry, structuredDisease, variantFeature, efo, vli));
        gr.setDisease(baseFactory.createDisease(structuredDisease, efo));
        gr.setTarget(baseFactory.createTarget(uniProtEntry));
        if (vli.getDbSNPs() != null) {
            gr.setVariant(createVariant(vli));
        }

        gr.setEvidence(createGeneticsEvidence(uniProtEntry, structuredDisease, pubmedEvIds, variant, vli));

        insertSomaticInfo(gr, uniProtEntry, efo, vli);

        return gr;
    }

    private void insertSomaticInfo(
            GeneticsRoot gr,
            UniProtEntry uniProtEntry,
            String efo,
            VariantLineInfo vli) {
        String accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
        List<String> dbSNPs = vli.getDbSNPs();

        if (dbSNPs.size() == 1) {
            boolean isSomatic = baseFactory.getSomaticDbSNPCache().isSomatic(accession, efo, dbSNPs.get(0));

            if (isSomatic) {
                gr.setType(SOMATIC_MUTATION);
                gr.getUnique_association_fields().setAlleleOrigin(SOMATIC_MUTATION);
            } else {
                gr.getUnique_association_fields().setAlleleOrigin(GERMLINE);
            }
        }
    }

    private UniqueAssociationFields createGeneticsUniqueAssociationFields(
            UniProtEntry uniProtEntry,
            DiseaseCommentStructured structuredDisease,
            Feature variantFeature,
            String efo,
            VariantLineInfo vli) {
        String accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
        List<String> dbSNPs = vli.getDbSNPs();

        UniqueAssociationFields uaf = new UniqueAssociationFields();
//        if (dbSNPs.size() == 1) {
//            boolean isSomatic = baseFactory.getSomaticDbSNPCache().isSomatic(accession, efo, dbSNPs.get(0));
//            uaf.setAlleleOrigin(isSomatic ? "somatic_mutation" : "germline");
//        }

        uaf.setTarget(accession);
        uaf.setDisease_acronym(structuredDisease.getDisease().getAcronym().getValue());
        uaf.setUniprot_release(baseFactory.getUniProtReleaseVersion());
        uaf.setDisease_uri(efo);
        uaf.setDbSnps(dbSNPs
                .stream()
                .collect(Collectors.joining(", ")));
        uaf.setVariant_id(((VariantFeature) variantFeature).getFeatureId().getValue());
        return uaf;
    }
//
//    public static Variant createVariant(VariantLineInfo variantLineInfo) {
//        Variant modelVariant = new Variant();
//        modelVariant.setId(createDbSnpUrls(variantLineInfo));
//        switch (variantLineInfo.getType()) {
//            case MISSENSE:
//                modelVariant.setType(SNP_SINGLE);
//                break;
//            case DELETION:
//                modelVariant.setType(STRUCTURAL_VARIANT);
//                break;
//            case INS_DEL:
//                modelVariant.setType(SNP_MULTIPLE);
//                break;
//            default:
//                LOGGER.warn("Unknown variant type: " + variantLineInfo.getType().name());
//        }
//        return modelVariant;
//    }
//
//    public static String createDbSnpUrls(VariantLineInfo variantLineInfo) {
//        List<String> dbSNPs = variantLineInfo.getDbSNPs()
//                .stream()
//                .map(dbsnp -> String.format(DefaultBaseFactory.DB_SNP_URI_FORMAT, dbsnp))
//                .collect(Collectors.toList());
//
//        if (dbSNPs.isEmpty() || dbSNPs.size() > 1) {
//            LOGGER.warn("Expected variant line information to contain only 1 dbsnp: {}", dbSNPs);
//        }
//        return dbSNPs.get(0);
//    }

    private GeneticsEvidence createGeneticsEvidence(UniProtEntry uniProtEntry,
            DiseaseCommentStructured structuredDisease,
            List<EvidenceId> pubmedEvIds,
            VariantFeature variant,
            VariantLineInfo vli) {
        GeneticsEvidence ge = new GeneticsEvidence();

        // gene to variant
        ge.setGene2variant(createGene2VariantEvidence(uniProtEntry, variant, vli));

        // variant to disease
        ge.setVariant2disease(createVariant2DiseaseEvidence(uniProtEntry, structuredDisease, pubmedEvIds));

        return ge;
    }

    private Gene2VariantEvidence createGene2VariantEvidence(
            UniProtEntry uniProtEntry,
            VariantFeature variant,
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
        g2ve.setUrls(singletonList(createLinkOut(
                "Further details in UniProt database",
                createUniProtDiseaseUrl(uniProtEntry))));

        return g2ve;
    }

    private Variant2DiseaseEvidence createVariant2DiseaseEvidence(
            UniProtEntry uniProtEntry,
            DiseaseCommentStructured structuredDisease,
            List<EvidenceId> pubmedEvIds) {
        Variant2DiseaseEvidence v2de = new Variant2DiseaseEvidence();

        // association score
        v2de.setResource_score(baseFactory.createAssociationScore(structuredDisease));

        // is associated
        v2de.setIs_associated(true);

        // date asserted
        v2de.setDate_asserted(
                DefaultBaseFactory.dateString(uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate()));

        // evidence codes
        v2de.setEvidence_codes(singletonList(DefaultBaseFactory.CTTV_FAVOURED_CURATED_EVIDENCE_ECO));

        // provenance
        Set<String> pubmeds = pubmedEvIds.stream()
                .map(evidenceId -> evidenceId.getAttribute().getValue())
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
        linkouts.add(createLinkOut("Further details in UniProt database",
                createUniProtDiseaseUrl(uniProtEntry)));
        pubmeds.stream()
                .map(this::createLinkOutForPubMed)
                .forEach(linkouts::add);
        v2de.setUrls(linkouts);

        return v2de;
    }

    private LinkOut createLinkOutForPubMed(String pubmed) {
        return createLinkOut(
                DefaultBaseFactory.LINK_OUT_NICE_NAME_PUBLISHED_REFERENCE,
                DefaultBaseFactory.createPubMedUrl(pubmed));
    }
}