package web.service;


import org.springframework.stereotype.Service;
import qa.QA_System;
import qa.evidence.internet.extration.CblockExtration;
import qa.evidence.internet.search.BaiDuSearch;
import qa.segmentation.Jieba;
import qa.segmentation.Segmenter;
import web.model.QAModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QAService {

    private static BaiDuSearch baiDuSearch = new BaiDuSearch(3);
    private static Segmenter segmenter = Jieba.getInstance();
    private static QA_System qa_system = QA_System.getInstance(baiDuSearch, segmenter);

    /**
     * @return 只返回 answer
     */
    @Deprecated
    public String getAnswer(String query) {
        String answer = null;
        List<String> entityList = qa_system.answer(query, CblockExtration.create());
        Map<String, Integer> map = new HashMap<>();
        for (String entity : entityList) {
            map.merge(entity, 1, (a, b) -> a + b);
        }
        int max = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                answer = entry.getKey();
            }
        }
        return answer;
    }

    /**
     * 返回 QAModel bean中存在的所有数据。
     */
    public void getAnswer(QAModel qaModel) {
        String query = qaModel.getQuery();
        if (query == null)
            return;
        List<String> words = new ArrayList<>();
        List<String> flags = new ArrayList<>();
        qa_system.answer(query, CblockExtration.create(), words, flags);
        List<List<QAModel.KVData>> kvDataLists = new ArrayList<>();
        int iFlag = 0;
        List<QAModel.KVData> kvDataList = new ArrayList<>();
        Map<String, Integer> answerMap = new HashMap<>();
        for (String word : words) {
            if ("\n".equals(word)) {
                List<QAModel.KVData> kvDataListSave = new ArrayList<>(kvDataList);
                kvDataLists.add(kvDataListSave);
                kvDataList.clear();
            } else {
                String flagStr = flags.get(iFlag++);
                if ("0;".equals(flagStr)) {
                    Integer count = answerMap.get(word);
                    if (count != null)
                        answerMap.put(word, ++count);
                    else
                        answerMap.put(word, 1);
                }
                kvDataList.add(new QAModel.KVData(word, flagStr));
            }
        }
        qaModel.setEvidences(kvDataLists);
        int maxCount = Integer.MIN_VALUE;
        String answer = "unknown";
        for (Map.Entry<String, Integer> entry : answerMap.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            if (count > maxCount) {
                answer = word;
                maxCount = count;
            }
        }
        qaModel.setAnswer(answer);
    }


    public void getAnswer(String query, String evidence, QAModel qaModel) {
        List<String> words = new ArrayList<>();
        List<String> flags = new ArrayList<>();
        qa_system.answer(query, evidence, words, flags);
        List<List<QAModel.KVData>> kvDataLists = new ArrayList<>();
        int iFlag = 0;
        List<QAModel.KVData> kvDataList = new ArrayList<>();
        Map<String, Integer> answerMap = new HashMap<>();
        for (String word : words) {
            if ("\n".equals(word)) {
                List<QAModel.KVData> kvDataListSave = new ArrayList<>(kvDataList);
                kvDataLists.add(kvDataListSave);
                kvDataList.clear();
            } else {
                String flagStr = flags.get(iFlag++);
                if ("0;".equals(flagStr)) {
                    Integer count = answerMap.get(word);
                    if (count != null)
                        answerMap.put(word, ++count);
                    else
                        answerMap.put(word, 1);
                }
                kvDataList.add(new QAModel.KVData(word, flagStr));
            }
        }
        qaModel.setEvidences(kvDataLists);
        int maxCount = Integer.MIN_VALUE;
        String answer = "unknown";
        for (Map.Entry<String, Integer> entry : answerMap.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            if (count > maxCount) {
                answer = word;
                maxCount = count;
            }
        }
        qaModel.setAnswer(answer);
    }
}
