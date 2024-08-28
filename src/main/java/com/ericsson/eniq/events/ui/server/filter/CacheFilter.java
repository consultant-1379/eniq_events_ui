package com.ericsson.eniq.events.ui.server.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 
 * @author ekurshi
 * @since 2013
 *
 */
public class CacheFilter implements Filter {

    /*Do allow caching of the helpset and user guide files*/
    public boolean isHelpSet(String requestURI) {
        if (requestURI.toLowerCase().contains("help")) {
            return true;
        }
        return false;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final Date now = new Date();
        final String requestURI = httpRequest.getRequestURI();

        boolean doCache = true;
        // Don't cache .html files. .cache.html files should be cached though, as should helpset files:
        if (requestURI.toLowerCase().endsWith(".html") && !requestURI.toLowerCase().endsWith(".cache.html") &&!isHelpSet(requestURI)) {
            doCache = false;
        }

        if (doCache) {
            now.setMonth(now.getMonth() + 1);
            httpResponse.setDateHeader("Expires", now.getTime());
            httpResponse.setHeader("Cache-Control", "private");
            httpResponse.setHeader("Pragma", "cache");
            //        if (requestURI.contains(".js")) {
            //        if (!(requestURI.contains("/images/") || requestURI.contains(".gif") || requestURI.contains(".ico"))) {
            //            System.out.println("filter................:" + requestURI);
            //            httpResponse.setHeader("Content-Encoding", "deflate");
            //        }
            filterChain.doFilter(request, response);
        }
        }

    @Override
    public void destroy() {
    }
}