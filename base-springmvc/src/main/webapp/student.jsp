<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.net.URLEncoder" %> 
<html>
<head>
    <title>Spring MVC表单处理</title>
</head>
<body>

<h2>Student Information</h2>
<form method="POST" action="/base-springmvc/addStudent">
   <table>
    <tr>
        <td><label path="name">名字：</label></td>
        <td><input path="name" value=""/></td>
    </tr>
    <tr>
        <td><label path="age">年龄：</label></td>
        <td><input path="age" /></td>
    </tr>
    <tr>
        <td><label path="id">编号：</label></td>
        <td><input path="id" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="提交表单"/>
        </td>
    </tr>
</table>  
</form>
</body>
</html>