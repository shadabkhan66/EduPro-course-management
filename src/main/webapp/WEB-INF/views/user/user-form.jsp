
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>    
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${pageTitle}</title>
<style>
    body {
        font-family: Arial, Helvetica, sans-serif;
        background-color: #f9f9f9;
        margin: 0;
        padding: 20px;
    }

    .container {
        width: 50%;
        margin: 30px auto;
        background: #ffffff;
        padding: 25px 30px;
        border-radius: 5px;
        box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }

    h2 {
        text-align: center;
        color: #333;
        margin-bottom: 20px;
    }

    .info {
        text-align: center;
        color: #555;
        margin-bottom: 20px;
    }

    .form-group {
        margin-bottom: 15px;
    }

    .form-group label {
        display: block;
        font-weight: bold;
        margin-bottom: 5px;
    }

    .form-group input, 
    .form-group select {
        width: 100%;
        padding: 8px 10px;
        box-sizing: border-box;
        border: 1px solid #ccc;
        border-radius: 3px;
    }

    .error {
        color: red;
        font-size: 0.9em;
        margin-top: 3px;
    }

    .actions {
        text-align: center;
        margin-top: 20px;
    }

    .actions button {
        padding: 10px 20px;
        background-color: #28a745;
        color: white;
        border: none;
        border-radius: 3px;
        cursor: pointer;
    }

    .actions button:hover {
        background-color: #218838;
    }

    a.back-link {
        display: block;
        text-align: center;
        margin-top: 20px;
        color: #555;
        text-decoration: none;
    }

    a.back-link:hover {
        text-decoration: underline;
    }
</style>
</head>
<body>
<div class="container">
    <h2>${pageTitle}</h2>
    <p class="info">${isEditMode ? 'Edit user details' : 'Register a new user'}</p>

    <c:choose>
        <c:when test="${isEditMode}">
            <c:url value="/users/edit" var="formAction" />
        </c:when>
        <c:otherwise>
            <c:url value="/users/register" var="formAction" />
        </c:otherwise>
    </c:choose>

    <!-- Global validation errors -->
    <frm:errors path="*" cssClass="error"/>

    <frm:form method="post" action="${formAction}" modelAttribute="user">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <frm:hidden path="id"/>

        <div class="form-group">
            <frm:label path="username">Username</frm:label>
            <frm:input path="username" placeholder="Enter username"/>
            <frm:errors path="username" cssClass="error"/>
        </div>

        <div class="form-group">
            <frm:label path="email">Email</frm:label>
            <frm:input path="email" placeholder="Enter email"/>
            <frm:errors path="email" cssClass="error"/>
        </div>

        <div class="form-group">
            <frm:label path="password">Password</frm:label>
            <frm:password path="password" placeholder="Enter password"/>
            <frm:errors path="password" cssClass="error"/>
        </div>

        <div class="form-group">
            <frm:label path="firstName">First Name</frm:label>
            <frm:input path="firstName" placeholder="Enter first name"/>
            <frm:errors path="firstName" cssClass="error"/>
        </div>

        <div class="form-group">
            <frm:label path="lastName">Last Name</frm:label>
            <frm:input path="lastName" placeholder="Enter last name"/>
            <frm:errors path="lastName" cssClass="error"/>
        </div>

        <div class="form-group">
            <frm:label path="role">Role</frm:label>
            <frm:select path="role">
                <frm:option value="" label="-- Select Role --"/>
                <c:forEach var="role" items="${roles}">
                    <frm:option value="${role}" label="${role}"/>
                </c:forEach>
            </frm:select>
            <frm:errors path="role" cssClass="error"/>
        </div>

        <div class="actions">
            <button type="submit">${isEditMode ? 'Update' : 'Register'}</button>
        </div>
    </frm:form>

    <a class="back-link" href="${pageContext.request.contextPath}/users">← Back to User List</a>
</div>
</body>
</html>




