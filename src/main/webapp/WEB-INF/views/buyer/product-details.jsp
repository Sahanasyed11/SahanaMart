<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${product.name} - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <a href="${pageContext.request.contextPath}/products" style="display: inline-block; margin-bottom: 20px; font-weight: 600;">« Back to Products</a>

        <c:if test="${param.reviewed == 'true'}">
            <div class="alert alert-success">
                ⭐ Thank you! Your verified product review has been submitted.
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.reviewError}">
            <div class="alert alert-danger">
                ⚠️ <c:out value="${sessionScope.reviewError}"/>
            </div>
            <c:remove var="reviewError" scope="session"/>
        </c:if>

        <div class="card" style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px; padding: 40px; margin-bottom: 40px;">
            <div>
                <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" style="width: 100%; max-height: 440px; object-fit: contain; border-radius: var(--radius-md); background: var(--gray-50); border: 1px solid var(--gray-200);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
            </div>

            <div>
                <span class="badge badge-info" style="margin-bottom: 12px; display: inline-block;"><c:out value="${product.category}"/></span>
                <h1 style="font-size: 2rem; font-weight: 800; margin-bottom: 12px; color: var(--gray-900);"><c:out value="${product.name}"/></h1>
                
                <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                    <span style="color: var(--accent); font-weight: 700; font-size: 1.1rem;">
                        ⭐ <c:out value="${product.averageRating > 0 ? String.format('%.1f', product.averageRating) : 'Not rated'}"/> / 5.0
                    </span>
                    <span style="color: var(--gray-400);">•</span>
                    <span style="color: var(--gray-500);"><c:out value="${product.reviewCount}"/> customer reviews</span>
                    <span style="color: var(--gray-400);">•</span>
                    <span style="color: var(--gray-600); font-size: 0.9rem;">Sold by: <strong><c:out value="${product.sellerName}"/></strong></span>
                </div>

                <div style="font-size: 2.2rem; font-weight: 800; color: var(--gray-900); margin-bottom: 20px;">
                    ₹<c:out value="${product.price}"/>
                </div>

                <p style="color: var(--gray-600); line-height: 1.7; margin-bottom: 30px; font-size: 1rem;">
                    <c:out value="${product.description}"/>
                </p>

                <div style="margin-bottom: 30px;">
                    <c:choose>
                        <c:when test="${product.stockQty > 0}">
                            <span class="badge badge-success" style="font-size: 0.9rem; padding: 6px 12px;">In Stock (<c:out value="${product.stockQty}"/> available)</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-warning" style="font-size: 0.9rem; padding: 6px 12px;">Out of Stock</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${product.stockQty > 0}">
                    <form action="${pageContext.request.contextPath}/cart" method="POST" style="display: flex; gap: 14px; margin-bottom: 20px;">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="number" name="quantity" value="1" min="1" max="${product.stockQty}" class="form-control" style="width: 90px; text-align: center;">
                        <button type="submit" class="btn btn-primary btn-lg" style="flex: 1;">Add to Cart 🛍️</button>
                    </form>
                </c:if>

                <!-- Wishlist Toggle (O1) -->
                <form action="${pageContext.request.contextPath}/wishlist" method="POST" style="margin: 0;">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.id}">
                    <button type="submit" class="btn btn-secondary btn-block">Add to Wishlist ❤️</button>
                </form>
            </div>
        </div>

        <!-- Reviews & Star Ratings Section (F8) -->
        <div class="card" style="padding: 30px;">
            <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 24px;">Customer Reviews & Ratings</h2>

            <!-- Add Review Form (Only for buyers who purchased) -->
            <c:choose>
                <c:when test="${canReview}">
                    <div style="background: var(--gray-50); padding: 24px; border-radius: var(--radius-md); border: 1px solid var(--gray-200); margin-bottom: 30px;">
                        <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 14px;">Leave a Verified Review</h3>
                        <form action="${pageContext.request.contextPath}/reviews" method="POST">
                            <input type="hidden" name="productId" value="${product.id}">
                            <div class="form-group">
                                <label for="rating">Rating</label>
                                <select name="rating" id="rating" class="form-control" style="max-width: 200px;" required>
                                    <option value="5">⭐⭐⭐⭐⭐ (5 - Excellent)</option>
                                    <option value="4">⭐⭐⭐⭐ (4 - Very Good)</option>
                                    <option value="3">⭐⭐⭐ (3 - Good)</option>
                                    <option value="2">⭐⭐ (2 - Fair)</option>
                                    <option value="1">⭐ (1 - Poor)</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="comment">Your Feedback</label>
                                <textarea name="comment" id="comment" class="form-control" placeholder="Share your experience with this item..." required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Submit Review</button>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: var(--gray-500); font-size: 0.9rem; margin-bottom: 24px; font-style: italic;">
                        ℹ️ Verified reviews can only be submitted by buyers who have purchased and received this product.
                    </p>
                </c:otherwise>
            </c:choose>

            <!-- Reviews List -->
            <c:choose>
                <c:when test="${empty reviews}">
                    <p style="color: var(--gray-500);">No reviews yet. Be the first to buy and review this product!</p>
                </c:when>
                <c:otherwise>
                    <div style="display: flex; flex-direction: column; gap: 16px;">
                        <c:forEach var="r" items="${reviews}">
                            <div style="padding: 16px; border: 1px solid var(--gray-200); border-radius: var(--radius-md); background: white;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                    <strong><c:out value="${r.userName}"/></strong>
                                    <span style="color: var(--accent);">
                                        <c:forEach begin="1" end="${r.rating}">⭐</c:forEach>
                                    </span>
                                </div>
                                <p style="color: var(--gray-700); font-size: 0.95rem; margin-bottom: 6px;"><c:out value="${r.comment}"/></p>
                                <small style="color: var(--gray-400);"><c:out value="${r.createdAt}"/></small>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
