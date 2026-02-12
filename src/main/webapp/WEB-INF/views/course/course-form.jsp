
<%@ include file="../fragments/header.jsp" %>
	
		<div class="container">
		
		    <h2>${pageHeading}</h2>
			
			<!-- Based on ifEditMode we should have sparate path -->
			<c:choose>
		        <c:when test="${isEditMode}">
		            <c:url value='/courses/${course.id}' var='formActionUrl'/>
		        </c:when>
		        <c:otherwise>
		            <c:url value='/courses' var='formActionUrl'/>
		        </c:otherwise>
		     </c:choose>
			
		    <hr>
		
		    <p class="info">This is <b>form.jsp</b></p>
		
		    <!-- Global validation errors -->
		    <frm:errors path="*" cssClass="error"/>
		
		    <frm:form
		            method="post"
		            modelAttribute="course"
		            action="${formActionUrl}">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		        <div class="form-group" >
		            <%-- <frm:label path="id">Course Id</frm:label> --%>
		            <frm:hidden path="id"/>
		            <frm:errors path="id" cssClass="error"/>
		        </div>
		        
		        <div class="form-group">
		            <frm:label path="title">Course Name</frm:label>
		            <frm:input path="title" placeholder="Java Full Stack"/>
		            <frm:errors path="title" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <frm:label path="description">Description</frm:label>
		            <frm:input path="description" placeholder="Complete Java + Spring"/>
		            <frm:errors path="description" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <frm:label path="durationInHours">Duration (hours)</frm:label>
		            <frm:input path="durationInHours" type="number"/>
		            <frm:errors path="durationInHours" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <frm:label path="fees">Fees</frm:label>
		            <frm:input path="fees" type="number"/>
		            <frm:errors path="fees" cssClass="error"/>
		        </div>
		
		        <div class="form-group">
		            <frm:label path="instructor">Instructor</frm:label>
		            <frm:input path="instructor" placeholder="John Doe"/>
		            <frm:errors path="instructor" cssClass="error"/>
		        </div>
		
		        <div class="actions">
		            <input type="submit" value="${submitButtonLabel}"/>
		        </div>
		
		    </frm:form>
		
		    <br>
		
		    <a href="${pageContext.request.contextPath}/courses">
		        ← Back to Course List
		    </a>
		
		</div>
		
<%@ include file="../fragments/footer.jsp" %>