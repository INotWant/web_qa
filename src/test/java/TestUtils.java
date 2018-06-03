import org.junit.Test;
import qa.util.Utils;

public class TestUtils {

    @Test
    public void testExecBashCmd() {
//        String[] cmds = {"docker", "cp", "/home/jie/project/java/web_qa/src/resources/text/qe-15a8a9d7-830b-48fd-a865-6a079f54f592"
//                , "qa:/home/QA/data/qe-15a8a9d7-830b-48fd-a865-6a079f54f592"};
        String[] cmds = {"docker", "exec", "qa", "/usr/bin/python", "/home/QA/application.py", "/home/QA/data/qe_1e7c1b41_f170_4222_8df7_7754dbb5629a"};

        Utils.execBashCmd(cmds);
    }


}
