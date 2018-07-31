package uk.ac.ebi.uniprot.ot.converter;

/**
 * Converts type S to type T
 * @author Edd
 */
@FunctionalInterface
public interface Converter <S, T> {
    T convert(S source);
}
