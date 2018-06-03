package qa.segmentation;

import com.huaban.analysis.jieba.JiebaSegmenter;
import qa.label.SafeType;
import qa.label.ThreadSafe;

import java.util.List;

@ThreadSafe(SafeType.STACK)
public class Jieba implements Segmenter {

    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    private static Jieba jieba = new Jieba();

    private Jieba() {
    }

    public static Jieba getInstance() {
        return jieba;
    }

    @Override
    public List<String> segment(String sentence) {
        return segmenter.sentenceProcess(sentence);
    }
}
