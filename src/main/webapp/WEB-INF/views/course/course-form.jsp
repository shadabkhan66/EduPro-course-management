<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
	<head>
    <title>Add Course</title>

    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #f9f9f9;
        }

        .container {
            width: 60%;
            margin: 30px auto;
            background: #ffffff;
            padding: 20px 30px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: tomato;
        }

        .info {
            text-align: center;
            color: #555;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .form-group input {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }

        .error {
            color: red;
            font-size: 0.9em;
        }

        .actions {
            text-align: center;
            margin-top: 20px;
        }

        .actions input {
            padding: 8px 20px;
        }
    </style>
	</head>

	<body>
	
		<div class="container">
		
		    <h2>${pageHeading}</h2>
			
			<!-- Based on ifEditMode we should have sparate path -->
			<c:choose>
		        <c:when test="${isEditMode}">
		            <c:url value='/courses/${course.id}' var='formActionUrl'/>
		        </c:when>
		        <c:otherwise>
		            <c:url value='/courses' var='formActionUrl'/>
		        </c:otherwise>
		     </c:choose>
			
		    <hr>
		
		    <p class="info">This is <b>form.jsp</b></p>
		
		    <!-- Global validation errors -->
		    <form:errors path="*" cssClass="error"/>
		
		    <form:form
		            method="post"
		            modelAttribute="course"
		            action="${formActionUrl}">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		        <div class="form-group" >
		            <%-- <form:label path="id">Course Id</form:label> --%>
		            <form:hidden path="id"/>
		            <form:errors path="id" cssClass="error"/>
		        </div>
		        
		        <div class="form-group">
		            <form:label path="title">Course Name</form:label>
		            <form:input path="title" placeholder="Java Full Stack"/>
		            <form:errors path="title" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <form:label path="description">Description</form:label>
		            <form:input path="description" placeholder="Complete Java + Spring"/>
		            <form:errors path="description" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <form:label path="durationInHours">Duration (hours)</form:label>
		            <form:input path="durationInHours" type="number"/>
		            <form:errors path="durationInHours" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <form:label path="fees">Fees</form:label>
		            <form:input path="fees" type="number"/>
		            <form:errors path="fees" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <form:label path="instructor">Instructor</form:label>
		            <form:input path="instructor" placeholder="John Doe"/>
		            <form:errors path="instructor" cssClass="error"/>
		        </div>
		
		        <div class="actions">
		            <input type="submit" value="${submitButtonLabel}"/>
		        </div>
		
		    </form:form>
		
		    <br>
		
		    <a href="${pageContext.request.contextPath}/courses">
		        ← Back to Course List
		    </a>
		
		</div>
		
	</body>
</html>
