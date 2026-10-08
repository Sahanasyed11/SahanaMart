<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Secure Checkout - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 900px;">
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 24px;">Complete Your Order</h1>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ⚠️ <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/checkout" method="POST">
            <div style="display: grid; grid-template-columns: 3fr 2fr; gap: 30px; align-items: start;">
                <!-- Shipping & Payment Form -->
                <div>
                    <!-- Step 1: Shipping Address -->
                    <div class="card" style="padding: 24px; margin-bottom: 24px;">
                        <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 16px;">1. Shipping Information</h2>
                        
                        <div class="form-group">
                            <label>Recipient Name</label>
                            <input type="text" class="form-control" value="<c:out value='${user.name}'/>" readonly style="background: var(--gray-50);">
                        </div>

                        <div class="form-group">
                            <label for="shippingAddress">Complete Delivery Address</label>
                            <textarea id="shippingAddress" name="shippingAddress" class="form-control" rows="3" placeholder="House/Flat No, Street, Landmark, City, State, PIN Code" required><c:out value="${user.address}"/></textarea>
                            <small style="color: var(--gray-500); font-size: 0.8rem;">Minimum 10 characters required.</small>
                        </div>
                    </div>

                    <!-- Step 2: Mock Payment Gateway (F5) -->
                    <div class="card" style="padding: 24px;">
                        <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">2. Payment Method</h2>
                        <p style="color: var(--gray-500); font-size: 0.85rem; margin-bottom: 16px;">(Simulated sandbox payment - no real funds charged)</p>

                        <div style="display: flex; flex-direction: column; gap: 12px;">
                            <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius-md); cursor: pointer;">
                                <input type="radio" name="paymentMethod" value="UPI" checked>
                                <span>⚡ <strong>UPI Instant Pay</strong> (Google Pay, PhonePe, Paytm simulation)</span>
                            </label>

                            <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius-md); cursor: pointer;">
                                <input type="radio" name="paymentMethod" value="MOCK_CARD">
                                <span>💳 <strong>Mock Credit / Debit Card</strong> (Visa, Mastercard, RuPay)</span>
                            </label>

                            <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius-md); cursor: pointer;">
                                <input type="radio" name="paymentMethod" value="NET_BANKING">
                                <span>🏦 <strong>Net Banking</strong> (All Major Indian Banks)</span>
                            </label>

                            <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius-md); cursor: pointer;">
                                <input type="radio" name="paymentMethod" value="COD">
                                <span>💵 <strong>Cash on Delivery (COD)</strong></span>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Order Review Card -->
                <div class="card" style="padding: 24px;">
                    <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 16px;">Order Summary</h2>

                    <div style="display: flex; flex-direction: column; gap: 12px; margin-bottom: 20px;">
                        <c:forEach var="item" items="${cartItems}">
                            <div style="display: flex; justify-content: space-between; font-size: 0.9rem;">
                                <span><c:out value="${item.product.name}"/> &times; ${item.quantity}</span>
                                <span style="font-weight: 600;">₹<c:out value="${item.subtotal}"/></span>
                            </div>
                        </c:forEach>
                    </div>

                    <hr style="border: none; border-top: 1px solid var(--gray-200); margin: 16px 0;">

                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px; color: var(--gray-600); font-size: 0.95rem;">
                        <span>Subtotal</span>
                        <span>₹<c:out value="${cartTotal}"/></span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 16px; color: var(--gray-600); font-size: 0.95rem;">
                        <span>Shipping</span>
                        <span style="color: var(--success); font-weight: 600;">FREE</span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 24px; font-size: 1.3rem; font-weight: 800; color: var(--gray-900);">
                        <span>Total Payable</span>
                        <span>₹<c:out value="${cartTotal}"/></span>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg">Place Order & Pay ➔</button>
                    <p style="text-align: center; color: var(--gray-400); font-size: 0.75rem; margin-top: 12px;">🔒 256-Bit SSL Encrypted Mock Checkout</p>
                </div>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
