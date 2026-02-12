<%@ include file="../fragments/header.jsp" %>

<div class="container">

    <h2>Login</h2>

    <c:if test="${not empty message}">
        <p class="info">${message}</p>
    </c:if>

    <c:if test="${param.error != null}">
        <p class="error">Invalid username or password</p>
    </c:if>

    <c:if test="${param.logout != null}">
        <p class="info" style="color: green;">
            You have been logged out successfully
        </p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">

        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}" />

        <div class="form-group">
            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   required />
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password"
                   id="password"
                   name="password"
                   required />
        </div>

        <div class="actions">
            <input type="submit" value="Login" />
        </div>

    </form>

</div>

<%@ include file="../fragments/footer.jsp" %>
