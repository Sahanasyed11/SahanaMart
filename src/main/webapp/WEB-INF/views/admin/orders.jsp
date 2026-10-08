<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Orders - Admin - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" style="font-size: 0.9rem; font-weight: 600;">« Back to Admin Console</a>
                <h1 style="font-size: 1.8rem; font-weight: 800; margin-top: 4px;">System Orders Oversight</h1>
            </div>
        </div>

        <c:if test="${param.updated == 'true'}">
            <div class="alert alert-success">
                ✅ Order status updated successfully.
            </div>
        </c:if>

        <div class="card" style="padding: 24px;">
            <table class="table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Buyer</th>
                        <th>Total</th>
                        <th>Payment</th>
                        <th>Status</th>
                        <th>Placed On</th>
                        <th>Override Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td><strong>#<c:out value="${o.id}"/></strong></td>
                            <td>
                                <div><c:out value="${o.buyerName}"/></div>
                                <small style="color: var(--gray-500);"><c:out value="${o.buyerEmail}"/></small>
                            </td>
                            <td style="font-weight: 700;">₹<c:out value="${o.totalAmount}"/></td>
                            <td>
                                <div><c:out value="${o.paymentMethod}"/></div>
                                <span class="badge badge-success"><c:out value="${o.paymentStatus}"/></span>
                            </td>
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
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/orders/status" method="POST" style="display: flex; gap: 6px; margin: 0;">
                                    <input type="hidden" name="orderId" value="${o.id}">
                                    <select name="status" class="form-control" style="padding: 4px 6px; font-size: 0.85rem; width: auto;">
                                        <option value="PENDING" <c:if test="${o.status == 'PENDING'}">selected</c:if>>Pending</option>
                                        <option value="CONFIRMED" <c:if test="${o.status == 'CONFIRMED'}">selected</c:if>>Confirmed</option>
                                        <option value="SHIPPED" <c:if test="${o.status == 'SHIPPED'}">selected</c:if>>Shipped</option>
                                        <option value="DELIVERED" <c:if test="${o.status == 'DELIVERED'}">selected</c:if>>Delivered</option>
                                        <option value="CANCELLED" <c:if test="${o.status == 'CANCELLED'}">selected</c:if>>Cancelled</option>
                                    </select>
                                    <button type="submit" class="btn btn-secondary btn-sm">Save</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
