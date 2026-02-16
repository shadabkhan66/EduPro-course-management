<%@ include file="../fragments/header.jsp" %>

<div class="container text-center">

    <h1 class="page-title" style="font-size: 1.8rem; margin-bottom: 0.5rem;">
        <i class="bi bi-mortarboard-fill"></i> Welcome to EduPro
    </h1>
    <p class="page-subtitle" style="font-size: 1rem;">
        Campus Course Management System
    </p>

    <hr>

    <%-- Logout message (set via session attribute in LogoutSuccessHandler) --%>
    <c:if test="${not empty logoutMessage}">
        <div class="msg msg-success">${logoutMessage}</div>
    </c:if>

    <p style="color: #666; margin: 1.5rem 0;">
        Browse available courses, register as a student, or manage courses as an administrator.
    </p>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary">
            <i class="bi bi-book"></i> View Courses
        </a>
        <sec:authorize access="isAnonymous()">
            <a href="${pageContext.request.contextPath}/users/register" class="btn btn-outline">
                <i class="bi bi-person-plus"></i> Register
            </a>
        </sec:authorize>
    </div>

</div>

<%@ include file="../fragments/footer.jsp" %>
