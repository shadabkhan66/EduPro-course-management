<%@ include file="../fragments/header.jsp" %>

<div class="container container-narrow">

    <h2 class="page-title text-center">${pageHeading}</h2>
    <p class="page-subtitle text-center">
        ${isEditMode ? 'Update course information' : 'Fill in the details to create a new course'}
    </p>
    <hr>

    <%--
        Dynamic form action URL:
        - CREATE mode: POST /courses
        - EDIT mode:   POST /courses/{id}
        <c:url> automatically prepends the context path.
    --%>
    <c:choose>
        <c:when test="${isEditMode}">
            <c:url value='/courses/${course.id}' var='formActionUrl'/>
        </c:when>
        <c:otherwise>
            <c:url value='/courses' var='formActionUrl'/>
        </c:otherwise>
    </c:choose>

    <%-- Global validation errors (shown above the form) --%>
    <frm:errors path="*" cssClass="form-errors"/>

    <%--
        <frm:form> is Spring's form tag. It:
        1. Binds form fields to the 'course' model attribute
        2. Auto-populates fields with existing values (for edit)
        3. Works with BindingResult for validation errors

        modelAttribute="course" MUST match:
        - @ModelAttribute("course") in controller
        - model.addAttribute("course", ...) in GET handler
    --%>
    <frm:form method="post"
              modelAttribute="course"
              action="${formActionUrl}">

        <%-- CSRF token --%>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <%-- Hidden ID field (null for create, populated for edit) --%>
        <frm:hidden path="id"/>

        <div class="form-group">
            <frm:label path="title">Course Title</frm:label>
            <frm:input path="title" placeholder="e.g. Java Full Stack"/>
            <frm:errors path="title" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="description">Description</frm:label>
            <frm:input path="description" placeholder="e.g. Complete Java + Spring Boot"/>
            <frm:errors path="description" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="durationInHours">Duration (hours)</frm:label>
            <frm:input path="durationInHours" type="number" placeholder="e.g. 40"/>
            <frm:errors path="durationInHours" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="fees">Fees</frm:label>
            <frm:input path="fees" type="number" placeholder="e.g. 5000 (leave empty for free)"/>
            <frm:errors path="fees" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <frm:label path="instructor">Instructor</frm:label>
            <frm:input path="instructor" placeholder="e.g. John Doe"/>
            <frm:errors path="instructor" cssClass="field-error"/>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline">Cancel</a>
            <button type="submit" class="btn btn-primary">
                <i class="bi ${isEditMode ? 'bi-pencil-square' : 'bi-plus-circle'}"></i>
                ${submitButtonLabel}
            </button>
        </div>

    </frm:form>

</div>

<%@ include file="../fragments/footer.jsp" %>
