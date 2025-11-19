package org.xu.project;

public record XuProject(XuProjectDescriptor descriptor, String main, Boolean isScript,
                         XuKernelConfiguration kernelConfiguration, XuDependencies dependencies,
                         XuEnvironmentDescriptor environment, XuInterceptorConfiguration interceptorConfiguration,
                         XuExternalInterceptorConfiguration externalInterceptorConfiguration) {
}
