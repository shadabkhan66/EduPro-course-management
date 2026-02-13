<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--
    EL        : ${}
    JSTL     : <c:forEach>, <c:if>, <c:choose>
    Spring   : <spring:message>, <form:form>
--%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>${pageTitle != null ? pageTitle : 'Course Management'}</title>
		 <link rel="stylesheet"
                  href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">

        <script type="text/javascript">
            function confirmDelete(courseTitle) {
                return confirm("Are you sure you want to delete the course: \"" + courseTitle + "\" ?");
            }
        </script>

		<style>
            /* ========== Global ========== */
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f4f6f9;

            }

            /* ========== Header ========== */
            header {
                background-color: #ffffff;
                padding: 20px;
                text-align: center;
                border-bottom: 1px solid #ddd;
            }

            header h1 {
                margin: 0;
                color: #333;
            }

            nav {
                margin-top: 10px;
            }

            nav a {
                margin: 0 10px;
                text-decoration: none;
                color: #333;
                font-weight: 500;
            }

            nav a:hover {
                text-decoration: underline;
            }

            /* ========== Container ========== */
            .container {
                width: 70%;
                margin: 40px auto;
                background: #ffffff;
                padding: 25px 35px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
                border-radius: 6px;
            }

            /* ========== Headings ========== */
            h2 {
                text-align: center;
                color: #e74c3c;
                margin-bottom: 20px;
            }

            .info {
                text-align: center;
                color: #555;
            }

            /* ========== Table ========== */
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }

            th {
                background-color: #f1f1f1;
                font-weight: bold;
            }

            th, td {
                padding: 10px;
                text-align: center;
                border: 1px solid #ddd;
            }

            tr:nth-child(even) {
                background-color: #fafafa;
            }

            .edit {
                color: #2b8cff;
            }

            .delete {
                color: #ff005d;
            }

            /* ========== Links ========== */
            a {
                text-decoration: none;
                color: #333;
            }

            a:hover {
                text-decoration: underline;
            }

            /* ========== Forms ========== */
            .form-group {
                margin-bottom: 18px;
            }

            .form-group label {
                display: block;
                font-weight: bold;
                margin-bottom: 6px;
            }

            .form-group input {
                width: 100%;
                padding: 8px 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 14px;
            }

            .form-group input:focus {
                border-color: #2b8cff;
                outline: none;
            }
            /* ===== Modern Details Card ===== */

            .details-card {
                background: #ffffff;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
            }

            .details-card h2 {
                text-align: center;
                color: #2b8cff;
                margin-bottom: 25px;
            }

            .detail-row {
                display: flex;
                justify-content: space-between;
                padding: 12px 0;
                border-bottom: 1px solid #eee;
            }

            .detail-row:last-child {
                border-bottom: none;
            }

            .label {
                font-weight: 600;
                color: #555;
            }

            .value {
                color: #333;
            }


            /* ========== Validation Errors ========== */
            .error {
                color: red;
                font-size: 0.85em;
                margin-top: 4px;
            }

            /* ========== Buttons ========== */
            .actions {
                text-align: center;
                margin-top: 25px;
            }

            .actions input,
            button {
                padding: 8px 18px;
                border-radius: 4px;
                border: none;
                cursor: pointer;
                font-size: 14px;
            }

            .actions input {
                background-color: #2b8cff;
                color: white;
            }

            .actions input:hover {
                background-color: #1f6fd6;
            }

            button {
                background: none;
            }

            /* ========== Logout Button ========== */
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

            /* ========== Footer ========== */
            footer {
                background-color: #ffffff;
                padding: 15px;
                text-align: center;
                margin-top: 40px;
                border-top: 1px solid #ddd;
                color: #666;

            }

        </style>
	</head>
<body>
   <header>
	    <h1>Academic </h1>
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
			    <a href="${pageContext.request.contextPath}/login">Login</a> |
			    <a href="${pageContext.request.contextPath}/users/register">Register</a>
			</sec:authorize>

		</nav>
   </header>