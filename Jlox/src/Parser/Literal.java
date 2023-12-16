package Parser;

public class Literal extends Expr {
    public final Object value;

    public Literal(Object value){
        this.value=value;
    }
    public <R> R accept(Visitor<R> visitor){
        return visitor.visitLiteralExpression(this);
    }



}
