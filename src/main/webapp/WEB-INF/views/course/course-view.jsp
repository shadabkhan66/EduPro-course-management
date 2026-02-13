<%@ include file="../fragments/header.jsp" %>

<div class="container">

    <h2>Course Details</h2>

    <c:if test="${not empty course}">

        <div class="form-group">
            <label>Title:</label>
            <p>${course.title}</p>
        </div>

        <div class="form-group">
            <label>Description:</label>
            <p>${course.description}</p>
        </div>

        <div class="form-group">
            <label>Duration (Hours):</label>
            <p>${course.durationInHours}</p>
        </div>

        <div class="form-group">
            <label>Fees:</label>
            <p>₹ ${course.fees}</p>
        </div>

        <div class="form-group">
            <label>Instructor:</label>
            <p>${course.instructor}</p>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/courses">
                <input type="button" value="Back to List"/>
            </a>
        </div>

    </c:if>

    <c:if test="${empty course}">
        <p class="error">Course not found.</p>
    </c:if>

</div>

<%@ include file="../fragments/footer.jsp" %>
