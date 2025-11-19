package org.xu.external.interceptor;

import org.xu.compiler.CompilationPipeline;
import org.xu.component.XuIOComponent;
import org.xu.interceptor.XuPassiveInterceptor;
import org.xu.pass.XuCompilationPass;
import org.xu.project.XuProject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("rawtypes")
public class XuInterceptorResolver {

    public void attachInterceptors(List<XuPassiveInterceptor> interceptors, CompilationPipeline compilationPipeline, XuProject project) {
        for (XuPassiveInterceptor interceptor : interceptors) {
            Type[] genericInterfaces = interceptor.getClass().getGenericInterfaces();
            for (Type type : genericInterfaces) {
                if (type instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                    if (actualTypeArguments.length == 2) {
                        Type inputType = actualTypeArguments[0];
                        Type outputType = actualTypeArguments[1];

                        for (XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent> pass : compilationPipeline.getPasses()) {
                            if (inputType == pass.getInputType() && outputType == pass.getOutputType()
                                    && project.externalInterceptorConfiguration().names().contains(interceptor.getName())) {
                                //noinspection unchecked
                                pass.addInterceptor(interceptor);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
