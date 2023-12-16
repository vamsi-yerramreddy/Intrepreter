package Statement;

import Lox.Token;
import Parser.Expr;

import java.util.List;

public abstract  class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitExpressionStatement(Expression expression);

        R visitPrintStatement(Print expression);
        R visitVarStatement(Var expression);

            R visitBlockStatement(Block block);
        R visitIfStatement(If stmt);
        R visitWhileStatement(While stmt);
    }

    public static class Block extends  Stmt{
        public   List<Stmt> statements;

        public Block(List<Stmt> block) {
            this.statements= block;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }
    }

    public static class Expression extends Stmt {
        public final Expr expression;
        public Expression(Expr expression) {
            this.expression = expression;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }


    }
    public static class Print extends Stmt {
        public final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {

            return visitor.visitPrintStatement(this);
        }

    }
    public static class Var extends Stmt {
        public Token name;
        public Expr initializer;

        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {

            return visitor.visitVarStatement(this);
        }
    }

    public static class If extends Stmt {
        public Expr condition;
        public Stmt thenBranch;
        public Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }
    }
    public static class While extends Stmt {
        public Expr condition;
        public Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }
    }



}

