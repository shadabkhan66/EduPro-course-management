<%--
    ============================================================
    header.jsp -- Shared page header (included by all pages)
    ============================================================
    LEARNING NOTES:

    This file is included via: <%@ include file="../fragments/header.jsp" %>
    This is a COMPILE-TIME include (JSP directive). The content is
    literally copy-pasted into the including JSP before compilation.

    Taglibs declared here are available in EVERY page that includes this.

    EL (Expression Language):  ${variable}   -- outputs model attributes
    JSTL:                      <c:if>, <c:forEach>, <c:choose>
    Spring Form:               <frm:form>, <frm:input>, <frm:errors>
    Spring Security:           <sec:authorize>
    ============================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- viewport meta tag makes the page responsive on mobile devices.
         width=device-width : page width matches the device screen width.
         initial-scale=1.0  : no zoom on load. --%>

    <title>${pageTitle != null ? pageTitle : 'EduPro'}</title>

    <%-- External CSS (instead of inline <style> block) --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <%-- Bootstrap Icons (icon font, not the full Bootstrap framework) --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
</head>
<body>

<%-- ==================== SITE HEADER ==================== --%>
<header class="site-header">
    <div class="header-inner">

        <%-- Brand / Logo --%>
        <a href="${pageContext.request.contextPath}/" class="site-brand">
            <i class="bi bi-mortarboard-fill"></i> EduPro
        </a>

        <%-- Navigation --%>
        <nav class="site-nav">
            <a href="${pageContext.request.contextPath}/">Home</a>
            <a href="${pageContext.request.contextPath}/courses">Courses</a>

            <span class="nav-divider">|</span>

            <%-- Show different links based on auth state.
                 <sec:authorize> checks Spring Security context.
                 isAuthenticated() = user is logged in.
                 isAnonymous()     = user is NOT logged in. --%>

            <sec:authorize access="isAuthenticated()">
                <%-- Logout must be POST (CSRF requires it).
                     We style this <form> + <button> to look like a nav link. --%>
                <form action="${pageContext.request.contextPath}/logout"
                      method="POST" style="display:inline; margin:0;">
                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}" />
                    <button type="submit" class="logout-btn">
                        <i class="bi bi-box-arrow-right"></i> Logout
                    </button>
                </form>
            </sec:authorize>

            <sec:authorize access="isAnonymous()">
                <a href="${pageContext.request.contextPath}/login">Login</a>
                <a href="${pageContext.request.contextPath}/users/register" class="btn btn-primary btn-sm">Register</a>
            </sec:authorize>
        </nav>

    </div>
</header>

<%-- ==================== MAIN CONTENT START ==================== --%>
<main class="site-main">
