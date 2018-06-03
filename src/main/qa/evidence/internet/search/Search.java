package qa.evidence.internet.search;

import qa.evidence.internet.extration.Extration;

import java.util.List;

public interface Search {

    /**
     * @param query query about entity.
     * @return a series of evidences.
     */
    List<String> getEvidences(String query);

    /**
     * @param query     query about entity.
     * @param extration extration for extrative the text of the web page
     * @return a series of evidences.
     */
    List<String> getEvidences(String query, Extration extration) throws InterruptedException;

}
