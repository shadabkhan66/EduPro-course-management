<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">
    <div class="error-page">

        <div class="error-code" style="color: #e67e22;">500</div>

        <div class="error-text">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <c:out value="${errorMessage}" />
                </c:when>
                <c:otherwise>
                    Something went wrong on our end. Please try again later.
                </c:otherwise>
            </c:choose>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                <i class="bi bi-house"></i> Back to Home
            </a>
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline">
                <i class="bi bi-book"></i> View Courses
            </a>
        </div>

    </div>
</div>

<%@ include file="../fragments/footer.jsp" %>
