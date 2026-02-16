<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">
    <div class="error-page">

        <div class="error-code">404</div>

        <div class="error-text">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <c:out value="${errorMessage}" />
                </c:when>
                <c:otherwise>
                    Oops! The page you are looking for does not exist.
                </c:otherwise>
            </c:choose>
        </div>

        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
            <i class="bi bi-house"></i> Back to Home
        </a>

    </div>
</div>

<%@ include file="../fragments/footer.jsp" %>
