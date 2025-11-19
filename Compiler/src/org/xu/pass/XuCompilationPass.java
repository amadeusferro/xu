package org.xu.pass;

import org.xu.component.XuIOComponent;
import org.xu.interceptor.XuPassiveInterceptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class XuCompilationPass<I extends XuIOComponent, O extends XuIOComponent> {

    private final List<XuPassiveInterceptor<I, O>> interceptors = new ArrayList<>();

    public XuCompilationPass<I, O> addInterceptor(XuPassiveInterceptor<I, O> interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public final O run(I input) {
        return pass(input);
    }

    public final O runWithInterceptors(I input) {

        for (XuPassiveInterceptor<I, O> interceptor : interceptors) {
            interceptor.beforeState(input);
        }

        O output = pass(input);

        for (XuPassiveInterceptor<I, O> interceptor : interceptors) {
            interceptor.afterState(output);
        }

        return output;
    }

    public abstract Class<I> getInputType();

    public abstract Class<O> getOutputType();

    public abstract String getDebugName();

    protected abstract O pass(I input);
}
