import org.junit.Before;
import org.junit.Test;
import web.model.QAModel;
import web.service.QAService;

public class TestService {

    private QAService qaService;

    @Before
    public void before() {
        qaService = new QAService();
    }

    @Test
    public void testGetAnswer() {
        String query = "姚明的妻子是谁？";
        String answer = qaService.getAnswer(query);
        System.out.println(query);
        System.out.println("\t[Answer] :: " + answer);
        query = "苹果的创始人是谁？";
        answer = qaService.getAnswer(query);
        System.out.println(query);
        System.out.println("\t[Answer] :: " + answer);
        // 日志
        // 解决第二次查询无响应问题
        // 原因: HttpClient 连接池设置太小
    }

    @Test
    public void testGetQAModel() {
        String query = "姚明的妻子是谁？";
        QAModel qaModel = new QAModel();
        qaModel.setQuery(query);
        qaService.getAnswer(qaModel);
        System.out.println();
    }

    @Test
    public void testGetRC() {
        String query = "姚明的妻子是谁？";
        String evidence = "姚明的妻子是叶莉。";
        QAModel qaModel = new QAModel();
        qaService.getAnswer(query, evidence, qaModel);
        System.out.println();
    }


}
