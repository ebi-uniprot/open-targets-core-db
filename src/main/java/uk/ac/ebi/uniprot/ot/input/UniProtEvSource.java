package uk.ac.ebi.uniprot.ot.input;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

/**
 * Created 06/05/15
 *
 * @author Edd
 */
public class UniProtEvSource implements EvStringSource<UniProtEntry> {
  private UniProtEntry uniProtEntry;

  @Override
  public UniProtEntry getEvidenceSource() {
    return this.uniProtEntry;
  }

  public void setUniProtEntry(UniProtEntry uniProtEntry) {
    this.uniProtEntry = uniProtEntry;
  }
}
