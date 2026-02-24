/* ================================================================
   EduPro -- Main JavaScript
   ================================================================
   LEARNING NOTES:

   Where to place <script> tags:
   - <head>   : Blocks page rendering until JS loads. Use 'defer' attribute.
   - </body>  : Loads after HTML is parsed. The traditional safe approach.
   - defer    : <script src="app.js" defer> -- loads async, runs after DOM ready.

   We load this in footer.jsp (before </body>) so the DOM is ready.

   CONCEPT: "Unobtrusive JavaScript"
   - Don't use inline onclick="..." in HTML (mixes JS with HTML).
   - Instead, attach event listeners from an external JS file.
   - Keeps HTML clean and JS maintainable.
   ================================================================ */


/* ================================================================
   1. CONFIRM DELETE
   ================================================================
   Called when user clicks "Delete" button on course list.
   The 'return confirm(...)' pattern:
   - confirm() shows a browser dialog with OK/Cancel
   - Returns true (OK) or false (Cancel)
   - If it returns false, the form submission is CANCELLED
   - This is set via onsubmit="return confirmDelete('...')" on the <form>

   WHY onsubmit instead of onclick?
   - onsubmit fires when the FORM is submitted (covers Enter key too)
   - onclick on button only fires on click (misses keyboard submit)
   ================================================================ */

function confirmDeleteCourse(itemName) {
    return confirm('Are you sure you want to delete "' + itemName + '"?\n\nThis action cannot be undone.');
}


/* ================================================================
   2. AUTO-HIDE FLASH MESSAGES
   ================================================================
   Success/error messages disappear after 5 seconds.

   CONCEPTS USED:
   - document.querySelectorAll() : finds all matching elements (returns NodeList)
   - .forEach()                  : loops over the NodeList
   - setTimeout()                : runs code after a delay (in milliseconds)
   - element.style.transition    : CSS transition applied via JS
   - element.style.opacity       : fade out effect (1 = visible, 0 = invisible)
   - element.remove()            : removes element from the DOM entirely
   ================================================================ */

document.addEventListener('DOMContentLoaded', function () {
    /*
     * DOMContentLoaded fires when the HTML is fully parsed (but images may
     * still be loading). This is the safest time to start manipulating the DOM.
     *
     * Alternative: window.onload -- waits for EVERYTHING (images, CSS, fonts).
     * DOMContentLoaded is faster and usually what you want.
     */

    var messages = document.querySelectorAll('.msg');

    messages.forEach(function (msg) {
        setTimeout(function () {
            msg.style.transition = 'opacity 0.5s ease';
            msg.style.opacity = '0';
            /* After the fade animation completes (500ms), remove from DOM */
            setTimeout(function () {
                msg.remove();
            }, 500);
        }, 5000); /* 5000ms = 5 seconds before fade starts */
    });
});

function confirmDeleteUser(UserName){
    return confirm('Are you sure you want to delete your profile with user Name :"' + UserName + '"?\n\nThis action cannot be undone.');
}
