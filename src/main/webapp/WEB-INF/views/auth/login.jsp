<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Login - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 480px;">
        <div class="card" style="margin-top: 20px;">
            <div class="card-body">
                <h2 style="font-size: 1.75rem; font-weight: 800; text-align: center; margin-bottom: 8px;">Welcome Back</h2>
                <p style="text-align: center; color: var(--gray-500); margin-bottom: 24px;">Log in to access your cart, orders, or seller portal.</p>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        ⚠️ <c:out value="${errorMessage}"/>
                    </div>
                </c:if>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        ✅ <c:out value="${successMessage}"/>
                    </div>
                </c:if>

                <c:if test="${param.loggedOut == 'true'}">
                    <div class="alert alert-info">
                        ℹ️ You have been securely logged out.
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth/login" method="POST">
                    <input type="hidden" name="returnUrl" value="<c:out value='${param.returnUrl}'/>">

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="name@example.com" value="<c:out value='${email}'/>" required autofocus>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 10px;">Sign In ➔</button>
                </form>

                <div style="text-align: center; margin-top: 24px; font-size: 0.9rem; color: var(--gray-600);">
                    Don't have an account? <a href="${pageContext.request.contextPath}/auth/register" style="font-weight: 600;">Sign up as Buyer or Seller</a>
                </div>


            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
