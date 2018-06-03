import org.junit.Test;
import qa.QA_System;
import qa.evidence.internet.extration.CblockExtration;
import qa.evidence.internet.search.BaiDuSearch;
import qa.segmentation.Jieba;
import qa.segmentation.Segmenter;

import java.util.List;

public class TestQASystem {

    @Test
    public void testAnswer() {
        BaiDuSearch baiDuSearch = new BaiDuSearch(3);
        Segmenter segmenter = Jieba.getInstance();
        QA_System qa_system = QA_System.getInstance(baiDuSearch, segmenter);
        List<String> answer = qa_system.answer("姚明的妻子是谁？", CblockExtration.create());
        System.out.println(answer);

    }

}
