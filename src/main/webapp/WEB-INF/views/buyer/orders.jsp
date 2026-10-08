<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Orders - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 24px;">Order History</h1>

        <c:choose>
            <c:when test="${empty orders}">
                <div style="text-align: center; padding: 70px 20px; background: white; border-radius: var(--radius-md); border: 1px dashed var(--gray-300);">
                    <div style="font-size: 3.5rem; margin-bottom: 14px;">📦</div>
                    <h2 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 8px;">No orders placed yet</h2>
                    <p style="color: var(--gray-500); margin-bottom: 20px;">Your purchase history will appear here once you place an order.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping</a>
                </div>
            </c:when>
            <c:otherwise>
                <div style="display: flex; flex-direction: column; gap: 20px;">
                    <c:forEach var="o" items="${orders}">
                        <div class="card" style="padding: 24px;">
                            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--gray-200); padding-bottom: 14px; margin-bottom: 16px;">
                                <div>
                                    <span style="font-size: 0.85rem; color: var(--gray-500);">Order Placed: <c:out value="${o.createdAt}"/></span>
                                    <div style="font-weight: 800; font-size: 1.1rem; color: var(--gray-900);">Order #<c:out value="${o.id}"/></div>
                                </div>
                                <div style="display: flex; align-items: center; gap: 14px;">
                                    <span class="badge 
                                        <c:choose>
                                            <c:when test="${o.status == 'DELIVERED'}">badge-success</c:when>
                                            <c:when test="${o.status == 'SHIPPED'}">badge-info</c:when>
                                            <c:when test="${o.status == 'CONFIRMED'}">badge-info</c:when>
                                            <c:when test="${o.status == 'CANCELLED'}">badge-danger</c:when>
                                            <c:otherwise>badge-warning</c:otherwise>
                                        </c:choose>
                                    ">
                                        <c:out value="${o.status}"/>
                                    </span>
                                    <a href="${pageContext.request.contextPath}/orders/view?id=${o.id}" class="btn btn-secondary btn-sm">View Details</a>
                                </div>
                            </div>

                            <!-- Order Items Summary -->
                            <div style="display: flex; flex-direction: column; gap: 12px;">
                                <c:forEach var="item" items="${o.items}">
                                    <div style="display: flex; justify-content: space-between; align-items: center;">
                                        <div style="display: flex; gap: 12px; align-items: center;">
                                            <img src="<c:out value='${item.productImage}'/>" alt="" style="width: 45px; height: 45px; object-fit: cover; border-radius: var(--radius-sm); border: 1px solid var(--gray-200);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                            <div>
                                                <a href="${pageContext.request.contextPath}/products/view?id=${item.productId}" style="font-weight: 600; color: var(--gray-800);">
                                                    <c:out value="${item.productName}"/>
                                                </a>
                                                <div style="font-size: 0.8rem; color: var(--gray-500);">Qty: <c:out value="${item.quantity}"/> &times; ₹<c:out value="${item.unitPrice}"/></div>
                                            </div>
                                        </div>
                                        <div style="font-weight: 700;">₹<c:out value="${item.subtotal}"/></div>
                                    </div>
                                </c:forEach>
                            </div>

                            <div style="text-align: right; margin-top: 16px; border-top: 1px solid var(--gray-100); padding-top: 12px; font-weight: 800; font-size: 1.1rem;">
                                Total: ₹<c:out value="${o.totalAmount}"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
