package uk.ac.ebi.uniprot.ot.mapper;

import java.util.Set;

/**
 * Interface for mapping OMIM ids to EFO. In fact, EFO covers Orphanet mapping too, so OMIM to
 * Orphanet ID mappings suffice the OMIM to EFO criteria.
 *
 * <p>Background: OMIM => Online Mendelian Inheritance in Man EFO => Experimental Factor Ontology
 * Orphanet => The portal for rare diseases and orphan drugs
 *
 * <p>In terms of diseases, OMIM focuses on disease information at the the loci level, i.e., at
 * specific locations on a chromosome. Orphanet, on the other hand, contains loci information at the
 * disease level (basically, they're different views that talk about the same thing). One subtle
 * difference between the two might be that, in the case of irritable bowel disease, of which there
 * are two forms: Crones' disease; and ulcerative colitis. OMIM might only have 1 ID associated with
 * IBD, because the loci for both diseases are similar, whereas Orphanet might contain two separate
 * disease ids distinguishing them.
 *
 * @author Edd
 */
public interface Omim2EfoMapper {
  Set<String> omim2Efo(String omim);
}
