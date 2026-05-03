
## new challanges

---

1. when i changed User to UserEntity if have to edit all over my project 
    is there amy simple method

2. refactory code when by changin file name is not joke i have to practice

3. currently not using Mapper 

4. ❓ How do I copy just one file from main into my feature branch?
    
    First switch to your feature branch, then checkout the file from main:
    
    git checkout feature/rest-api
    git checkout main -- new_challanges.md
    git add new_challanges.md
    git commit -m "Add file from main"
    ❓ Why do I need to switch to my feature branch first?
    
    Because Git applies the file change to your current branch.
    If you stay on main, you won’t be updating your feature branch.
    
    ❓ What does this command mean?
    git checkout main -- new_challanges.md
    main → source branch
    -- → separator between branch and file
    new_challanges.md → the file you want
    
5. @PrePersist for enrolledCount
   @PrePersist
   protected void incrementEnrolledCount() {
   this.enrolledCount = this.enrolledCount + 1;
   }
   @PrePersist runs only when the entity is first saved, not when users enroll.
   So every new course will start with enrolledCount = 1, which is incorrect.
   Enrollment is a business operation, not a persistence lifecycle concern.

6. @ManyToMany(mappedBy = "course")
   private List<UserEntity> enrolledUsers;
    here  mappedBy = "course" looks wrong (should likely be "courses" in UserEntity)
7. Avoid primitive for nullable DB fields

8. @Column is used for basic fields (like String, int, etc.)
   @JoinColumn is used for relationships (@ManyToOne, @OneToOne, etc.)

    👉 When you use @ManyToOne, the column is defined by @JoinColumn only
    So adding @Column here will either:
    
    be ignored, or
    cause mapping exceptions depending on the JPA provider

9.  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_INSTRUCTOR")
    private UserEntity instructor;
    ✅ This is technically valid, but:
    The column name COURSE_INSTRUCTOR is misleading
    It actually stores a foreign key (likely a numeric ID), not a name
    👉 Better:
    @JoinColumn(name = "INSTRUCTOR_ID")

10. decision making 
    like 
    1. Should I use unidirectional or bidirectional mapping?
    2. If I use mapping, which side should be the owning side?
       Should User own it or Role?
       Who controls updates to the relationship?
    3. Should I use @ManyToMany or a join table / join entity?
       When is @ManyToMany okay?
       When should I create a separate USER_ROLE table?
       When should that table become a full entity (UserRole)?
    4. 
    