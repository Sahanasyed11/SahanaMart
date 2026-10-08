<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Products - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="${pageContext.request.contextPath}/seller/dashboard" style="font-size: 0.9rem; font-weight: 600;">« Back to Dashboard</a>
                <h1 style="font-size: 1.8rem; font-weight: 800; margin-top: 4px;">My Product Listings</h1>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/seller/products/new" class="btn btn-primary">+ Add New Product</a>
            </div>
        </div>

        <c:if test="${param.saved == 'true'}">
            <div class="alert alert-success">
                ✅ Product listing saved successfully!
            </div>
        </c:if>

        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">
                ✅ Product listing removed successfully!
            </div>
        </c:if>

        <div class="card" style="padding: 24px;">
            <c:choose>
                <c:when test="${empty products}">
                    <div style="text-align: center; padding: 60px 20px;">
                        <p style="color: var(--gray-500); margin-bottom: 16px;">You haven't listed any products yet.</p>
                        <a href="${pageContext.request.contextPath}/seller/products/new" class="btn btn-primary">Create Your First Listing</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Thumbnail</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <img src="<c:out value='${p.imageUrl}'/>" alt="" style="width: 45px; height: 45px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                    </td>
                                    <td>
                                        <strong><c:out value="${p.name}"/></strong>
                                    </td>
                                    <td><span class="badge badge-info"><c:out value="${p.category}"/></span></td>
                                    <td style="font-weight: 700;">₹<c:out value="${p.price}"/></td>
                                    <td>
                                        <span class="badge ${p.stockQty > 5 ? 'badge-success' : 'badge-warning'}">
                                            <c:out value="${p.stockQty}"/> in stock
                                        </span>
                                    </td>
                                    <td style="text-align: right;">
                                        <div style="display: inline-flex; gap: 8px;">
                                            <a href="${pageContext.request.contextPath}/seller/products/edit?id=${p.id}" class="btn btn-secondary btn-sm">Edit</a>
                                            <form action="${pageContext.request.contextPath}/seller/products/delete" method="POST" style="margin: 0;">
                                                <input type="hidden" name="id" value="${p.id}">
                                                <button type="submit" class="btn btn-danger btn-sm" data-confirm="Are you sure you want to delete this listing?">Delete</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
