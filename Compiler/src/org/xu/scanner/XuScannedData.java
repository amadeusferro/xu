package org.xu.scanner;

import org.xu.component.XuIOComponent;

import java.util.ArrayList;
import java.util.List;

public class XuScannedData extends XuIOComponent<XuScannedData> {

    private final List<Token> tokens;

    public XuScannedData(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public XuScannedData clone() {
        List<Token> clonedTokens = new ArrayList<>(tokens.size());
        for(Token token : tokens) {
            Token clonedToken = new Token(token.type(), token.lexeme(), token.literal(), token.line());
            clonedTokens.add(clonedToken);
        }

        return new XuScannedData(clonedTokens);
    }
}
