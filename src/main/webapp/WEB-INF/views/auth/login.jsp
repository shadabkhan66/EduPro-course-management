<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">

    <h2 class="page-title text-center">Login</h2>
    <p class="page-subtitle text-center">Sign in to your account</p>
    <hr>

    <%-- Flash message from registration success --%>
    <c:if test="${not empty message}">
        <div class="msg msg-success">${message}</div>
    </c:if>

    <%-- Spring Security sets ?error on bad credentials --%>
    <c:if test="${param.error != null}">
        <div class="msg msg-error">Invalid username or password</div>
    </c:if>

    <%-- Spring Security sets ?logout on default logout --%>
    <c:if test="${param.logout != null}">
        <div class="msg msg-success">You have been logged out successfully</div>
    </c:if>

    <%--
        IMPORTANT: This is a plain HTML <form>, NOT Spring's <frm:form>.
        Spring Security's /login endpoint does NOT provide a model attribute,
        so <frm:form> would throw "Neither BindingResult nor plain target object
        for bean name 'command'". Plain <form> avoids this.

        The field names MUST be 'username' and 'password' -- Spring Security
        expects exactly these names by default.
    --%>
    <form method="post" action="${pageContext.request.contextPath}/login">

        <%-- CSRF token (required for all POST requests with Spring Security) --%>
        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}" />

        <div class="form-group">
            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Enter your username"
                   autocomplete="username"
                   required />
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password"
                   id="password"
                   name="password"
                   placeholder="Enter your password"
                   autocomplete="current-password"
                   required />
        </div>

        <div class="actions">
            <button type="submit" class="btn btn-primary" style="width: 100%;">
                <i class="bi bi-box-arrow-in-right"></i> Login
            </button>
        </div>

    </form>

    <p class="text-center text-muted mt-2 text-small">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/users/register">Register here</a>
    </p>

</div>

<%@ include file="../fragments/footer.jsp" %>
