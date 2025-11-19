package org.xu.project;

public record XuKernelConfiguration(Boolean enabled, XuGPUTarget gpuTarget, Float memoryLimits) {
}
