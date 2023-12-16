package Lox;

import Parser.*;
import Statement.Stmt;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    public String print(Expr expression) {

        return expression.accept(this);
    }

    String print(Stmt statement) {
        return statement.accept(this);
    }

    @Override
    public String visitBinaryExpression(Binary expression) {
        return parenthesis(expression.operator.lexeme, expression.left, expression.right);

    }

    private String parenthesis(String name, Expr... expressions) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        transform(builder, parts);
        builder.append(")");
        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr) {
                builder.append(((Expr) part).accept(this));
            } else if (part instanceof Stmt) {
                builder.append(((Stmt) part).accept(this));
            } else if (part instanceof Token) {
                builder.append(((Token) part).lexeme);
            } else if (part instanceof String) {
                builder.append(part);
            } else if (part instanceof Double) {
                String text = part.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                builder.append(text);
            } else {
                builder.append(part.toString());
        }
    }
}
    @Override
    public String visitGroupingExpression(Grouping expression) {
        return parenthesis("group",expression);

    }

    @Override
    public String visitLiteralExpression(Literal expression) {
        if(expression.value==null) return "nil";
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Unary expression) {
        return parenthesis(expression.operator.lexeme,expression.right);
    }

    @Override
    public String visitAssignExpression(Assign assign) {
        return parenthesize2("=",assign.name.lexeme, assign.value);

    }


    @Override
    public String visitVariableExpression(Variable variable) {
        return variable.name.lexeme;
    }

    @Override
    public String visitExpressionStatement(Stmt.Expression expression) {
        return  parenthesis(";",expression.expression);


    }

    @Override
    public String visitPrintStatement(Stmt.Print stmt) {
        if (stmt.expression == null) return "(return)";
        return parenthesis("print",stmt.expression);
    }

    @Override
    public String visitVarStatement(Stmt.Var expression) {
        if (expression.initializer == null) return parenthesize2("var", expression.name);

        return parenthesize2("var", expression.name, "=", expression.initializer);
    }
    @Override
    public String visitBlockStatement(Stmt.Block block) {
        StringBuilder builder=new StringBuilder();
        builder.append("(block ");
        for(Stmt statement:block.statements){
            builder.append(statement.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
    @Override
    public String visitIfStatement(Stmt.If stmt) {
        if(stmt.elseBranch==null){
            return parenthesize2("if",stmt.condition,stmt.thenBranch);
        }
        return parenthesize2("if-else",stmt.condition,stmt.thenBranch,stmt.elseBranch);
    }
    @Override
    public String visitWhileStatement(Stmt.While stmt) {
       return parenthesize2("while",stmt.condition,stmt.body);
    }
    @Override
    public String visitLogicalExpression(Logical logical) {
        return parenthesis(logical.operator.lexeme,logical.left,logical.right);
    }


}
