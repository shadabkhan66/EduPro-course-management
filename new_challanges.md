
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
    
