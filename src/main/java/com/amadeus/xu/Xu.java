package com.amadeus.xu;

import com.amadeus.xu.compiler.lexer.Lexer;
import com.amadeus.xu.compiler.lexer.Token;
import com.amadeus.xu.compiler.parser.Parser;
import com.amadeus.xu.compiler.parser.statement.StatementNode;
import com.amadeus.xu.util.VisualTreeGenerator;
import com.amadeus.xu.util.XuFile;

import java.util.List;

public class Xu {
    public static void main(String[] args) {

        XuFile mainFile = new XuFile("src/main/java/com/amadeus/xu/example/main.xu");

        Lexer lexer = new Lexer();

        List<Token> tokens = lexer.scanTokens(mainFile);

        for(Token token : tokens) {
            System.out.println(token);
        }
        System.out.println();

        Parser parser = new Parser();

        List<StatementNode> statements = parser.parseTokens(tokens);

        for(StatementNode statement : statements) {
            System.out.println(statement.toString());
        }

        String HTML = VisualTreeGenerator.generateTree(statements);
        VisualTreeGenerator.writeHTML("src/main/java/com/amadeus/xu/out/index.html", HTML);

    }
}