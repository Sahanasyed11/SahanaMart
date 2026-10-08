<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Product Catalog - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <!-- Catalog Header & Search Filter Bar -->
        <div style="background: white; padding: 24px; border-radius: var(--radius-md); border: 1px solid var(--gray-200); margin-bottom: 30px;">
            <form action="${pageContext.request.contextPath}/products" method="GET" style="display: flex; gap: 14px; flex-wrap: wrap;">
                <div style="flex: 2; min-width: 250px;">
                    <input type="text" name="keyword" class="form-control" placeholder="Search products by title or keywords..." value="<c:out value='${keyword}'/>">
                </div>
                <div style="flex: 1; min-width: 180px;">
                    <select name="category" class="form-control">
                        <option value="ALL">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}" <c:if test="${selectedCategory == cat}">selected</c:if>><c:out value="${cat}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <button type="submit" class="btn btn-primary" style="height: 48px; padding: 0 24px;">Filter Catalog</button>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary" style="height: 48px; padding: 0 16px;">Reset</a>
                </div>
            </form>
        </div>

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h1 style="font-size: 1.5rem; font-weight: 800;">
                <c:choose>
                    <c:when test="${not empty keyword}">Results for "<c:out value="${keyword}"/>"</c:when>
                    <c:when test="${selectedCategory != 'ALL' && not empty selectedCategory}"><c:out value="${selectedCategory}"/></c:when>
                    <c:otherwise>All Products</c:otherwise>
                </c:choose>
                <span style="font-size: 0.95rem; font-weight: 500; color: var(--gray-500); margin-left: 8px;">(<c:out value="${totalItems}"/> items found)</span>
            </h1>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div style="text-align: center; padding: 80px 20px; background: white; border-radius: var(--radius-md); border: 1px dashed var(--gray-300);">
                    <div style="font-size: 3.5rem; margin-bottom: 12px;">🔍</div>
                    <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">No matching products found</h3>
                    <p style="color: var(--gray-500); margin-bottom: 20px;">Try clearing your search keyword or selecting a different category.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse All Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Products Grid -->
                <div class="product-grid">
                    <c:forEach var="p" items="${products}">
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

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div style="display: flex; justify-content: center; gap: 8px; margin-top: 40px;">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/products?page=${currentPage - 1}&keyword=<c:out value='${keyword}'/>&category=<c:out value='${selectedCategory}'/>" class="btn btn-secondary btn-sm">« Prev</a>
                        </c:if>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="${pageContext.request.contextPath}/products?page=${i}&keyword=<c:out value='${keyword}'/>&category=<c:out value='${selectedCategory}'/>" 
                               class="btn btn-sm ${currentPage == i ? 'btn-primary' : 'btn-secondary'}">
                                <c:out value="${i}"/>
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/products?page=${currentPage + 1}&keyword=<c:out value='${keyword}'/>&category=<c:out value='${selectedCategory}'/>" class="btn btn-secondary btn-sm">Next »</a>
                        </c:if>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
