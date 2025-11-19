package org.xu.interpreter;

import org.xu.component.XuIOComponent;

public class XuInterpretResult extends XuIOComponent<XuInterpretResult> {

    private final int result;

    public XuInterpretResult(int result) {
        this.result = result;
    }

    @Override
    public XuInterpretResult clone() {
        return new XuInterpretResult(result);
    }
}
