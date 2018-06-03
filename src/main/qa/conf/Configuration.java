package qa.conf;

public class Configuration {

    // for WEB
//    public static final String QE_TEXT_PATH = "/home/QA/data/qe_text";
//    public static final String[] BASH_CMD = {"python", "/home/QA/src/mLSTM_crf/mLSTM_crf_application.py", QE_TEXT_PATH};

    // for TEST
    public static final String QE_TEXT_PATH = "/home/jie/project/java/web_qa/src/resources/text/qe_text";
//
    public static final String[] BASH_CMD_1 = {"docker", "cp", "/home/jie/project/java/web_qa/src/resources/text/qe_"
            , "paddle:/home/QA/data/qe_"};
    public static final String[] BASH_CMD_2 = {"docker", "exec", "paddle", "/usr/bin/python", "/home/QA/src/mLSTM_crf/mLSTM_crf_application.py", "/home/QA/data/qe_"};

}
