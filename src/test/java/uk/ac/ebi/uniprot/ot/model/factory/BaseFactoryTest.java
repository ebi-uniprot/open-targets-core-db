package uk.ac.ebi.uniprot.ot.model.factory;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.ASSOCIATION_SCORE_DEFINITE;
import static uk.ac.ebi.uniprot.ot.model.factory.DefaultBaseFactory.ASSOCIATION_SCORE_INDEFINITE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import uk.ac.ebi.kraken.interfaces.uniprot.EvidencedValue;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseNote;
import uk.ac.ebi.kraken.model.uniprot.EvidencedValueImpl;
import uk.ac.ebi.uniprot.ot.MockitoExtension;

/**
 * Created 16/06/17
 *
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class BaseFactoryTest {
  private DefaultBaseFactory baseFactory;

  @Mock private DiseaseCommentStructured mockDiseaseComment;

  @Mock private DiseaseNote mockDiseaseNote;

  @BeforeEach
  void setUp() {
    baseFactory = new DefaultBaseFactory();
  }

  @Test
  void createsCertainAssociationScore() {
    when(mockDiseaseComment.getNote()).thenReturn(mockDiseaseNote);
    EvidencedValueImpl evidencedValue = new EvidencedValueImpl();
    evidencedValue.setValue("Curator information ...");
    List<EvidencedValue> evidenceValues = singletonList(evidencedValue);
    when(mockDiseaseNote.getTexts()).thenReturn(evidenceValues);
    String confidence = baseFactory.createConfidence(mockDiseaseComment);

    assertThat(confidence, is(ASSOCIATION_SCORE_DEFINITE));
  }

  @Test
  void createsUncertainAssociationScore() {
    when(mockDiseaseComment.getNote()).thenReturn(mockDiseaseNote);
    EvidencedValueImpl evidencedValue = new EvidencedValueImpl();
    evidencedValue.setValue(
        "Disease susceptibility may be associated with variations affecting the gene "
            + "represented in this entry. More curator information ...");
    List<EvidencedValue> evidenceValues = singletonList(evidencedValue);
    when(mockDiseaseNote.getTexts()).thenReturn(evidenceValues);
    String confidence = baseFactory.createConfidence(mockDiseaseComment);

    assertThat(confidence, is(ASSOCIATION_SCORE_INDEFINITE));
  }

  @Test
  void creatingAssociationScoreForNullDiseaseSucceeds() {
    String confidence = baseFactory.createConfidence(null);
    assertThat(confidence, is(notNullValue()));
  }
}
