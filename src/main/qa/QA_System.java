package qa;

import qa.conf.Configuration;
import qa.evidence.internet.extration.Extration;
import qa.evidence.internet.search.Search;
import qa.segmentation.Segmenter;
import qa.util.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QA_System {

    private Search search;
    private Segmenter segmenter;

    private static QA_System qa_system;

    private QA_System(Search search, Segmenter segmenter) {
        this.search = search;
        this.segmenter = segmenter;
    }

    public synchronized static QA_System getInstance(Search search, Segmenter segmenter) {
        if (qa_system == null)
            qa_system = new QA_System(search, segmenter);
        return qa_system;
    }

    //*
    @Deprecated
    public List<String> answer(String query, Extration extration) {
        // 对每次查询分配不同的文章
        String uuid = UUID.randomUUID().toString().replaceAll("-", "_");
        String qeTextFile = Configuration.QE_TEXT_PATH.replaceAll("qe_text", "qe_" + uuid);

        List<String> entityList = new ArrayList<>();
        // 1. initialize qe_text
        List<String> totalEWords = new ArrayList<>();
        try (BufferedWriter bfWriter = new BufferedWriter(new FileWriter(qeTextFile))) {
            List<String> qWords = segmenter.segment(query);
            bfWriter.write(new String((listToStr(qWords) + "\n").getBytes(), "UTF-8"));
            List<String> evidences;
            if (extration == null)
                evidences = search.getEvidences(query);
            else
                evidences = search.getEvidences(query, extration);

            for (String evidence : evidences) {
                List<String> eWords = segmenter.segment(evidence);
                totalEWords.addAll(eWords);
                bfWriter.write(new String((listToStr(eWords) + "\n").getBytes(), "UTF-8"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 2. exec qa.sh
        //* for local
        String[] cmd1 = Configuration.BASH_CMD_1;
        Utils.execBashCmd(cmd1[0], cmd1[1], cmd1[2] + uuid, cmd1[3] + uuid);
        String[] cmd2 = Configuration.BASH_CMD_2;
        List<String> flagList = Utils.execBashCmd(cmd2[0], cmd2[1], cmd2[2], cmd2[3], cmd2[4], cmd2[5] + uuid);
        // */

        /* for docker
        String[] cmd = Configuration.BASH_CMD;
        List<String> flagList = Utils.execBashCmd(cmd[0], cmd[1], cmd[2].replaceAll("qe_text", "qe_" + uuid));
        // */

        // 3. parse tags
        int pos = 0;
        for (String flag : flagList) {
            // find and save entity
            if ("0;".equals(flag))
                entityList.add(totalEWords.get(pos));
            ++pos;
        }
        return entityList;
    }
    // */

    /**
     * 为 QA 提供接口，网爬证据文章。
     */
    public void answer(String query, Extration extration, List<String> words, List<String> flags) {
        // 0. 对每次查询分配不同的文章
        String uuid = UUID.randomUUID().toString().replaceAll("-", "_");
        String qeTextFile = Configuration.QE_TEXT_PATH.replaceAll("qe_text", "qe_" + uuid);

        // 1. initialize qe_text
        try (BufferedWriter bfWriter = new BufferedWriter(new FileWriter(qeTextFile))) {
            List<String> qWords = segmenter.segment(query);
            bfWriter.write(listToStr(qWords) + "\n");
            List<String> evidences;
            if (extration == null)
                evidences = search.getEvidences(query);
            else
                evidences = search.getEvidences(query, extration);
            for (String evidence : evidences) {
                List<String> eWords = segmenter.segment(evidence);
                words.addAll(eWords);
                // 区分不同证据文章，这样会导致与 flags 大小不一，使用时要过滤！
                words.add("\n");
                bfWriter.write(listToStr(eWords) + "\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 2. exec qa.sh
        //* for local
        String[] cmd1 = Configuration.BASH_CMD_1;
        Utils.execBashCmd(cmd1[0], cmd1[1], cmd1[2] + uuid, cmd1[3] + uuid);
        String[] cmd2 = Configuration.BASH_CMD_2;
        List<String> flagList = Utils.execBashCmd(cmd2[0], cmd2[1], cmd2[2], cmd2[3], cmd2[4], cmd2[5] + uuid);
        // */

        /* for docker
        String[] cmd = Configuration.BASH_CMD;
        List<String> flagList = Utils.execBashCmd(cmd[0], cmd[1], cmd[2].replaceAll("qe_text", "qe_" + uuid));
        // */

        // 3. transfer flags
        flags.addAll(flagList);
    }

    /**
     * 为 rc 提供接口，即证据文章由 用户 提供。
     */
    public void answer(String query, String evidence, List<String> words, List<String> flags) {
        // 0. 对每次查询分配不同的文章
        String uuid = UUID.randomUUID().toString().replaceAll("-", "_");
        String qeTextFile = Configuration.QE_TEXT_PATH.replaceAll("qe_text", "qe_" + uuid);

        // 1. initialize qe_text
        try (BufferedWriter bfWriter = new BufferedWriter(new FileWriter(qeTextFile))) {
            List<String> qWords = segmenter.segment(query);
            bfWriter.write(listToStr(qWords) + "\n");
            words.addAll(segmenter.segment(evidence));
            words.add("\n");
            bfWriter.write(listToStr(words) + "\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 2. exec qa.sh
        //* for local
        String[] cmd1 = Configuration.BASH_CMD_1;
        Utils.execBashCmd(cmd1[0], cmd1[1], cmd1[2] + uuid, cmd1[3] + uuid);
        String[] cmd2 = Configuration.BASH_CMD_2;
        List<String> flagList = Utils.execBashCmd(cmd2[0], cmd2[1], cmd2[2], cmd2[3], cmd2[4], cmd2[5] + uuid);
        // */

        /* for docker
        String[] cmd = Configuration.BASH_CMD;
        List<String> flagList = Utils.execBashCmd(cmd[0], cmd[1], cmd[2].replaceAll("qe_text", "qe_" + uuid));
        // */

        // 3. transfer flags
        flags.addAll(flagList);
    }

    // change list<String> to String
    private String listToStr(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs)
            sb.append(s).append(' ');
        if (sb.length() >= 1)
            sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

}
