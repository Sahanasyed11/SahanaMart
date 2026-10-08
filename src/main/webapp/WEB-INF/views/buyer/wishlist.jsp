<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Wishlist - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 24px;">Your Wishlist ❤️</h1>

        <c:choose>
            <c:when test="${empty wishlistItems}">
                <div style="text-align: center; padding: 70px 20px; background: white; border-radius: var(--radius-md); border: 1px dashed var(--gray-300);">
                    <div style="font-size: 3.5rem; margin-bottom: 14px;">🤍</div>
                    <h2 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 8px;">Your wishlist is empty</h2>
                    <p style="color: var(--gray-500); margin-bottom: 20px;">Save products here to easily purchase them later!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Discover Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="item" items="${wishlistItems}">
                        <div class="product-card">
                            <div class="product-img-wrapper">
                                <span class="product-category-tag"><c:out value="${item.product.category}"/></span>
                                <img src="<c:out value='${item.product.imageUrl}'/>" alt="" class="product-img" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                            </div>
                            <div class="product-details">
                                <h3 class="product-title">
                                    <a href="${pageContext.request.contextPath}/products/view?id=${item.product.id}"><c:out value="${item.product.name}"/></a>
                                </h3>
                                <div class="product-price-row">
                                    <span class="product-price">₹<c:out value="${item.product.price}"/></span>
                                    <div style="display: flex; gap: 6px;">
                                        <form action="${pageContext.request.contextPath}/wishlist" method="POST" style="margin: 0;">
                                            <input type="hidden" name="action" value="moveToCart">
                                            <input type="hidden" name="productId" value="${item.product.id}">
                                            <button type="submit" class="btn btn-primary btn-sm">Move to Cart</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/wishlist" method="POST" style="margin: 0;">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="productId" value="${item.product.id}">
                                            <button type="submit" class="btn btn-secondary btn-sm" title="Remove">✕</button>
                                        </form>
                                    </div>
                                </div>
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
