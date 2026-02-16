<%--
    ============================================================
    footer.jsp -- Shared page footer (included by all pages)
    ============================================================
    LEARNING NOTES:

    This closes the <main> tag opened in header.jsp.
    The <script> tag is placed here (before </body>) so the DOM
    is fully parsed before JavaScript runs. This is the traditional
    approach. Modern alternative: use 'defer' attribute in <head>.
    ============================================================
--%>

<%-- ==================== MAIN CONTENT END ==================== --%>
</main>

<%-- ==================== FOOTER ==================== --%>
<footer class="site-footer">
    <p>EduPro Campus Management &copy; 2025 &mdash; Built with Spring Boot, JPA, Spring Security</p>
</footer>

<%-- JavaScript loaded at end of body (DOM is ready by now) --%>
<script src="${pageContext.request.contextPath}/js/app.js"></script>

</body>
</html>
