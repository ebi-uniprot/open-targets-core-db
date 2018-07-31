package uk.ac.ebi.uniprot.ot.converter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created 22/05/15
 * @author Edd
 */
public class ConversionReport {
    private String message;
    private AtomicInteger totalItemsSucceeded = new AtomicInteger(0);
    private AtomicInteger totalItemsFailed = new AtomicInteger(0);;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AtomicInteger getTotalItemsSucceeded() {
        return totalItemsSucceeded;
    }

    public void setTotalItemsSucceeded(AtomicInteger totalItemsSucceeded) {
        this.totalItemsSucceeded = totalItemsSucceeded;
    }

    public AtomicInteger getTotalItemsFailed() {
        return totalItemsFailed;
    }

    public void setTotalItemsFailed(AtomicInteger totalItemsFailed) {
        this.totalItemsFailed = totalItemsFailed;
    }

    @Override public String toString() {
        return "ConversionReport{" +
                "message='" + message + '\'' +
                ", totalItemsSucceeded=" + totalItemsSucceeded +
                ", totalItemsFailed=" + totalItemsFailed +
                '}';
    }
}
