Learned the proper use of redirect and forword  use prg pattern for double posting and modularity 
With RedirectAttributes 



Is there any thing i shoul.know while propagating exception from  two handler mathods while redirection and forwording 


Remember there is difference between @NotNull and @NotBlank
Can create problem during validation


How to validate uniqness by comparing what we already present in DB
validating uniqueness, like ensuring that a course title is not already in the database before saving. In Spring MVC with Bean Validation, this is not automatic, because @Valid and standard annotations (@NotNull, @NotBlank) only handle simple in-memory checks.


Doubt using same page for editing and registering course which can lead to validation problem 
when I click submit , it sends POST request that handler menthod which contains validation log to check if 
couse with same Title already present or not but during editing it well obviousely be present duing edit 
so what should I do 
should I take saperate handler or change invalidation to check id is being pass through hidden form field which means we are editing in this case we validation should pass

🚨 FLOW 2: UPDATE (Existing Course)
❌ What you DID (problematic)

Form (id = 100040)
        ↓
CourseVO (id = 100040, version = null)
        ↓
new CourseEntity()
        ↓
BeanUtils.copyProperties()
        ↓
Entity(id=100040, version=null)
        ↓
save()
        ↓
💥 ERROR
Hibernate says:
	“You gave me an entity that looks old (has ID)
	but you erased its memory (version = null).
	I refuse.”


	@Override
	public String registerNewCourse(@Valid CourseVO courseVo) {
		
		if(courseVo.getId() != null) {
			return this.courseRepository.save(convertVOToEntityForUpdate(courseVo)).getTitle();
			
		}
		String savedCourseName = this.courseRepository.save(convertVOToEntity(courseVo)).getTitle();
		return savedCourseName;
	}
	
	private CourseEntity convertVOToEntity(CourseVO courseVO) {
		CourseEntity courseEntity = new CourseEntity();
		BeanUtils.copyProperties(courseVO, courseEntity);
		return courseEntity;
	}
	
	private CourseEntity convertVOToEntityForUpdate(CourseVO courseVO) {
		CourseEntity courseEntity = this.courseRepository
				.findById(courseVO.getId())
				.orElseThrow(() -> new CourseNotFoundException("Course with ID " + courseVO.getId() + " not found"));
		BeanUtils.copyProperties(courseVO, courseEntity, "id", "createdBy", "createdDate", "version");
		return courseEntity;
	}
==============================

DispatcherServlet HandlerMapping get confused by  "/courses/add " mapping  with POST method
Instead And called  @PostMapping("/{courseId}") -> /courses/10000

First I was forwording to GET method which is wrong 


We need to manually send and CSRF token in every form which includes the http://methods other than git so I think there are other better options than jSP or we should use some kind of tag or we have to include the hidden field in the form to send an CSRF tokens details or use thymeleaf 


===================

We have to add jar for 


<sec:authorize access="isAuthenticated()">
<form action="${pageContext.request.contextPath}/logout" method="POST" style="display:inline;">
<input type="hidden"
name="${_csrf.parameterName}"
value="${_csrf.token}" />
<button type="submit" class="logout-btn">LogOut</button>
</form>
</sec:authorize>
<sec:authorize access="isAnonymous()">
<a href="${pageContext.request.contextPath}/login">Login</a>
</sec:authorize>


===============



.logout( logoutConfig -> logoutConfig
	.logoutUrl("/logout")
	  .logoutSuccessHandler((request, response, authentication) -> {
			// Set flash attribute for logout message
			request.getSession().setAttribute("logoutMessage", "You have been logged out successfully.");
			response.sendRedirect("/");
		})
	.permitAll()


public interface LogoutSuccessHandler {

	void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException;

}


========================

Problem - I noticed my login save credencials(username and password) are coming in register form with auto filled username and password even thoulght I cant see password

========

❓ “What challenges did you face while implementing user registration with validation and uniqueness checks in Spring MVC?”

	✅ Best Answer (Structured & Professional)
	One of the main challenges I faced was handling multiple validation layers properly — including Bean Validation, uniqueness checks, error message resolution, and displaying errors correctly in a JSP form without causing runtime exceptions.
	There were three key challenges:
	
	1️⃣ Handling Multiple Unique Field Validations
	In my registration form, both username and email had unique constraints.
	Initially, I implemented uniqueness checks in the service layer like this:
	
	if(userRepository.existsByEmail(user.getEmail())) {
    throw new EmailAlreadyExistsException("Email already exists");
}
	if(userRepository.existsByUsername(user.getUsername())) {
    throw new UserNameAlreadyExists("Username already exists");
}
	🔎 Problem
	If both username and email already existed:
		○ Only the first check triggered.
		○ The method stopped execution after throwing the first exception.
		○ The user saw only one error at a time.
	This created poor user experience.
	✅ Final Solution (Best Practice)
	Instead of throwing exceptions, I handled uniqueness validation using BindingResult.rejectValue() in the controller (or via a custom validator).
	
	if(userRepository.existsByEmail(user.getEmail())) {
    result.rejectValue("email", null, "Email already exists");
}
	if(userRepository.existsByUsername(user.getUsername())) {
    result.rejectValue("username", null, "Username already exists");
}
	This allowed:
		○ Both errors to be displayed simultaneously
		○ Cleaner validation flow
		○ No unnecessary exceptions
		○ Better separation of concerns
	

==========

Used Transectional properly and readonly= True
