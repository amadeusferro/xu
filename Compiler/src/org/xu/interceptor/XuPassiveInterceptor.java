package org.xu.interceptor;

public interface XuPassiveInterceptor<I, O> {
    void beforeState(final I input);

    void afterState(final O input);

    String getName();
}
