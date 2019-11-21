package uk.ac.ebi.uniprot.ot.model.factory;

import java.util.List;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;

/**
 * Created 11/05/15
 *
 * @author Edd
 */
public interface BaseFactory {
  List<LiteratureCuratedRoot> createLiteratureCuratedRoot(
      UniProtEntry uniProtEntry, DiseaseCommentStructured structuredDisease);

  List<GeneticsRoot> createGeneticsRoots(
      UniProtEntry uniProtEntry, DiseaseCommentStructured structuredDisease);
}
