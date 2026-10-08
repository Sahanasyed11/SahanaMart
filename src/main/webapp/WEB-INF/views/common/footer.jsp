<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer">
    <div class="container">
        <div class="footer-grid">
            <div>
                <h3 class="footer-title" style="color: var(--white);">🛒 SahanaMart</h3>
                <p style="margin-bottom: 15px;">Enterprise E-Commerce Web Application built for Anna University R2025 Semester 3 Capstone.</p>
                <p style="font-size: 0.85rem; color: var(--gray-400);">Technology Stack: Java Servlets, JDBC, Apache Tomcat 9, HikariCP, H2/MySQL, jBCrypt, JUnit 5.</p>
            </div>
            <div>
                <h4 class="footer-title">Quick Links</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/products">All Products</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/login">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/register">Register as Seller</a></li>
                </ul>
            </div>
            <div>
                <h4 class="footer-title">Customer Care</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/orders">Track Order</a></li>
                    <li><a href="mailto:support@sahanamart.com">support@sahanamart.com</a></li>
                    <li><a href="#">Shipping & Returns</a></li>
                </ul>
            </div>
            <div>
                <h4 class="footer-title">System Status</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/api/v1/health" target="_blank">Health Check API</a></li>
                    <li><span class="badge badge-success">API Status: UP</span></li>
                    <li><span class="badge badge-info">Database: Connected</span></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2026 SahanaMart. Anna University R2025 Semester 3 Capstone. All Rights Reserved.</p>
        </div>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>
