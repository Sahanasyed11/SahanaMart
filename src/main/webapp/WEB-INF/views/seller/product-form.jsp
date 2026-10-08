<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isEdit" value="${product != null && product.id != null}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Product' : 'Add New Product'} - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container" style="max-width: 680px;">
        <a href="${pageContext.request.contextPath}/seller/products" style="display: inline-block; margin-bottom: 20px; font-weight: 600;">« Back to Products</a>

        <div class="card" style="padding: 30px;">
            <h1 style="font-size: 1.6rem; font-weight: 800; margin-bottom: 8px;">
                <c:out value="${isEdit ? 'Edit Product Listing' : 'Create New Product Listing'}"/>
            </h1>
            <p style="color: var(--gray-500); margin-bottom: 24px;">Provide accurate details to ensure buyers can easily find and purchase your item.</p>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    ⚠️ <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/seller/products/save" method="POST">
                <input type="hidden" name="id" value="<c:out value='${product.id}'/>">

                <div class="form-group">
                    <label for="name">Product Name *</label>
                    <input type="text" id="name" name="name" class="form-control" placeholder="e.g. Wireless Noise-Cancelling Headphones" value="<c:out value='${product.name}'/>" required autofocus>
                </div>

                <div class="form-group">
                    <label for="description">Detailed Description *</label>
                    <textarea id="description" name="description" class="form-control" rows="4" placeholder="Detail features, specifications, and warranty..." required><c:out value="${product.description}"/></textarea>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                    <div class="form-group">
                        <label for="price">Price (₹ INR) *</label>
                        <input type="number" id="price" name="price" class="form-control" step="0.01" min="1" placeholder="999.00" value="<c:out value='${product.price}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="stockQty">Available Stock Quantity *</label>
                        <input type="number" id="stockQty" name="stockQty" class="form-control" min="0" placeholder="50" value="<c:out value='${product.stockQty}'/>" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="category">Category *</label>
                    <input type="text" id="category" name="category" list="categoryList" class="form-control" placeholder="Select or type category (Electronics, Fashion, Books...)" value="<c:out value='${product.category}'/>" required>
                    <datalist id="categoryList">
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}"/>
                        </c:forEach>
                        <option value="Electronics"/>
                        <option value="Fashion"/>
                        <option value="Books"/>
                        <option value="Home & Living"/>
                        <option value="Groceries"/>
                        <option value="Health & Beauty"/>
                    </datalist>
                </div>

                <div class="form-group">
                    <label for="imageUrl">Product Image URL (HTTPS)</label>
                    <input type="url" id="imageUrl" name="imageUrl" class="form-control" placeholder="https://images.unsplash.com/..." value="<c:out value='${product.imageUrl}'/>">
                    <small style="color: var(--gray-500); font-size: 0.8rem;">Provide a public image link. Leave blank for a default category image.</small>
                </div>

                <div style="display: flex; gap: 14px; margin-top: 10px;">
                    <button type="submit" class="btn btn-primary btn-lg" style="flex: 1;">Save Product Listing</button>
                    <a href="${pageContext.request.contextPath}/seller/products" class="btn btn-secondary btn-lg">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="../common/chatbot.jsp"/>
<jsp:include page="../common/footer.jsp"/>
