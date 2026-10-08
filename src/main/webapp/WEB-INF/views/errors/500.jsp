<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Server Error - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 600px; text-align: center; padding: 60px 20px;">
        <div style="font-size: 5rem; font-weight: 800; color: var(--danger); margin-bottom: 12px;">500</div>
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 12px;">Something went wrong</h1>
        <p style="color: var(--gray-500); margin-bottom: 24px;">An internal server error occurred while processing your request. Our engineering team has been notified.</p>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">Return to Homepage</a>
            <a href="mailto:support@sahanamart.com" class="btn btn-secondary btn-lg">Contact Support</a>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
