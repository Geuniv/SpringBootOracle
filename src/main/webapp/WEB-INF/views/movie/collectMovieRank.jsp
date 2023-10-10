<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="kopo.poly.util.CmmUtil" %>
<%
    // Controller로부터 전달받은 데이터
    String msg = CmmUtil.nvl((String) request.getAttribute("msg"));
%>
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CGV 영화 수집 결과</title>
</head>
<body>
<%=msg%>
</body>
</html>