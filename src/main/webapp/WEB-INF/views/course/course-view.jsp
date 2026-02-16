<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">

    <c:if test="${not empty course}">

        <h2 class="page-title text-center">${course.title}</h2>
        <p class="page-subtitle text-center">Course Details</p>
        <hr>

        <div class="detail-card">
            <div class="detail-row">
                <span class="detail-label">Description</span>
                <span class="detail-value">${course.description}</span>
            </div>

            <div class="detail-row">
                <span class="detail-label">Duration</span>
                <span class="detail-value">${course.durationInHours} Hours</span>
            </div>

            <div class="detail-row">
                <span class="detail-label">Fees</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${course.fees != null}">
                            <c:out value="${course.fees}" />
                        </c:when>
                        <c:otherwise>Free</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="detail-row">
                <span class="detail-label">Instructor</span>
                <span class="detail-value">${course.instructor}</span>
            </div>
        </div>

        <div class="actions mt-2">
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline">
                <i class="bi bi-arrow-left"></i> Back to Courses
            </a>
            <sec:authorize access="hasRole('ADMIN')">
                <a href="${pageContext.request.contextPath}/courses/edit/${course.id}" class="btn btn-primary btn-sm">
                    <i class="bi bi-pencil-square"></i> Edit
                </a>
            </sec:authorize>
        </div>

    </c:if>

    <c:if test="${empty course}">
        <div class="empty-state">
            <p class="text-muted">Course not found.</p>
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline">
                <i class="bi bi-arrow-left"></i> Back to Courses
            </a>
        </div>
    </c:if>

</div>

<%@ include file="../fragments/footer.jsp" %>
