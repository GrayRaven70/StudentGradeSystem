package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// 拦截所有访问 user_list.html 的请求
@WebFilter("/user_list.html")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 检查 Session 里有没有 currentUser 标记
        if (session != null && session.getAttribute("currentUser") != null) {
            HttpServletResponse httpResp = (HttpServletResponse) response;
            httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            httpResp.setHeader("Pragma", "no-cache"); // HTTP 1.0
            httpResp.setDateHeader("Expires", 0); // Proxies
            // 已登录，放行
            chain.doFilter(request, response);
        } else {
            // 未登录，重定向到登录页
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
}