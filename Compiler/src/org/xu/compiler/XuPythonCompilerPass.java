package org.xu.compiler;

import org.xu.component.XuNullIOComponent;
import org.xu.exception.XuException;
import org.xu.parser.XuParsedData;
import org.xu.parser.expression.*;
import org.xu.parser.statement.*;
import org.xu.pass.XuCompilationPass;
import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.processor.XuStatementNodeProcessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class XuPythonCompilerPass extends XuCompilationPass<XuParsedData, XuNullIOComponent>
        implements XuExpressionNodeProcessor<String>, XuStatementNodeProcessor<String> {

    private PrintWriter writer;
    private final String outPath = "project/build/test.py";
    private int tabSize = 4;
    private int indentation = 0;

    public XuPythonCompilerPass() {
        try {
            writer = new PrintWriter(new FileOutputStream(outPath));
        } catch (IOException e) {
            throw new XuException(e.getMessage());
        }
    }

    @Override
    public Class<XuParsedData> getInputType() {
        return XuParsedData.class;
    }

    @Override
    public Class<XuNullIOComponent> getOutputType() {
        return XuNullIOComponent.class;
    }

    @Override
    public String getDebugName() {
        return "Python Compiler";
    }

    @Override
    protected XuNullIOComponent pass(XuParsedData input) {
        for (XuStatementNode statement : input.getStatements()) {
            writeLine(evaluate(statement));
        }

        writer.close();

        return new XuNullIOComponent();
    }

    private void writeLine(String value) {
        writer.print(indent() + value);
    }

    private String indent() {
        return " ".repeat(indentation * tabSize);
    }

    private String evaluate(XuExpressionNode expression) {
        return expression.acceptProcessor(this);
    }

    private String evaluate(XuStatementNode statement) {
        return statement.acceptProcessor(this);
    }

    @Override
    public String processLiteralExpression(XuLiteralExpression expression) {

        return switch (expression.literal.type()) {
            case TRUE -> "True";
            case FALSE -> "False";
            default -> expression.literal.literal().toString();
        };
    }

    @Override
    public String processBinaryExpression(XuBinaryExpression expression) {
        StringBuilder sb = new StringBuilder();

        String left = evaluate(expression.left);
        String right = evaluate(expression.right);
        String operator = expression.operator.lexeme();

        sb.append(left).append(" ").append(operator).append(" ").append(right);

        return sb.toString();
    }

    @Override
    public String processUnaryExpression(XuUnaryExpression expression) {
        StringBuilder sb = new StringBuilder();

        String right = evaluate(expression.expression);
        String operator = expression.operator.lexeme();

        switch (expression.operator.type()) {
            case MARK:
                sb.append("not ").append(right);
                break;

            case MINUS:
                sb.append("-").append(right);
                break;

            default:
                sb.append(operator).append(right);
                break;
        }

        return sb.toString();
    }

    @Override
    public String processLogicalExpression(XuLogicalExpression expression) {
        StringBuilder sb = new StringBuilder();

        String left = evaluate(expression.left);
        String right = evaluate(expression.right);
        String operator = expression.operator.lexeme();

        switch (expression.operator.type()) {
            case AND:
                sb.append(left).append(" ").append("and").append(" ").append(right);
                break;

            case OR:
                sb.append(left).append(" ").append("or").append(" ").append(right);
                break;

            default:
                sb.append(left).append(" ").append(operator).append(" ").append(right);
                break;
        }

        return sb.toString();
    }

    @Override
    public String processGroupExpression(XuGroupExpression expression) {
        StringBuilder sb = new StringBuilder();

        String solvedExpression = evaluate(expression.expression);
        sb.append("(").append(solvedExpression).append(")");

        return sb.toString();
    }

    @Override
    public String processVariableGetExpression(XuVariableGetExpression expression) {
        return expression.name.lexeme();
    }

    @Override
    public String processAssignmentExpression(XuAssignmentExpression expression) {
        StringBuilder sb = new StringBuilder();

        String variableName = expression.name.lexeme();
        String value = evaluate(expression.value);

        sb.append(variableName).append(" = ").append(value);

        return sb.toString();
    }

    @Override
    public String processIfStatement(XuIfStatement statement) {
        StringBuilder sb = new StringBuilder();

        sb.append("if ");
        sb.append(evaluate(statement.condition));
        sb.append(":\n");

        beginScope();

        sb.append(evaluate(statement.thenStatement));

        if (statement.elseStatement != null) {
            endScope();
            sb.append("else:\n");
            beginScope();
            sb.append(evaluate(statement.elseStatement));
        }

        endScope();

        return sb.toString();
    }

    @Override
    public String processExpressionStatement(XuExpressionStatement statement) {
        return evaluate(statement.expression);
    }

    @Override
    public String processBodyStatement(XuBodyStatement statement) {
        StringBuilder sb = new StringBuilder();

        for (XuStatementNode statementNode : statement.statements) {
            sb.append(indent()).append(evaluate(statementNode)).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String processPrintStatement(PrintStatement statement) {
        StringBuilder sb = new StringBuilder();

        sb.append("print('");
        sb.append(evaluate(statement.value));
        sb.append("')");

        return sb.toString();
    }

    @Override
    public String processVariableDeclaration(VariableDeclarationStatement statement) {
        StringBuilder sb = new StringBuilder();

        String variableName = statement.name.lexeme();
        String value = evaluate(statement.value);

        sb.append(variableName).append(" = ").append(value);

        return sb.toString();
    }

    @Override
    public String processWhileStatement(XuWhileStatement statement) {
        StringBuilder sb = new StringBuilder();

        sb.append("while ");
        sb.append(evaluate(statement.condition));
        sb.append(":\n");

        beginScope();

        sb.append(evaluate(statement.body));

        endScope();

        return sb.toString();
    }

    private void beginScope() {
        indentation++;
    }

    private void endScope() {
        indentation--;
    }
}
