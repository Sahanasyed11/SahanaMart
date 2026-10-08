<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin Console - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <h1 style="font-size: 1.8rem; font-weight: 800;">⚡ System Administration Console</h1>
                <p style="color: var(--gray-500);">Platform moderation, user management, and global sales metrics.</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Manage Users</a>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Moderate Products</a>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-primary">Manage All Orders</a>
            </div>
        </div>

        <!-- Global Metrics Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 30px;">
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">REGISTERED USERS</span>
                <div style="font-size: 2.2rem; font-weight: 800; color: var(--primary); margin-top: 8px;"><c:out value="${totalUsers}"/></div>
            </div>
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">TOTAL LISTINGS</span>
                <div style="font-size: 2.2rem; font-weight: 800; color: var(--secondary); margin-top: 8px;"><c:out value="${totalProducts}"/></div>
            </div>
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">ORDERS PLACED</span>
                <div style="font-size: 2.2rem; font-weight: 800; color: var(--accent); margin-top: 8px;"><c:out value="${totalOrders}"/></div>
            </div>
            <div class="card" style="padding: 24px;">
                <span style="font-size: 0.85rem; color: var(--gray-500); font-weight: 600;">TOTAL PLATFORM REVENUE</span>
                <div style="font-size: 2.2rem; font-weight: 800; color: var(--success); margin-top: 8px;">₹<c:out value="${totalRevenue}"/></div>
            </div>
        </div>

        <!-- Recent System Orders -->
        <div class="card" style="padding: 24px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h2 style="font-size: 1.25rem; font-weight: 700;">Recent Platform Orders</h2>
                <a href="${pageContext.request.contextPath}/admin/orders" style="font-size: 0.9rem;">View All Orders ➔</a>
            </div>

            <table class="table">
                <thead>
                    <tr>
                        <th>Order #</th>
                        <th>Customer</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Placed On</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${recentOrders}">
                        <tr>
                            <td><strong>#<c:out value="${o.id}"/></strong></td>
                            <td><c:out value="${o.buyerName}"/> (<c:out value="${o.buyerEmail}"/>)</td>
                            <td style="font-weight: 700;">₹<c:out value="${o.totalAmount}"/></td>
                            <td>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test="${o.status == 'DELIVERED'}">badge-success</c:when>
                                        <c:when test="${o.status == 'CANCELLED'}">badge-danger</c:when>
                                        <c:otherwise>badge-info</c:otherwise>
                                    </c:choose>
                                ">
                                    <c:out value="${o.status}"/>
                                </span>
                            </td>
                            <td style="color: var(--gray-500); font-size: 0.85rem;"><c:out value="${o.createdAt}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
