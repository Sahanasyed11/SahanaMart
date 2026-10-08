<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create Account - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 520px;">
        <div class="card" style="margin-top: 20px;">
            <div class="card-body">
                <h2 style="font-size: 1.75rem; font-weight: 800; text-align: center; margin-bottom: 8px;">Create Account</h2>
                <p style="text-align: center; color: var(--gray-500); margin-bottom: 24px;">Join thousands of shoppers and sellers on SahanaMart.</p>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        ⚠️ <c:out value="${errorMessage}"/>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth/register" method="POST">
                    <div class="form-group">
                        <label for="name">Full Name</label>
                        <input type="text" id="name" name="name" class="form-control" placeholder="John Doe" value="<c:out value='${name}'/>" required autofocus>
                        <c:if test="${not empty fieldErrors['name']}">
                            <span style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['name']}"/></span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="john@example.com" value="<c:out value='${email}'/>" required>
                        <c:if test="${not empty fieldErrors['email']}">
                            <span style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['email']}"/></span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="password">Password (minimum 6 characters)</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
                        <c:if test="${not empty fieldErrors['password']}">
                            <span style="color: var(--danger); font-size: 0.8rem;"><c:out value="${fieldErrors['password']}"/></span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="role">Account Role</label>
                        <select id="role" name="role" class="form-control" required>
                            <option value="BUYER" <c:if test="${role == 'BUYER'}">selected</c:if>>Buyer (Shop products, track orders, leave reviews)</option>
                            <option value="SELLER" <c:if test="${role == 'SELLER'}">selected</c:if>>Seller (Create listings, manage stock, view incoming orders)</option>
                        </select>
                        <small style="color: var(--gray-500); font-size: 0.8rem;">Note: Admin accounts are pre-seeded and cannot be created publicly.</small>
                    </div>

                    <div class="form-group">
                        <label for="phone">Phone Number (Optional)</label>
                        <input type="text" id="phone" name="phone" class="form-control" placeholder="9876543210">
                    </div>

                    <div class="form-group">
                        <label for="address">Shipping / Business Address (Optional)</label>
                        <textarea id="address" name="address" class="form-control" placeholder="123 Street, City, State, PIN"></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg">Complete Registration ➔</button>
                </form>

                <div style="text-align: center; margin-top: 24px; font-size: 0.9rem; color: var(--gray-600);">
                    Already have an account? <a href="${pageContext.request.contextPath}/auth/login" style="font-weight: 600;">Sign in here</a>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
