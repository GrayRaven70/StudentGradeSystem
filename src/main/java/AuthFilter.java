package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// 拦截所有访问 user_list.html 的请求
@WebFilter("/*")
public class AuthFilter implements Filter {
    // 无需登录即可访问的路径
    private static final Set<String> WHITE = new HashSet<>(Arrays.asList(
            "/index.jsp", "/admin_login.jsp", "/teacher_login.jsp", "/student_login.jsp",
            "/login", "/studentLogin", "/changePassword", "/logout"
    ));
    // 静态资源后缀
    private static final Set<String> STATIC_EXT = new HashSet<>(Arrays.asList(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".svg", ".woff", ".woff2", ".ttf"
    ));

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.isEmpty()) path = "/";

        // 白名单 / 静态资源 放行
        if (WHITE.contains(path) || isStatic(path)) { chain.doFilter(request, response); return; }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
    private boolean isStatic(String path) {
        for (String ext : STATIC_EXT) if (path.toLowerCase().endsWith(ext)) return true;
        return false;
    }
}