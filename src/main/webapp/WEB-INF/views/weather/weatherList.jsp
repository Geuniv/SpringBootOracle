<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="kopo.poly.util.CmmUtil" %>
<%@ page import="kopo.poly.dto.WeatherDTO" %>
<%
    // Controller로부터 전달받은 데이터
    List<WeatherDTO> rList = (List<WeatherDTO>) request.getAttribute("rList");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>네이버 날씨 수집 결과</title>
    <link rel="stylesheet" href="/css/table.css"/>
</head>
<body>
<h2>네이버 날씨 정보</h2>
<hr/>
<br/>
<div class="divTable minimalistBlack">
    <div class="divTableHeading">
        <div class="divTableRow">
            <div class="divTableHead">지역</div>
            <div class="divTableHead">온도</div>
            <div class="divTableHead">날씨</div>
        </div>
    </div>
    <div class="divTableBody">
        <%
            for (WeatherDTO rDTO : rList) {
        %>
        <div class="divTableRow">
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getRegion())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getTemp())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getWeather())%>
            </div>
        </div>
        <%
            }
        %>
    </div>
</div>
</body>
</html>
