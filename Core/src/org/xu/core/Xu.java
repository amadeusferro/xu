package org.xu.core;

import org.xu.compiler.CompilationPipeline;
import org.xu.project.*;
import org.xu.runtime.XuExecutionEnvironment;
import org.xu.runtime.CompilationPipelineBuilder;
import org.xu.util.XuFile;

public class Xu {
    public static void main(String[] args) {
        CompilationPipelineBuilder pipelineBuilder = new CompilationPipelineBuilder();
        XuProjectFileReader projectFileReader = new XuProjectFileReader(args[0]);
        XuProject project = projectFileReader.getProject();
        XuFile file = new XuFile(project.main());
        CompilationPipeline compilationPipeline = pipelineBuilder.build(project);
        XuExecutionEnvironment executor = new XuExecutionEnvironment();
        executor.execute(compilationPipeline, project, file);
    }
}