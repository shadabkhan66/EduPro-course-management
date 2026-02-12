
<%@ include file="../fragments/header.jsp" %>


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


<%@ include file="../fragments/footer.jsp" %>