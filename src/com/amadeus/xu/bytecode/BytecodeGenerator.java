package com.amadeus.xu.bytecode;

import com.amadeus.xu.lexer.TokenType;
import com.amadeus.xu.parser.expression.*;
import com.amadeus.xu.parser.statment.ExpressionStatement;
import com.amadeus.xu.parser.statment.StatementNode;
import com.amadeus.xu.parser.visitor.ExpressionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BytecodeGenerator implements ExpressionVisitor<Void> {

    private List<Byte> bytes;
    private List<Object> constantTable;

    public Bytecode generate(List<StatementNode> statements) {
        bytes = new ArrayList<Byte>();
        constantTable = new ArrayList<>();
        for(StatementNode statement : statements) {
            if(statement instanceof ExpressionStatement expressionStatement) {
                generateBytecodeForExpression(expressionStatement.expression);
            }
        }
        emitByte(Instruction.Return);
        return new Bytecode(bytes, constantTable);
    }

    private void generateBytecodeForExpression(ExpressionNode expression) {
        expression.accept(this);
    }


    @Override
    public Void visitLiteralExpression(LiteralExpression expr) {
        Byte index = emitConstant(expr.literal.literal);
        emitBytes(Instruction.Push, index);
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression expr) {
        generateBytecodeForExpression(expr.expression);
        emitByte(Instruction.Negate);


        return null;
    }

    @Override
    public Void visitFactorExpression(FactorExpression expr) {
        return null;
    }

    @Override
    public Void visitTermExpression(TermExpression expr) {

        generateBytecodeForExpression(expr.leftExpression);
        generateBytecodeForExpression(expr.rightExpression);
        if(expr.operator.type == TokenType.PLUS) {
            emitByte(Instruction.Add);
        } else if (expr.operator.type == TokenType.MINUS) {
            emitByte(Instruction.Sub);
        }
        return null;
    }

    @Override
    public Void visitGroupExpression(GroupExpression expr) {
        return null;
    }
    
    private void emitByte(Instruction instruction) {
        bytes.add((byte) instruction.ordinal());
    }
    
    private void emitBytes(Instruction first, Byte second) {
        bytes.add((byte) first.ordinal());
        bytes.add(second);
    }
    
    private Byte emitConstant(Object constant) {
        Byte index = (byte) constantTable.size();
        constantTable.add(constant);
        return index;
    }
    
}
