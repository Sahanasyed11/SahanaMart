<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/" class="nav-brand">
            🛒 <span>Sahana</span>Mart
        </a>

        <!-- Search Bar -->
        <div class="nav-search">
            <form action="${pageContext.request.contextPath}/products" method="GET" class="search-form">
                <input type="text" name="keyword" placeholder="Search across 10,000+ products..." value="<c:out value='${param.keyword}'/>">
                <button type="submit" class="search-btn">🔍</button>
            </form>
        </div>

        <!-- Navigation Links -->
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/products" class="nav-item">Browse Catalog</a></li>
            
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <!-- Logged-in Navigation -->
                    <c:if test="${sessionScope.user.role == 'BUYER'}">
                        <li><a href="${pageContext.request.contextPath}/wishlist" class="nav-item">❤️ Wishlist</a></li>
                        <li><a href="${pageContext.request.contextPath}/cart" class="nav-item">🛍️ Cart</a></li>
                        <li><a href="${pageContext.request.contextPath}/orders" class="nav-item">📦 Orders</a></li>
                    </c:if>

                    <c:if test="${sessionScope.user.role == 'SELLER'}">
                        <li><a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-item">📊 Seller Portal</a></li>
                        <li><a href="${pageContext.request.contextPath}/seller/products" class="nav-item">My Products</a></li>
                        <li><a href="${pageContext.request.contextPath}/seller/orders" class="nav-item">Incoming Orders</a></li>
                    </c:if>

                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">⚡ Admin Panel</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/users" class="nav-item">Users</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/orders" class="nav-item">All Orders</a></li>
                    </c:if>

                    <li>
                        <span class="badge badge-info"><c:out value="${sessionScope.user.name}"/> (<c:out value="${sessionScope.user.role}"/>)</span>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-secondary btn-sm">Logout</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <!-- Guest Navigation -->
                    <li><a href="${pageContext.request.contextPath}/auth/login" class="nav-item">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary btn-sm">Sign Up</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</header>
