<%@ include file="../fragments/header.jsp" %>

<div class="container">

    <div class="details-card">
        <h2>Course Details</h2>

        <c:if test="${not empty course}">

            <div class="detail-row">
                <span class="label">Title</span>
                <span class="value">${course.title}</span>
            </div>

            <div class="detail-row">
                <span class="label">Description</span>
                <span class="value">${course.description}</span>
            </div>

            <div class="detail-row">
                <span class="label">Duration</span>
                <span class="value">${course.durationInHours} Hours</span>
            </div>

            <div class="detail-row">
                <span class="label">Fees</span>
                <span class="value">₹ ${course.fees}</span>
            </div>

            <div class="detail-row">
                <span class="label">Instructor</span>
                <span class="value">${course.instructor}</span>
            </div>

            <div class="actions">
                <a href="${pageContext.request.contextPath}/courses">
                    <button type="button">Back to List</button>
                </a>
            </div>

        </c:if>

        <c:if test="${empty course}">
            <p class="error">Course not found.</p>
        </c:if>

    </div>

</div>

<%@ include file="../fragments/footer.jsp" %>
