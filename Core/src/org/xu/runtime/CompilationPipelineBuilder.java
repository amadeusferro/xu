package org.xu.runtime;

import org.xu.binary.XuBytecodeEmissionPass;
import org.xu.compiler.XuCSharpCompilerPass;
import org.xu.compiler.XuCompilePass;
import org.xu.compiler.XuPythonCompilerPass;
import org.xu.compiler.CompilationPipeline;
import org.xu.emulator.XuVirtualMachine;
import org.xu.external.interceptor.XuInterceptorLoader;
import org.xu.external.interceptor.XuInterceptorResolver;
import org.xu.interceptor.XuPassiveInterceptor;
import org.xu.interpreter.XuInterpretPass;
import org.xu.optimizer.XuOptimizationPass;
import org.xu.parser.XuParsePass;
import org.xu.project.XuExternalInterceptorConfiguration;
import org.xu.project.XuOptimizationLevel;
import org.xu.project.XuProject;
import org.xu.scanner.XuScanPass;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CompilationPipelineBuilder {

    public CompilationPipeline build(XuProject project) {

        CompilationPipeline compilationPipeline = new CompilationPipeline();

        XuScanPass scanPass = new XuScanPass();
        XuParsePass parsePass = new XuParsePass();
        XuOptimizationPass optimizationPass = new XuOptimizationPass();
        //XuCompilePass compilePass = new XuCompilePass();
        //XuPythonCompilerPass compilePass = new XuPythonCompilerPass();
        XuCSharpCompilerPass compilePass = new XuCSharpCompilerPass();
        //XuBytecodeEmissionPass emissionPass = new XuBytecodeEmissionPass();
        //XuVirtualMachine virtualMachine = new XuVirtualMachine();
        XuInterpretPass interpretPass = new XuInterpretPass();

        compilationPipeline.insertStage(scanPass);
        compilationPipeline.insertStage(parsePass);

        if (project.environment().optimizationLevel() == XuOptimizationLevel.MAX) {
            compilationPipeline.insertStage(optimizationPass);
        }

        if (!project.isScript()) {
            compilationPipeline.insertStage(compilePass);
            //compilationPipeline.insertStage(emissionPass);
            //compilationPipeline.insertStage(virtualMachine);
        }

        if (project.isScript()) {
            compilationPipeline.insertStage(interpretPass);
        } else {
            XuExternalInterceptorConfiguration externalInterceptorDescriptor = project.externalInterceptorConfiguration();
            if (externalInterceptorDescriptor.enableExternalInterceptors()) {
                XuInterceptorLoader interceptorLoader = new XuInterceptorLoader();
                List<XuPassiveInterceptor> interceptors = interceptorLoader.loadExternalInterceptors();

                XuInterceptorResolver interceptorResolver = new XuInterceptorResolver();
                interceptorResolver.attachInterceptors(interceptors, compilationPipeline,
                        project);
            }
        }

        return compilationPipeline;
    }
}
