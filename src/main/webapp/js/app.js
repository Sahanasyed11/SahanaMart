/**
 * SahanaMart Core Client-side Interaction Script
 */

document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Confirmation for destructive actions
    const deleteButtons = document.querySelectorAll("[data-confirm]");
    deleteButtons.forEach(btn => {
        btn.addEventListener("click", function (e) {
            const message = this.getAttribute("data-confirm") || "Are you sure you want to proceed?";
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
