package com.amadeus.xu.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.amadeus.xu.lexer.Lexer;
import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.Parser;
import com.amadeus.xu.parser.expression.ExpressionNode;
import com.amadeus.xu.parser.statment.StatementNode;

public class Main {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        File file = new File("src/com/amadeus/xu/secondCode.xu");

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        String content = sb.toString();

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        ArrayList<Token> tokensList = lexer.scanTokens(content);

        tokensList.forEach(System.out::println);

        List<StatementNode> expressionsList  = parser.parseTokens(tokensList);

        expressionsList.forEach(System.out::println);

        System.out.println();
        String HTML = TreeGenerator.generateTree(expressionsList);
        TreeGenerator.writeHTML("/home/live/Desktop/index.html", HTML);


    }
}
