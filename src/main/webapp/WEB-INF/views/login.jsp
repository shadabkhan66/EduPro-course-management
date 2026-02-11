<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Log-In</title>
</head>
<body>
    <h3>${massage}</h3>
	<div>
		<form method="post" action="/login">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}" />

    <div>
        <label for="username">Username :</label>
        <input type="text" id="username" name="username" />
    </div>

    <div>
        <label for="password">Password :</label>
        <input type="password" id="password" name="password" />
    </div>

    <div>
        <input type="submit" value="Login" />
    </div>
</form>
	</div>	
	<div>
		
	</div>
</body>
</html>