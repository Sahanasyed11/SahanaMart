<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Order #${order.id} - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 800px;">
        <a href="${pageContext.request.contextPath}/orders" style="display: inline-block; margin-bottom: 20px; font-weight: 600;">« Back to All Orders</a>

        <div class="card" style="padding: 30px;">
            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--gray-200); padding-bottom: 20px; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 1.6rem; font-weight: 800;">Order #<c:out value="${order.id}"/></h1>
                    <span style="color: var(--gray-500); font-size: 0.9rem;">Placed on: <c:out value="${order.createdAt}"/></span>
                </div>
                <div>
                    <span class="badge 
                        <c:choose>
                            <c:when test="${order.status == 'DELIVERED'}">badge-success</c:when>
                            <c:when test="${order.status == 'SHIPPED'}">badge-info</c:when>
                            <c:when test="${order.status == 'CONFIRMED'}">badge-info</c:when>
                            <c:when test="${order.status == 'CANCELLED'}">badge-danger</c:when>
                            <c:otherwise>badge-warning</c:otherwise>
                        </c:choose>
                    " style="font-size: 0.9rem; padding: 6px 12px;">
                        <c:out value="${order.status}"/>
                    </span>
                </div>
            </div>

            <!-- Workflow Progress tracker (O2) -->
            <div style="display: flex; justify-content: space-between; margin-bottom: 30px; padding: 20px; background: var(--gray-50); border-radius: var(--radius-md);">
                <div style="text-align: center;">
                    <div style="font-size: 1.5rem;">📝</div>
                    <strong style="font-size: 0.85rem; color: var(--primary);">Pending</strong>
                </div>
                <div style="align-self: center; flex: 1; height: 2px; background: var(--primary); margin: 0 10px;"></div>
                <div style="text-align: center;">
                    <div style="font-size: 1.5rem;">✅</div>
                    <strong style="font-size: 0.85rem; color: ${order.status != 'PENDING' ? 'var(--primary)' : 'var(--gray-400)'};">Confirmed</strong>
                </div>
                <div style="align-self: center; flex: 1; height: 2px; background: ${order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'var(--primary)' : 'var(--gray-300)'}; margin: 0 10px;"></div>
                <div style="text-align: center;">
                    <div style="font-size: 1.5rem;">🚚</div>
                    <strong style="font-size: 0.85rem; color: ${order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'var(--primary)' : 'var(--gray-400)'};">Shipped</strong>
                </div>
                <div style="align-self: center; flex: 1; height: 2px; background: ${order.status == 'DELIVERED' ? 'var(--primary)' : 'var(--gray-300)'}; margin: 0 10px;"></div>
                <div style="text-align: center;">
                    <div style="font-size: 1.5rem;">🎁</div>
                    <strong style="font-size: 0.85rem; color: ${order.status == 'DELIVERED' ? 'var(--primary)' : 'var(--gray-400)'};">Delivered</strong>
                </div>
            </div>

            <!-- Shipping & Payment Details -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 30px;">
                <div style="padding: 16px; border: 1px solid var(--gray-200); border-radius: var(--radius-md);">
                    <h3 style="font-size: 0.95rem; font-weight: 700; margin-bottom: 8px; color: var(--gray-700);">Shipping Address</h3>
                    <p style="color: var(--gray-600); font-size: 0.9rem; line-height: 1.5;"><c:out value="${order.shippingAddress}"/></p>
                </div>
                <div style="padding: 16px; border: 1px solid var(--gray-200); border-radius: var(--radius-md);">
                    <h3 style="font-size: 0.95rem; font-weight: 700; margin-bottom: 8px; color: var(--gray-700);">Payment Details</h3>
                    <p style="color: var(--gray-600); font-size: 0.9rem; line-height: 1.5;">
                        Method: <strong><c:out value="${order.paymentMethod}"/></strong><br>
                        Status: <span class="badge badge-success"><c:out value="${order.paymentStatus}"/></span>
                    </p>
                </div>
            </div>

            <!-- Items Table -->
            <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 14px;">Purchased Items</h3>
            <table class="table" style="margin-bottom: 24px;">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Unit Price</th>
                        <th style="text-align: center;">Quantity</th>
                        <th style="text-align: right;">Total</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${order.items}">
                        <tr>
                            <td>
                                <div style="display: flex; gap: 12px; align-items: center;">
                                    <img src="<c:out value='${item.productImage}'/>" alt="" style="width: 45px; height: 45px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                    <div>
                                        <a href="${pageContext.request.contextPath}/products/view?id=${item.productId}" style="font-weight: 600; color: var(--gray-900);">
                                            <c:out value="${item.productName}"/>
                                        </a>
                                    </div>
                                </div>
                            </td>
                            <td>₹<c:out value="${item.unitPrice}"/></td>
                            <td style="text-align: center;"><c:out value="${item.quantity}"/></td>
                            <td style="text-align: right; font-weight: 700;">₹<c:out value="${item.subtotal}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div style="text-align: right; font-size: 1.3rem; font-weight: 800; color: var(--gray-900);">
                Grand Total: ₹<c:out value="${order.totalAmount}"/>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
