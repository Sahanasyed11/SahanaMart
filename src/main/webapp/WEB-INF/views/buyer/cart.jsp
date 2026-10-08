<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Shopping Cart - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 24px;">Your Shopping Cart</h1>

        <c:if test="${not empty sessionScope.cartError}">
            <div class="alert alert-danger">
                ⚠️ <c:out value="${sessionScope.cartError}"/>
            </div>
            <c:remove var="cartError" scope="session"/>
        </c:if>

        <c:choose>
            <c:when test="${empty cartItems}">
                <div style="text-align: center; padding: 80px 20px; background: white; border-radius: var(--radius-md); border: 1px dashed var(--gray-300);">
                    <div style="font-size: 4rem; margin-bottom: 16px;">🛍️</div>
                    <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 10px;">Your cart is currently empty</h2>
                    <p style="color: var(--gray-500); margin-bottom: 24px;">Browse through our catalog and find items you love!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Start Shopping ➔</a>
                </div>
            </c:when>
            <c:otherwise>
                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 30px; align-items: start;">
                    <!-- Cart Items List -->
                    <div class="card" style="padding: 24px;">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th style="text-align: center;">Qty</th>
                                    <th>Subtotal</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cartItems}">
                                    <tr>
                                        <td>
                                            <div style="display: flex; gap: 14px; align-items: center;">
                                                <img src="<c:out value='${item.product.imageUrl}'/>" alt="" style="width: 55px; height: 55px; object-fit: cover; border-radius: var(--radius-sm); border: 1px solid var(--gray-200);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/products/view?id=${item.productId}" style="font-weight: 700; color: var(--gray-900);">
                                                        <c:out value="${item.product.name}"/>
                                                    </a>
                                                    <div style="font-size: 0.8rem; color: var(--gray-500);"><c:out value="${item.product.category}"/></div>
                                                </div>
                                            </div>
                                        </td>
                                        <td style="font-weight: 600;">₹<c:out value="${item.product.price}"/></td>
                                        <td style="text-align: center;">
                                            <form action="${pageContext.request.contextPath}/cart" method="POST" style="display: inline-flex; align-items: center; gap: 6px;">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="cartItemId" value="${item.id}">
                                                <button type="submit" name="quantity" value="${item.quantity - 1}" class="btn btn-secondary btn-sm" style="padding: 2px 8px;">-</button>
                                                <span style="font-weight: 700; min-width: 20px; text-align: center;"><c:out value="${item.quantity}"/></span>
                                                <button type="submit" name="quantity" value="${item.quantity + 1}" class="btn btn-secondary btn-sm" style="padding: 2px 8px;">+</button>
                                            </form>
                                        </td>
                                        <td style="font-weight: 800; color: var(--gray-900);">₹<c:out value="${item.subtotal}"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin: 0;">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="cartItemId" value="${item.id}">
                                                <button type="submit" class="btn btn-secondary btn-sm" style="color: var(--danger);" title="Remove">🗑️</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Summary Card -->
                    <div class="card" style="padding: 24px;">
                        <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 20px;">Order Summary</h2>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 12px; color: var(--gray-600);">
                            <span>Subtotal</span>
                            <span>₹<c:out value="${cartTotal}"/></span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 16px; color: var(--gray-600);">
                            <span>Shipping</span>
                            <span style="color: var(--success); font-weight: 600;">FREE</span>
                        </div>
                        <hr style="border: none; border-top: 1px solid var(--gray-200); margin: 16px 0;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 24px; font-size: 1.25rem; font-weight: 800; color: var(--gray-900);">
                            <span>Total</span>
                            <span>₹<c:out value="${cartTotal}"/></span>
                        </div>
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-block btn-lg">Proceed to Checkout ➔</a>
                        <div style="text-align: center; margin-top: 14px;">
                            <a href="${pageContext.request.contextPath}/products" style="font-size: 0.9rem;">Continue Shopping</a>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
