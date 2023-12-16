package Parser;

public abstract class Expr {
     public interface Visitor<R> {
          R visitBinaryExpression(Binary expression);

          R visitGroupingExpression(Grouping expression);

          R visitLiteralExpression(Literal expression);

          R visitUnaryExpression(Unary expression);

          R visitAssignExpression(Assign assign);

          R visitVariableExpression(Variable variable);
          R visitLogicalExpression(Logical logical);

     }

     public abstract <R> R accept(Visitor<R> visitor);

}
