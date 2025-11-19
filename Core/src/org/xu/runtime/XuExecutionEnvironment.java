package org.xu.runtime;

import org.xu.compiler.CompilationPipeline;
import org.xu.project.XuProject;
import org.xu.util.XuFile;

public class XuExecutionEnvironment {
    public void execute(CompilationPipeline compilationPipeline, XuProject project, XuFile main) {
        try {
            if (project.environment().debug()) {
                System.out.println("Compiling and running " + project.descriptor().name() + " " +
                        project.descriptor().version() + ".");
                compilationPipeline.runWithInterceptors(main);
            } else {
                System.out.println("Compiling and running " + project.descriptor().name() + " " +
                        project.descriptor().version() + ".");
                compilationPipeline.run(main);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
