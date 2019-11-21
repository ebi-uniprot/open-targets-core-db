package uk.ac.ebi.uniprot.ot.input;

/**
 * Interface specifying a single data source unit for creating a list of evidence strings.
 *
 * <p>Currently a UniProtEntry wrapper.
 *
 * @author Edd
 */
public interface EvStringSource<T> {
  T getEvidenceSource();
}
