package org.xu.semantics;

import org.xu.parser.XuParsedData;
import org.xu.pass.XuCompilationPass;

public class XuSemanticAnalyzerPass extends XuCompilationPass<XuParsedData, XuParsedData> {

    @Override
    public Class<XuParsedData> getInputType() {
        return XuParsedData.class;
    }

    @Override
    public Class<XuParsedData> getOutputType() {
        return XuParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Semantic Analyzer Pass";
    }

    @Override
    protected XuParsedData pass(XuParsedData input) {
        return null;
    }
}
