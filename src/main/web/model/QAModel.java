package web.model;

import java.util.List;

public class QAModel {

    public static class KVData{
        private String word;
        private String flag;

        public KVData(String word, String flag) {
            this.word = word;
            this.flag = flag;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }
    }

    private String query;
    private String answer;
    private List<List<KVData>> evidences;

    // for RC
    private String sourceEvidence;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<List<KVData>> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<List<KVData>> evidences) {
        this.evidences = evidences;
    }

    public void setSourceEvidence(String sourceEvidence) {
        this.sourceEvidence = sourceEvidence;
    }

    public String getSourceEvidence() {
        return sourceEvidence;
    }
}
