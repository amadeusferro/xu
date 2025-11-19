package org.xu.project;

import java.util.List;

public record XuInterceptorConfiguration(List<XuInterceptorDescriptor> interceptors) {

    public boolean isInterceptorActive(String name) {
        return interceptors.stream()
                .filter(interceptor -> interceptor.name().equals(name))
                .findFirst()
                .map(XuInterceptorDescriptor::isActive)
                .orElse(false);
    }
}
