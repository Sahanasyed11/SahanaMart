<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="SahanaMart - Modern E-Commerce Platform" scope="request"/>
<jsp:include page="common/header.jsp"/>
<jsp:include page="common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <!-- Hero Section -->
        <section class="hero">
            <div class="hero-content">
                <h1>Next-Gen E-Commerce, Built for Performance.</h1>
                <p>Discover thousands of premium electronics, fashion, books, and groceries with ultra-fast delivery and verified reviews.</p>
                <div style="display: flex; gap: 14px; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg" style="background: white; color: var(--primary);">Explore Catalog ➔</a>
                    <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-outline btn-lg" style="color: white; border-color: white;">Become a Seller</a>
                </div>
            </div>
            <div style="display: none; font-size: 8rem;" class="hero-icon">
                🛒
            </div>
        </section>

        <!-- Categories Section -->
        <section style="margin-bottom: 50px;">
            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 20px;">
                <h2 style="font-size: 1.6rem; font-weight: 800;">Popular Categories</h2>
                <a href="${pageContext.request.contextPath}/products" style="font-weight: 600;">View All ➔</a>
            </div>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px;">
                <c:forEach var="cat" items="${categories}">
                    <a href="${pageContext.request.contextPath}/products?category=${cat}" class="card" style="padding: 20px; text-align: center; text-decoration: none;">
                        <div style="font-size: 2.2rem; margin-bottom: 8px;">
                            <c:choose>
                                <c:when test="${cat == 'Electronics'}">🎧</c:when>
                                <c:when test="${cat == 'Fashion'}">👕</c:when>
                                <c:when test="${cat == 'Books'}">📚</c:when>
                                <c:when test="${cat == 'Home & Living'}">🛋️</c:when>
                                <c:when test="${cat == 'Groceries'}">🍎</c:when>
                                <c:otherwise>📦</c:otherwise>
                            </c:choose>
                        </div>
                        <h3 style="font-size: 1rem; color: var(--gray-800); font-weight: 700;"><c:out value="${cat}"/></h3>
                    </a>
                </c:forEach>
            </div>
        </section>

        <!-- Featured Products Section -->
        <section>
            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 20px;">
                <h2 style="font-size: 1.6rem; font-weight: 800;">Featured Arrivals</h2>
                <a href="${pageContext.request.contextPath}/products" style="font-weight: 600;">Browse All Items ➔</a>
            </div>

            <div class="product-grid">
                <c:forEach var="p" items="${featuredProducts}">
                    <div class="product-card">
                        <div class="product-img-wrapper">
                            <span class="product-category-tag"><c:out value="${p.category}"/></span>
                            <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>" class="product-img" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                        </div>
                        <div class="product-details">
                            <h3 class="product-title">
                                <a href="${pageContext.request.contextPath}/products/view?id=${p.id}"><c:out value="${p.name}"/></a>
                            </h3>
                            <div class="product-rating">
                                ⭐ <c:out value="${p.averageRating > 0 ? String.format('%.1f', p.averageRating) : 'New'}"/>
                                <span style="color: var(--gray-400); font-size: 0.8rem;">(<c:out value="${p.reviewCount}"/> reviews)</span>
                            </div>
                            <div class="product-price-row">
                                <span class="product-price">₹<c:out value="${p.price}"/></span>
                                <c:choose>
                                    <c:when test="${p.stockQty > 0}">
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin: 0;">
                                            <input type="hidden" name="action" value="add">
                                            <input type="hidden" name="productId" value="${p.id}">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn btn-primary btn-sm">Add to Cart</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-warning">Out of Stock</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
    </div>
</main>

<jsp:include page="common/chatbot.jsp"/>
<jsp:include page="common/footer.jsp"/>
