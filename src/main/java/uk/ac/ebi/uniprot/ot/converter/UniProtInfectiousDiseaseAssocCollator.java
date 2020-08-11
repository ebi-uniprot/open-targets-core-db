package uk.ac.ebi.uniprot.ot.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.model.uniprot.comments.CommentTextImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.FunctionCommentImpl;
import uk.ac.ebi.uniprot.ot.input.UniProtEvSource;
import uk.ac.ebi.uniprot.ot.model.GeneticsRoot;
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
//			List<GeneticsRoot> genRoots = this.baseFactory.createGeneticsRoots(uniProtEntry, disease);

//			removeDuplicates(litRoots, genRoots);

			if (validate) {
				recordValidResults(bases, uniProtEntry, disease, litRoots);
//				recordValidResults(bases, uniProtEntry, disease, genRoots);
			} else {
				recordResultsWithoutValidating(bases, litRoots);
//				recordResultsWithoutValidating(bases, genRoots);
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

	protected boolean recordValidResults(String accession, String disease, Base base) {
		boolean succeeded = false;
		String message = "Invalid literature evidence JSON for: ({}, {})";
		try {
			succeeded = validator.validate(jsonSchema, objectMapper.writeValueAsString(base)).succeeded();
		} catch (JsonProcessingException e) {
			message += " -- problem converting object to JSON";
		}
		if (!succeeded) {
//			LOGGER.warn(message, accession, disease);
			this.conversionReport.getTotalItemsFailed().getAndIncrement();
		}
		return succeeded;
	}

	private List<InfectiousDisease> getInfectiousDiseasesStream(UniProtEntry uniProtEntry) {
		Collection<Comment> comments = uniProtEntry.getComments();
		List<InfectiousDisease> list = new ArrayList<InfectiousDisease>();
		for (Iterator iterator = comments.iterator(); iterator.hasNext();) {
			Comment comment = (Comment) iterator.next();
			if(comment instanceof FunctionCommentImpl) {
				FunctionCommentImpl commentText = (FunctionCommentImpl)comment;
			if (commentText.getValue().contains("Microbial infection")) {
				InfectiousDisease disease = new InfectiousDisease();
				System.out.println("comment.toString(): "+commentText.getValue());
				disease.setComment(commentText.getValue());
				disease.setEvidenceIds(commentText.getEvidenceIds());
				list.add(disease);
			}
			}
		}
		return list;
//		return uniProtEntry.getComments().stream().filter(x -> x.getCommentStatus().getValue().contains("Microbial infection"))
//				.map(d -> (DiseaseCommentStructured) d);
	}

}
