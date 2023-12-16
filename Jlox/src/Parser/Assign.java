package Parser;

import Lox.Token;

public class Assign extends Expr{
    public Token name;
    public Expr value;


    public Assign(Token name, Expr value){
        this.name=name;
        this.value=value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExpression(this);
    }
}
