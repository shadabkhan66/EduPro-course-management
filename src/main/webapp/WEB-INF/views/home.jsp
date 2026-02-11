<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
		<style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
            }
            header {
                background-color: #f8f8f8;
                padding: 20px;
                text-align: center;
            }
            nav a {
                margin: 0 10px;
                text-decoration: none;
                color: #333;
            }
            nav a:hover {
                text-decoration: underline;
            }
            .content {
                padding: 20px;
            }
            footer {
                background-color: #f8f8f8;
                padding: 10px;
                text-align: center;
                position: fixed;
                width: 100%;
                bottom: 0;
            }
            .logout-btn {
                background-color: transparent;
                border: none;
                color: #333;
                cursor: pointer;
                font-size: 1em;
            }
            .logout-btn:hover {
                text-decoration: underline;
            }
        </style>
	</head>
<body>
   <header>
	    <h1>Welcome to the Home Page</h1>
	    <p>This is a simple JSP page.</p>
	    
	    <nav>

			<a href="${pageContext.request.contextPath}/">Home</a> |
			<a href="${pageContext.request.contextPath}/courses">Course</a> |
			<a href="${pageContext.request.contextPath}/about">About</a> |
			<%-- <a href="${pageContext.request.contextPath}/login">LogIn</a> | --%>
			
			<sec:authorize access="isAuthenticated()">
			    <form action="${pageContext.request.contextPath}/logout" method="POST" style="display:inline;">
			        <input type="hidden"
			               name="${_csrf.parameterName}"
			               value="${_csrf.token}" />
			        <button type="submit" class="logout-btn">LogOut</button>
			    </form>
			</sec:authorize>
			
			<sec:authorize access="isAnonymous()">
			    <a href="${pageContext.request.contextPath}/login">Login</a>
			</sec:authorize>

		</nav>
   </header>
   <div class="content">
        <h2>Home Page Content</h2>
        <p>This is the main content area of the home page.</p>
        <p>${logoutMessage}</p>
    </div>
    
    <footer>
        <p>&copy; 2024 My Website. All rights reserved.</p>
    </footer>
</body>
</html>