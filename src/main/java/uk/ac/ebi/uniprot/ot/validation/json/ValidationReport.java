package uk.ac.ebi.uniprot.ot.validation.json;

import java.util.List;

/**
 * Created 23/04/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
public interface ValidationReport {
    boolean succeeded();
    List<String> getMessages();
}
