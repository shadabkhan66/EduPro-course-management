<%@ include file="../fragments/header.jsp" %>

<div class="container">

    <%-- Page heading with "Add" button on the right --%>
    <div class="flex-between">
        <div>
            <h2 class="page-title">Courses</h2>
            <p class="page-subtitle mb-0">
                <span class="badge">${numberOfCourses} available</span>
            </p>
        </div>

        <%-- Only ADMINs can add courses --%>
        <sec:authorize access="hasRole('ADMIN')">
            <a href="${pageContext.request.contextPath}/courses/add" class="btn btn-primary btn-sm">
                <i class="bi bi-plus-circle"></i> Add Course
            </a>
        </sec:authorize>
    </div>

    <hr>

    <%-- Flash messages from redirects (create / update / delete) --%>
    <c:if test="${not empty successMessage}">
        <div class="msg msg-success"><i class="bi bi-check-circle"></i> ${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="msg msg-error"><i class="bi bi-exclamation-circle"></i> ${errorMessage}</div>
    </c:if>

    <%-- Course table or empty state --%>
    <c:choose>
        <c:when test="${not empty courses}">
            <table>
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Duration</th>
                    <th>Fees</th>
                    <th>Instructor</th>
                    <%-- Show action columns only for ADMIN --%>
                    <sec:authorize access="hasRole('ADMIN')">
                        <th class="col-actions">Actions</th>
                    </sec:authorize>
                </tr>
                </thead>

                <tbody>
                <%--
                    <c:forEach> iterates over the 'courses' list from the model.
                    var="course" creates a loop variable accessible via ${course.xxx}
                --%>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/courses/${course.id}"
                               style="font-weight: 500; color: #2980b9;">
                                ${course.title}
                            </a>
                        </td>
                        <td class="text-muted text-small">${course.description}</td>
                        <td>${course.durationInHours} hrs</td>
                        <td>
                            <c:choose>
                                <c:when test="${course.fees != null}">
                                    <c:out value="${course.fees}" />
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Free</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${course.instructor}</td>

                        <sec:authorize access="hasRole('ADMIN')">
                        <td class="col-actions">
                            <%-- Edit link --%>
                            <a href="${pageContext.request.contextPath}/courses/edit/${course.id}"
                               class="btn-inline edit" title="Edit">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <%-- Delete form (POST with CSRF) --%>
                            <form action="${pageContext.request.contextPath}/courses/delete/${course.id}"
                                  method="post"
                                  style="display: inline;"
                                  onsubmit="return confirmDelete('${course.title}')">
                                <input type="hidden"
                                       name="${_csrf.parameterName}"
                                       value="${_csrf.token}" />
                                <button type="submit" class="btn-inline delete" title="Delete">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                        </sec:authorize>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <div class="empty-state">
                <i class="bi bi-journal-x" style="font-size: 2rem; color: #ccc;"></i>
                <p>No courses available yet.</p>
                <sec:authorize access="hasRole('ADMIN')">
                    <a href="${pageContext.request.contextPath}/courses/add" class="btn btn-primary btn-sm">
                        <i class="bi bi-plus-circle"></i> Add First Course
                    </a>
                </sec:authorize>
            </div>
        </c:otherwise>
    </c:choose>

</div>

<%@ include file="../fragments/footer.jsp" %>
