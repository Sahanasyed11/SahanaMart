<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Order Placed Successfully! - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 600px; text-align: center;">
        <div class="card" style="padding: 50px 30px;">
            <div style="font-size: 4rem; margin-bottom: 16px;">🎉</div>
            <h1 style="font-size: 2rem; font-weight: 800; color: var(--gray-900); margin-bottom: 8px;">Order Confirmed!</h1>
            <p style="color: var(--gray-500); margin-bottom: 24px;">Thank you for shopping with SahanaMart. Your order has been received and processed.</p>

            <div style="background: var(--gray-50); border: 1px solid var(--gray-200); border-radius: var(--radius-md); padding: 20px; margin-bottom: 30px; text-align: left;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--gray-600);">Order ID:</span>
                    <strong style="color: var(--primary);">#<c:out value="${param.orderId}"/></strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                    <span style="color: var(--gray-600);">Payment Status:</span>
                    <span class="badge badge-success">COMPLETED</span>
                </div>
                <div style="display: flex; justify-content: space-between;">
                    <span style="color: var(--gray-600);">Order Status:</span>
                    <span class="badge badge-info">CONFIRMED</span>
                </div>
            </div>

            <div style="display: flex; gap: 14px; justify-content: center;">
                <a href="${pageContext.request.contextPath}/orders" class="btn btn-primary btn-lg">View My Orders</a>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-lg">Continue Shopping</a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
