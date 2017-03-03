package com.fxiaoke.dataplatform.crm.cleantool.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;


public class IntervalInterceptor implements HandlerInterceptor {
    @SuppressWarnings("unchecked")
    private static final ThreadLocal<String> INTERVAL = ThreadLocal.withInitial((Supplier) () -> "1m");
    private static final String INTERVAL_OLD_VALUE = "_OLD_INTERVAL_";
    private static final String INTERVAL_COOKIE_NAME = "interval";

    public static String getTimeInterval() {
        return "group by time(" + INTERVAL.get() + ")";
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String param = request.getParameter(INTERVAL_COOKIE_NAME);
        if (param != null) {
            //同步设置到cookie中
            Cookie c = new Cookie(INTERVAL_COOKIE_NAME, param);
            c.setMaxAge(3600);
            response.addCookie(c);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie i : cookies) {
                    if (i.getName().equals(INTERVAL_COOKIE_NAME)) {
                        param = i.getValue();
                        break;
                    }
                }
            }
        }

        if (param != null) {
            request.setAttribute(INTERVAL_OLD_VALUE, INTERVAL.get());
            INTERVAL.set(param);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Object old = request.getAttribute(INTERVAL_OLD_VALUE);
        if (old != null) {
            INTERVAL.set((String) old);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
