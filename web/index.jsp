<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: jie
  Date: 18-3-21
  Time: 上午9:02
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title>QA</title>
</head>
<body>

<h1 align="center" style="font-size: 70px;margin-top: 50px">Hello QA!!!</h1>
<div align="center">
    <s:form action="queryAction_query" method="POST" namespace="/">
        <s:if test="%{qaModel != null}">
            <input name="query" value="${qaModel.query}" maxlength="255"
                   style="font-size: 26px">&nbsp;&nbsp;&nbsp;&nbsp;
        </s:if>
        <s:else>
            <input name="query" placeholder="请输入您的问题～～" maxlength="255"
                   style="font-size: 26px">&nbsp;&nbsp;&nbsp;&nbsp;
        </s:else>

        <input type="submit" value="QA" style="font-size: 25px"/>
        <a href="rcIndex.jsp" style="font-size: 26px">RC</a>

    </s:form>
</div>
<s:if test="%{qaModel != null}">
    <h5 align="center" style="margin-top: 20px;font-size: 30px">${qaModel.answer}</h5>
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
</body>
</html>
