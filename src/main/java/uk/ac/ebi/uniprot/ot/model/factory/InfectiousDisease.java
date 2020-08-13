package uk.ac.ebi.uniprot.ot.model.factory;

import java.util.List;

import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;

public class InfectiousDisease {
  private String comment;
  private List<EvidenceId> evidenceIds;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public List<EvidenceId> getEvidenceIds() {
    return evidenceIds;
  }

  public void setEvidenceIds(List<EvidenceId> evidenceIds) {
    this.evidenceIds = evidenceIds;
  }
}
