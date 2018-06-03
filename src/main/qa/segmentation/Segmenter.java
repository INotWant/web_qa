package qa.segmentation;

import java.util.List;

public interface Segmenter {

    /**
     * @param sentence sentence
     * @return the word collection
     */
    List<String> segment(String sentence);

}
