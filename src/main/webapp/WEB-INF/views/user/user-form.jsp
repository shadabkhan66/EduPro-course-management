<%--
    ============================================================
    user-form.jsp -- User Registration (and future Edit) Form
    ============================================================
    FIX: This page previously had its own <html>, <head>, <style> block
    and did NOT use the shared header/footer fragments. Now it uses the
    same layout as all other pages for consistent look and feel.
    ============================================================
--%>
<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">

    <h2 class="page-title text-center">${pageTitle}</h2>
    <p class="page-subtitle text-center">
        ${isEdit ? 'Update user details' : 'Create a new account'}
    </p>
    <hr>

    <%-- Dynamic form action URL based on create/edit mode --%>
    <c:choose>
        <c:when test="${isEdit}">
            <c:url value="/users/edit" var="formAction" />
        </c:when>
        <c:otherwise>
            <c:url value="/users/register" var="formAction" />
        </c:otherwise>
    </c:choose>

    <%-- Global validation errors --%>
    <frm:errors path="*" cssClass="form-errors"/>

    <frm:form method="post" action="${formAction}" modelAttribute="user">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <frm:hidden path="id"/>

        <div class="form-group">
            <frm:label path="username">Username</frm:label>
            <frm:input path="username" placeholder="Choose a username (min 3 characters)"/>
            <frm:errors path="username" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="email">Email</frm:label>
            <frm:input path="email" placeholder="you@example.com" type="email"/>
            <frm:errors path="email" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="password">Password</frm:label>
            <frm:password path="password" placeholder="Min 6 characters"/>
            <frm:errors path="password" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="firstName">First Name</frm:label>
            <frm:input path="firstName" placeholder="Enter first name"/>
            <frm:errors path="firstName" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="lastName">Last Name</frm:label>
            <frm:input path="lastName" placeholder="Enter last name (optional)"/>
            <frm:errors path="lastName" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="role">Role</frm:label>
            <frm:select path="role">
                <frm:option value="" label="-- Select Role --"/>
                <c:forEach var="role" items="${roles}">
                    <frm:option value="${role}" label="${role}"/>
                </c:forEach>
            </frm:select>
            <frm:errors path="role" cssClass="field-error"/>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Cancel</a>
            <button type="submit" class="btn btn-success">
                <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-person-plus'}"></i>
                ${submitButtonLabel}
            </button>
        </div>
    </frm:form>

    <p class="text-center text-muted mt-2 text-small">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login">Login here</a>
    </p>

</div>

<%@ include file="../fragments/footer.jsp" %>
