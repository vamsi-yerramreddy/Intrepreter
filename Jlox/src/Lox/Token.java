package Lox;

/*Below class is the representation of the token
* It has the following attributes
* 1. type : this is the type of the token
* 2. lexeme : this is the string representation of the token
* 3. literal : this is the value of the token
* 4. line : this is the line number where the token is present
*
* */
public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line){
        this.type = type;
        this.lexeme=lexeme;
        this.line=line;
        this.literal=literal;
    }
    public String toString(){
        return type+ " "+ lexeme+ " "+ literal;
    }

}
