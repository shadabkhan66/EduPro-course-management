<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .error-container {
            text-align: center;
            margin-top: 100px;
        }
        .error-code {
            font-size: 72px;
            font-weight: bold;
            color: #dc3545;
        }
        .error-message {
            font-size: 22px;
            margin-top: 10px;
        }
        .error-description {
            margin-top: 15px;
            color: #6c757d;
        }
        .home-btn {
            display: inline-block;
            margin-top: 25px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .home-btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="error-container">
    <div class="error-code">404</div>

    <div class="error-message">
        Oops! The page you are looking for does not exist.
    </div>

    <div class="error-description">
        The URL might be incorrect or the resource may have been removed.
    </div>

    <a href="${pageContext.request.contextPath}/" class="home-btn">
        Return to Home
    </a>
</div>

</body>
</html>