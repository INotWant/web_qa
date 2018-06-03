package web.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import web.model.QAModel;
import web.service.QAService;

import javax.annotation.Resource;
import java.util.logging.Level;
import java.util.logging.Logger;


@Controller
@Scope("prototype")
public class QueryAction implements ModelDriven<QAModel> {

    private QAModel qaModel = new QAModel();

    @Resource
    private QAService qaService;

    @Override
    public QAModel getModel() {
        return qaModel;
    }

    public String query() {
        QAModel qaModel = new QAModel();
        qaModel.setQuery(this.qaModel.getQuery());

        Logger logger = Logger.getLogger("QueryAction.class");
        logger.log(Level.INFO, "Query :: " + this.qaModel.getQuery());

        qaModel.setAnswer("Unknown");

        /* Deprecated
        String answer = qaService.getAnswer(qaModel.getQuery());
        if (answer != null)
            qaModel.setAnswer(answer);
        // */

        qaService.getAnswer(qaModel);
        ActionContext.getContext().getValueStack().set("qaModel", qaModel);
        return "toAnswer";
    }

    public String rc() {
        Logger logger = Logger.getLogger("QueryAction.class");
        logger.log(Level.INFO, "Query :: " + qaModel.getQuery());

        QAModel qaModel = new QAModel();
        qaModel.setQuery(this.qaModel.getQuery());
        qaModel.setSourceEvidence(this.qaModel.getSourceEvidence());
        qaService.getAnswer(qaModel.getQuery(), qaModel.getSourceEvidence(), qaModel);
        ActionContext.getContext().getValueStack().set("qaModel", qaModel);
        return "toRC";
    }

}
