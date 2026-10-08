<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Seller Dashboard - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <h1 style="font-size: 1.8rem; font-weight: 800;">Seller Dashboard</h1>
                <p style="color: var(--gray-500);">Manage your store inventory, track customer orders, and view sales performance.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/seller/products/new" class="btn btn-primary">+ Add New Product</a>
            </div>
        </div>

        <!-- Metrics Cards (Feature O3) -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 30px;">
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">ACTIVE LISTINGS</span>
                <div style="font-size: 2rem; font-weight: 800; color: var(--primary); margin-top: 8px;"><c:out value="${totalProducts}"/></div>
            </div>
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">UNITS SOLD</span>
                <div style="font-size: 2rem; font-weight: 800; color: var(--secondary); margin-top: 8px;"><c:out value="${totalUnitsSold}"/></div>
            </div>
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">TOTAL REVENUE</span>
                <div style="font-size: 2rem; font-weight: 800; color: var(--success); margin-top: 8px;">₹<c:out value="${totalRevenue}"/></div>
            </div>
        </div>

        <!-- Quick Links -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
            <div class="card" style="padding: 24px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                    <h2 style="font-size: 1.2rem; font-weight: 700;">My Product Listings</h2>
                    <a href="${pageContext.request.contextPath}/seller/products" style="font-size: 0.9rem;">View All ➔</a>
                </div>
                <table class="table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Stock</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${products}" begin="0" end="4">
                            <tr>
                                <td><c:out value="${p.name}"/></td>
                                <td>₹<c:out value="${p.price}"/></td>
                                <td>
                                    <span class="badge ${p.stockQty > 5 ? 'badge-success' : 'badge-warning'}">
                                        <c:out value="${p.stockQty}"/>
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="card" style="padding: 24px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                    <h2 style="font-size: 1.2rem; font-weight: 700;">Recent Order Items</h2>
                    <a href="${pageContext.request.contextPath}/seller/orders" style="font-size: 0.9rem;">View All ➔</a>
                </div>
                <c:choose>
                    <c:when test="${empty recentOrders}">
                        <p style="color: var(--gray-500); font-size: 0.9rem;">No incoming customer orders yet.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Qty</th>
                                    <th>Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${recentOrders}">
                                    <tr>
                                        <td><c:out value="${item.productName}"/></td>
                                        <td><c:out value="${item.quantity}"/></td>
                                        <td>₹<c:out value="${item.subtotal}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
