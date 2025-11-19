package org.xu.project;

import java.util.List;

public record XuExternalInterceptorConfiguration(boolean enableExternalInterceptors, List<String> names) {
}
