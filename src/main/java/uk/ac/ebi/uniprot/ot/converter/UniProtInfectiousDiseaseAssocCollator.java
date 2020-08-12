package uk.ac.ebi.uniprot.ot.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.model.uniprot.comments.FunctionCommentImpl;
import uk.ac.ebi.uniprot.ot.input.UniProtEvSource;
import uk.ac.ebi.uniprot.ot.model.LiteratureCuratedRoot;
import uk.ac.ebi.uniprot.ot.model.base.Base;
import uk.ac.ebi.uniprot.ot.model.factory.InfectiousDisease;
import uk.ac.ebi.uniprot.ot.model.factory.InfectiousDiseaseBaseFactory;

/**
 * Transforms an evidence string source object into one or more {@link Base}
 * instances.
 *
 * @author Edd
 */
public class UniProtInfectiousDiseaseAssocCollator extends UniProtDiseaseAssocCollator {

	private static final String MICROBIAL_INFECTION = "Microbial infection";
	private InfectiousDiseaseBaseFactory baseFactory;

	@Inject
	public UniProtInfectiousDiseaseAssocCollator(InfectiousDiseaseBaseFactory baseFactory) throws IOException {
		super(baseFactory);
		this.baseFactory = baseFactory;
		this.objectMapper = new ObjectMapper();
		this.conversionReport = new ConversionReport();
		this.conversionReport.setMessage("UniProt -> Disease Association Conversion Report");
	}

	@Override
	public Collection<Base> convert(UniProtEvSource source) {
		Collection<Base> bases = new ArrayList<>();
		UniProtEntry uniProtEntry = source.getEvidenceSource();

		// iterate through diseases of entry and create evidence strings for each
		getInfectiousDiseasesStream(uniProtEntry).forEach(disease -> {
			// create the disease association pojos
			List<LiteratureCuratedRoot> litRoots = this.baseFactory.createLiteratureCuratedRoot(uniProtEntry, disease);

			if (validate) {
				recordValidResults(bases, uniProtEntry, disease, litRoots);
			} else {
				recordResultsWithoutValidating(bases, litRoots);
			}
		});

		return bases;
	}

	protected void recordValidResults(Collection<Base> bases, UniProtEntry uniProtEntry, InfectiousDisease disease,
			List<? extends Base> basesSubset) {
		basesSubset.stream().filter(base -> recordValidResults(uniProtEntry.getPrimaryUniProtAccession().getValue(),
				disease.getComment(), base)).forEach(base -> {
					bases.add(base);
					this.conversionReport.getTotalItemsSucceeded().getAndIncrement();
				});
	}

	private List<InfectiousDisease> getInfectiousDiseasesStream(UniProtEntry uniProtEntry) {
		Collection<Comment> comments = uniProtEntry.getComments();
		List<InfectiousDisease> list = new ArrayList<InfectiousDisease>();
		for (Iterator<Comment> iterator = comments.iterator(); iterator.hasNext();) {
			Comment comment = (Comment) iterator.next();
			if (comment instanceof FunctionCommentImpl) {
				FunctionCommentImpl commentText = (FunctionCommentImpl) comment;
				if (commentText.getValue().contains(MICROBIAL_INFECTION)) {
					InfectiousDisease disease = new InfectiousDisease();
					disease.setComment(commentText.getValue());
					disease.setEvidenceIds(commentText.getEvidenceIds());
					list.add(disease);
				}
			}
		}
		return list;
	}

}
