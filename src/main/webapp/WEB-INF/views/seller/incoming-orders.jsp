<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Incoming Orders - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="${pageContext.request.contextPath}/seller/dashboard" style="font-size: 0.9rem; font-weight: 600;">« Back to Dashboard</a>
                <h1 style="font-size: 1.8rem; font-weight: 800; margin-top: 4px;">Incoming Customer Orders</h1>
            </div>
        </div>

        <c:if test="${param.updated == 'true'}">
            <div class="alert alert-success">
                ✅ Order status updated successfully!
            </div>
        </c:if>

        <div class="card" style="padding: 24px;">
            <c:choose>
                <c:when test="${empty incomingOrders}">
                    <div style="text-align: center; padding: 60px 20px;">
                        <p style="color: var(--gray-500);">No incoming customer orders found for your listed products yet.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Item</th>
                                <th>Qty</th>
                                <th>Price</th>
                                <th>Subtotal</th>
                                <th>Order Placed</th>
                                <th>Update Workflow Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${incomingOrders}">
                                <tr>
                                    <td><strong>#<c:out value="${item.orderId}"/></strong></td>
                                    <td>
                                        <div style="display: flex; gap: 10px; align-items: center;">
                                            <img src="<c:out value='${item.productImage}'/>" alt="" style="width: 40px; height: 40px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                            <span><c:out value="${item.productName}"/></span>
                                        </div>
                                    </td>
                                    <td><c:out value="${item.quantity}"/></td>
                                    <td>₹<c:out value="${item.unitPrice}"/></td>
                                    <td style="font-weight: 700;">₹<c:out value="${item.subtotal}"/></td>
                                    <td style="font-size: 0.85rem; color: var(--gray-500);"><c:out value="${item.createdAt}"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/seller/orders/status" method="POST" style="display: flex; gap: 8px; margin: 0;">
                                            <input type="hidden" name="orderId" value="${item.orderId}">
                                            <select name="status" class="form-control" style="padding: 4px 8px; font-size: 0.85rem; width: auto;">
                                                <option value="CONFIRMED">Confirmed</option>
                                                <option value="SHIPPED">Shipped</option>
                                                <option value="DELIVERED">Delivered</option>
                                                <option value="CANCELLED">Cancelled</option>
                                            </select>
                                            <button type="submit" class="btn btn-secondary btn-sm">Update</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
