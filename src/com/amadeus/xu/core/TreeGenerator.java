package com.amadeus.xu.core;

import com.amadeus.xu.parser.expression.ExpressionNode;
import com.amadeus.xu.parser.statment.StatementNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TreeGenerator {
    public static String generateTree(List<StatementNode> expressionsList) {

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /><title>Amadeus' Parse Tree</title><style>body {font-family: Calibri, Segoe, \"Segoe UI\", \"Gill Sans\", \"Gill Sans MT\", sans-serif;} .tree, .tree ul, .tree li {list-style: none; margin: 0; padding: 0; position: relative;} .tree {margin: 0 0 1em; text-align: center;} .tree, .tree ul {display: table;} .tree ul {width: 100%;} .tree li {display: table-cell; padding: 0.5em 0; vertical-align: top;} .tree li:before {outline: solid 1px #666; content: \"\"; left: 0; position: absolute; right: 0; top: 0;} .tree li:first-child:before {left: 50%;} .tree li:last-child:before {right: 50%;} .tree code, .tree span {border: solid 0.1em #666; border-radius: 0.2em; display: inline-block; margin: 0 0.2em 0.5em; padding: 0.2em 0.5em; position: relative;} .tree code {font-family: monaco, Consolas, \"Lucida Console\", monospace;} .tree ul:before, .tree code:before, .tree span:before {outline: solid 1px #666; content: \"\"; height: 0.5em; left: 50%; position: absolute;} .tree ul:before {top: -0.5em;} .tree code:before, .tree span:before {top: -0.55em;} .tree > li {margin-top: 0;} .tree > li:before, .tree > li:after, .tree > li > code:before, .tree > li > span:before {outline: none;}</style></head><body><h1>Amadeus' Parse Tree</h1>");

        for (int i = 0; i < expressionsList.size(); i++) {
            sb.append("<!-- Tree ");
            sb.append(i+1);
            sb.append("1 --><figure><figcaption>Expression Tree #");
            sb.append(i+1);
            sb.append("</figcaption><ul class=\"tree\">");
            sb.append(expressionsList.get(i).getTreeNode());
            sb.append("</ul></figure>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    public static void writeHTML(String path, String content) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(content);
            System.out.println("Parse Tree Created!");
        } catch (IOException e) {
            System.err.println("Error writing HTML file.;");
            e.printStackTrace();
        }
    }
}
