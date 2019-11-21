package uk.ac.ebi.uniprot.ot.validation.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 23/04/15
 *
 * @author Edd <eddturner@ebi.ac.uk>
 */
public class JacksonValidationReport implements ValidationReport {
  private boolean succeeded = false;
  private List<String> messages = new ArrayList<>();

  public void setSucceeded(boolean succeeded) {
    this.succeeded = succeeded;
  }

  public boolean addMessage(String message) {
    return this.messages.add(message);
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  @Override
  public boolean succeeded() {
    return this.succeeded;
  }

  @Override
  public List<String> getMessages() {
    return this.messages;
  }
}
