<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page Not Found - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 600px; text-align: center; padding: 60px 20px;">
        <div style="font-size: 5rem; font-weight: 800; color: var(--primary); margin-bottom: 12px;">404</div>
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 12px;">Page Not Found</h1>
        <p style="color: var(--gray-500); margin-bottom: 24px;">The page you are looking for might have been moved, removed, or never existed.</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">Return to Homepage</a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-lg">Explore Catalog</a>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
