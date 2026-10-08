<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Moderate Products - Admin - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" style="font-size: 0.9rem; font-weight: 600;">« Back to Admin Console</a>
                <h1 style="font-size: 1.8rem; font-weight: 800; margin-top: 4px;">Product Catalog Moderation</h1>
            </div>
        </div>

        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">
                ✅ Product listing moderated and removed from platform.
            </div>
        </c:if>

        <div class="card" style="padding: 24px;">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Seller</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th style="text-align: right;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>#<c:out value="${p.id}"/></td>
                            <td>
                                <div style="display: flex; gap: 10px; align-items: center;">
                                    <img src="<c:out value='${p.imageUrl}'/>" alt="" style="width: 40px; height: 40px; object-fit: cover; border-radius: var(--radius-sm);" onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'">
                                    <div>
                                        <a href="${pageContext.request.contextPath}/products/view?id=${p.id}" target="_blank" style="font-weight: 700; color: var(--gray-900);">
                                            <c:out value="${p.name}"/>
                                        </a>
                                    </div>
                                </div>
                            </td>
                            <td><c:out value="${p.sellerName}"/></td>
                            <td><span class="badge badge-info"><c:out value="${p.category}"/></span></td>
                            <td style="font-weight: 700;">₹<c:out value="${p.price}"/></td>
                            <td><c:out value="${p.stockQty}"/></td>
                            <td style="text-align: right;">
                                <form action="${pageContext.request.contextPath}/admin/products/delete" method="POST" style="margin: 0; display: inline;">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-danger btn-sm" data-confirm="Are you sure you want to remove this product from the platform?">Remove Listing</button>
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
