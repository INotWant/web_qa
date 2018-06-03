<%--
  Created by IntelliJ IDEA.
  User: jie
  Date: 18-3-25
  Time: 上午10:44
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RC</title>
</head>
<body>
<h1 align="center" style="font-size: 70px;margin-top: 50px">Hello RC!!!</h1>
<div align="center">
    <s:form action="queryAction_rc" method="POST" namespace="/">
        <s:if test="%{qaModel != null}">
            <div>
                <input name="query" value="${qaModel.query}" maxlength="300" style="font-size: 26px;width: 585px">
            </div>
            <br/>
            <div>
                <textarea name="sourceEvidence" style="font-size: 22px;overflow-y: auto;width:600px;resize:none"
                          rows="16">${qaModel.sourceEvidence}</textarea>
            </div>
            <br/>
        </s:if>
        <s:else>
            <div>
                <input name="query" placeholder="请输入您的问题～～" maxlength="300" style="font-size: 26px;width: 585px">
            </div>
            <br/>
            <div>
                <textarea name="sourceEvidence" placeholder="请您提供支持回答问题的文章～～"
                          style="font-size: 22px;overflow-y: auto;width:600px;resize:none" rows="16"></textarea>
            </div>
            <br/>
        </s:else>
        <input type="submit" value="RC" style="font-size: 28px;width: 450px;height: 45px;font-style: inherit"/>
        <s:if test="%{qaModel != null}">
            <h5 align="center" style="margin-top: 20px;font-size: 30px">${qaModel.answer}</h5>
            <br/>
            <s:iterator value="#request.qaModel.evidences" var="evidences">
                <div style="width: 800px; margin: 0 auto;font-size: 22px">
                    <s:iterator value="evidences" var="evidence">
                        <s:if test="#evidence.flag == '0;'">
                            <span style="background:#FF0000;"><s:property value="#evidence.word"/></span>
                        </s:if>
                        <s:elseif test="#evidence.flag == '1;'">
                            <span style="background:#FF0000;"><s:property value="#evidence.word"/></span>
                        </s:elseif>
                        <s:elseif test="#evidence.flag == '2;'">
                            <span style="background:#FFEFDB;"><s:property value="#evidence.word"/></span>
                        </s:elseif>
                        <s:else>
                            <span style="background:#E0EEEE;"><s:property value="#evidence.word"/></span>
                        </s:else>
                    </s:iterator>
                </div>
                <br/>
            </s:iterator>
        </s:if>
        <s:else>
            <h5 align="center" style="margin-top: 20px;font-size: 30px"></h5>
        </s:else>
    </s:form>
</div>


</body>
</html>
