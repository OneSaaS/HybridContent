package net.twomini.hybridcontent.assets;

import com.google.common.base.Strings;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Ec2ElbHttpsRedirectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //EC2 Elastic Load Balancer header to show protocol, http or https
        String proto = ((HttpServletRequest)servletRequest).getHeader("X-Forwarded-Proto");
        //If there is a value, we are behind EC2 ELB, if http redirect to https
        if (!Strings.isNullOrEmpty(proto) && "http".equalsIgnoreCase(proto)) {
            ((HttpServletResponse)servletResponse).sendRedirect(((HttpServletRequest)servletRequest).getRequestURL().toString().replaceFirst("http", "https"));
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
