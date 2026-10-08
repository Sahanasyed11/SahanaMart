<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="User Management - Admin - SahanaMart" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/navbar.jsp"/>

<main class="main-content">
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" style="font-size: 0.9rem; font-weight: 600;">« Back to Admin Console</a>
                <h1 style="font-size: 1.8rem; font-weight: 800; margin-top: 4px;">User Account Management</h1>
            </div>
        </div>

        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">
                ✅ User account successfully removed.
            </div>
        </c:if>

        <div class="card" style="padding: 24px;">
            <table class="table">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Phone</th>
                        <th>Registered Date</th>
                        <th style="text-align: right;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td>#<c:out value="${u.id}"/></td>
                            <td><strong><c:out value="${u.name}"/></strong></td>
                            <td><c:out value="${u.email}"/></td>
                            <td>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test="${u.role == 'ADMIN'}">badge-danger</c:when>
                                        <c:when test="${u.role == 'SELLER'}">badge-warning</c:when>
                                        <c:otherwise>badge-info</c:otherwise>
                                    </c:choose>
                                ">
                                    <c:out value="${u.role}"/>
                                </span>
                            </td>
                            <td><c:out value="${u.phone != null ? u.phone : '-'}"/></td>
                            <td style="color: var(--gray-500); font-size: 0.85rem;"><c:out value="${u.createdAt}"/></td>
                            <td style="text-align: right;">
                                <c:if test="${u.role != 'ADMIN'}">
                                    <form action="${pageContext.request.contextPath}/admin/users/delete" method="POST" style="margin: 0; display: inline;">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <button type="submit" class="btn btn-danger btn-sm" data-confirm="Are you sure you want to delete this user? All their products and cart items will be removed.">Delete</button>
                                    </form>
                                </c:if>
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
