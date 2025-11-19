package org.xu.compiler;

import org.xu.component.XuIOComponent;
import org.xu.pass.XuCompilationPass;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CompilationPipeline {

    private final List<XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent>> passes = new ArrayList<>();

    public void insertStage(XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent> pass) {
        passes.add(pass);
    }

    public void run(XuIOComponent input) {
        XuIOComponent currentInput = input;
        System.out.println("Running without interceptors.");
        for (XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent> pass : passes) {
            currentInput = runPass(pass, currentInput);
        }
    }

    public void runWithInterceptors(XuIOComponent input) {
        XuIOComponent currentInput = input;
        System.out.println("Running with interceptors.");

        for (XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent> pass : passes) {
            currentInput = runPassWithInterceptors(pass, currentInput);
        }
    }

    private <I extends XuIOComponent, O extends XuIOComponent> XuIOComponent runPass(XuCompilationPass<I, O> pass, XuIOComponent input) {
        if (!pass.getInputType().isInstance(input)) {
            throw new IllegalArgumentException("Input type mismatch. Expected: " + pass.getInputType() + ", but got: " + input.getClass());
        }
        return pass.run((I) input);
    }

    private <I extends XuIOComponent, O extends XuIOComponent> XuIOComponent runPassWithInterceptors(XuCompilationPass<I, O> pass, XuIOComponent input) {
        if (!pass.getInputType().isInstance(input)) {
            throw new IllegalArgumentException("Input type mismatch. Expected: " + pass.getInputType() + ", but got: " + input.getClass());
        }
        return pass.runWithInterceptors((I) input);
    }

    public List<XuCompilationPass<? extends XuIOComponent, ? extends XuIOComponent>> getPasses() {
        return passes;
    }
}
