package Interp;

import Lox.Environment;
import Lox.Error;
import Lox.Token;
import Lox.TokenType;
import Parser.*;
import Statement.Stmt;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Error.RuntimeError(error);
        }

    }

    private void execute(Stmt statement) {
        statement.accept(this);

    }




    public String stringify(Object obj) {
        if (obj == null) return "nil";
        if (obj instanceof Double) {
            String text = obj.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return obj.toString();
    }


    @Override
    public Object visitBinaryExpression(Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        switch (expression.operator.type) {
            case MINUS -> {
                checkOperand(expression.operator, left, right);
                return (double) left - (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expression.operator, "Operands must be two numbers or two strings");
            }
            case GREATER -> {
                checkOperand(expression.operator, left, right);
                checkOperand(expression.operator, left, right);
                return (double) left > (double) right;
            }
            case LESS -> {
                checkOperand(expression.operator, left, right);
                return (double) left < (double) right;
            }
            case GREATER_EQUAL -> {
                checkOperand(expression.operator, left, right);
                return (double) left >= (double) right;
            }
            case LESS_EQUAL -> {
                checkOperand(expression.operator, left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
            case SLASH -> {
                checkOperand(expression.operator, left, right);
                return (double) left / (double) right;
            }
            case STAR -> {
                checkOperand(expression.operator, left, right);
                return (double) left * (double) right;
            }
        }
        return "unreachable visit binary exp";
    }

    private void checkOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be a number buddy");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);


    }

    @Override
    public Object visitGroupingExpression(Grouping grp) {
        return evaluate(grp.expression);
    }

    private Object evaluate(Expr exp) {
        return exp.accept(this);
    }

    @Override
    public Object visitLiteralExpression(Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpression(Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double) right;
        }

        return null;
    }

    @Override
    public Object visitAssignExpression(Assign assign) {
        Object value = evaluate(assign.value);
        environment.assign(assign.name, value);

        return value;
    }

    private boolean isTruthy(Object obj) {
        //System.out.println("isTruthy: " + obj);
        if (obj == null)
            return false;
        if (obj instanceof Boolean) return (boolean) obj;

        return true;
    }


    @Override
    public Void visitExpressionStatement(Stmt.Expression expression) {
        evaluate(expression.expression);

        return null;
    }

    @Override
    public Void visitPrintStatement(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStatement(Stmt.Var expression) {
        Object value = null;
        if (expression.initializer != null) {
            value = evaluate(expression.initializer);
        }
        environment.define(expression.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStatement(Stmt.Block block) {
        executeBlock(block.statements, new Environment(environment));
        return null;
    }

    @Override
    public Object visitVariableExpression(Variable expression) {
            return environment.get(expression.name);

    }

    public void executeBlock(List<Stmt> statements,
                             Environment env) {
        Environment previous = this.environment;

        try {
            this.environment = env;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitIfStatement(Stmt.If stmt) {

        if (isTruthy(evaluate(stmt.condition))) {
        //    System.out.println("if condition is true");
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }
    @Override
    public  Object visitLogicalExpression(Logical expression){
        Object left=evaluate(expression.left);
        if(expression.operator.type== TokenType.OR){
            if(isTruthy(left))return left;
        }else{
            if(!isTruthy(left))return left;
        }
        return evaluate(expression.right);
    }
    public Void visitWhileStatement(Stmt.While stmt){
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

}
