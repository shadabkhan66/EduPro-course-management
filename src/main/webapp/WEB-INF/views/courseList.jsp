<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>

<%-- Tag Libraries --%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%--
    EL        : ${}
    JSTL     : <c:forEach>, <c:if>, <c:choose>
    Spring   : <spring:message>, <form:form>
--%>

<html>
<head>
    <title>Course List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
	
	<script type="text/javascript">
	    function confirmDelete(courseTitle) {
	        return confirm("Are you sure you want to delete the course: \"" + courseTitle + "\" ?");
	    }
	</script>

    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #f9f9f9;
        }

        .container {
            width: 70%;
            margin: 30px auto;
            background: #ffffff;
            padding: 20px 30px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            color: tomato;
        }

        .info {
            text-align: center;
            color: #555;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 8px;
            text-align: center;
            border: 1px solid #ccc;
        }

        .edit {
            color: #2b8cff;
        }

        .delete {
            color: #ff005d;
        }

        a {
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>
<div class="container">

    <h2>Course List</h2>
    <hr>

    <p class="info">This is <b>courseList.jsp</b></p>

    <br>

    <c:if test="${not empty successMessage}">
        <p class="info" style="color: green;">${successMessage}</p>
    </c:if>
    
    <c:if test="${not empty errorMessage}">
        <p class="info" style="color: red;">${errorMessage}</p>
    </c:if>
    <br>

    <c:choose>
        <c:when test="${not empty courses}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Duration (Hours)</th>
                    <th>Fees</th>
                    <th>Instructor</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.id}</td>
                        <td>${course.title}</td>
                        <td>${course.description}</td>
                        <td>${course.durationInHours}</td>
                        <td>${course.fees}</td>
                        <td>${course.instructor}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/courses/edit/${course.id}">
                                <i class="bi bi-pencil-square edit"></i> Edit
                            </a>
                        </td>

						<td>
						    <form action="${pageContext.request.contextPath}/courses/delete/${course.id}"
						          method="post"
						          style="display:inline;"
						          onsubmit="return confirmDelete('${course.title}')">
                                 <input type="hidden"
                                       name="${_csrf.parameterName}"
                                       value="${_csrf.token}" />
						        <button type="submit" style="border:none; background:none; cursor:pointer;">
						            <i class="bi bi-trash delete"></i> Delete
						        </button>
						
						    </form>
						</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <br>
            <p>No of courses available : ${numberOfCourses}</p>	
        </c:when>

        <c:otherwise>
            <p class="info">No courses available. Please add a course.</p>
        </c:otherwise>
    </c:choose>

    <br>

	<br>

    <a href="${pageContext.request.contextPath}/courses/add">
        ➕ Add New Course
    </a>
	

	
</div>
</body>
</html>
