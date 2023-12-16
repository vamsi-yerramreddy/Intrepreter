package Parser;

import Lox.Token;
import Lox.TokenType;
import Statement.Stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ExParser {
    private List<Token> tokens;

    private static class ParseError extends RuntimeException{}
    private int current=0;

    public ExParser(List<Token> tokens){
        this.tokens=tokens;
    }

    public List<Stmt> parse(){

        List<Stmt>  statements = new ArrayList<>();
        while(!isEnd()){
            statements.add(declaration());
        }
        return   statements;

    }
    public Stmt declaration() {
        try {
            if (match(TokenType.VAR)) {
               // System.out.println("var declaration");
                return varDeclaration();
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }

    }
    public Stmt varDeclaration(){
        Token name=consume(TokenType.IDENTIFIER,"Expect variable name.");
        Expr initializer=null;
        if(match(TokenType.EQUAL)){
            initializer=expression();
        }
        consume(TokenType.SEMICOLON,"Expect ';' after variable declaration.");
        return new Stmt.Var(name,initializer);

    }



    private Expr expression(){
        return assignment();
    }
    private Expr assignment(){
        Expr expr=or();
        if(match(TokenType.EQUAL)){
            Token equals=previous();
            Expr value=assignment();
            if(expr instanceof Variable){
                Token name=((Variable) expr).name;
                return new Assign(name,value);
            }
            error(equals,"Invalid assignment target.");
        }
        return expr;
    }
    private Expr or(){
        Expr expr=and();
        while(match(TokenType.OR)){
            Token operator=previous();
            Expr right=and();
            expr=new Logical(expr,operator,right);
        }
        return expr;
    }
    private Expr and(){
        Expr expr=equality();
        while(match(TokenType.AND)){
            Token operator=previous();
            Expr right=equality();
            expr=new Logical(expr,operator,right);
        }
        return expr;
    }



    private Stmt statement(){
        if(match(TokenType.PRINT)) {
           // System.out.println("print statement");
            return printStatement();
        }
            if(match(TokenType.IF)){
                //System.out.println("if statement");
            return ifStatement();
        }
        if(match(TokenType.WHILE)){
            return whileStatement();
        }
        if(match(TokenType.FOR)){
            return forStatement();
        }
        if(match(TokenType.LEFT_BRACE))
            return new Stmt.Block(block());
        return expressionStatement();
    }
    private Stmt printStatement(){
        Expr value=expression();
        consume(TokenType.SEMICOLON,"Expect ';' after value.");
        return new Stmt.Print(value);
    }
    private Stmt ifStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'if'.");
        Expr condition=expression();
        consume(TokenType.RIGHT_PAREN,"Expect ')' after if condition.");
        Stmt thenBranch=statement();
        Stmt elseBranch=null;
        if(match(TokenType.ELSE)){
            elseBranch=statement();
        }
        return new Stmt.If(condition,thenBranch,elseBranch);

    }
        private Stmt forStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'for'.");
        Stmt initializer;
        if(match(TokenType.SEMICOLON)){
            initializer=null;
        }else if(match(TokenType.VAR)){
            initializer=varDeclaration();
        }else{
            initializer=expressionStatement();
        }
        Expr condition=null;
        if(!check(TokenType.SEMICOLON)){
            condition=expression();
        }
        consume(TokenType.SEMICOLON,"Expect ';' after loop condition.");
        Expr increment=null;
        if(!check(TokenType.RIGHT_PAREN)){
            increment=expression();
        }
        consume(TokenType.RIGHT_PAREN,"Expect ')' after for clauses.");
        Stmt body=statement();
        if(increment!=null){
            body=new Stmt.Block(Arrays.asList(body,
                    new Stmt.Expression(increment)));
        }
        if(condition==null) condition=new Literal(true);
        body=new Stmt.While(condition,body);
        if(initializer!=null){
            body=new Stmt.Block(List.of(initializer,body));
        }
        return body;
    }
    private Stmt whileStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'while'.");
        Expr condition=expression();
        consume(TokenType.RIGHT_PAREN,"Expect ')' after condition.");
        Stmt body=statement();
        return new Stmt.While(condition,body);
    }
private Stmt expressionStatement(){
        Expr expr=expression();
        consume(TokenType.SEMICOLON,"Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    //equality -> comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality(){
        Expr expression=comparison();

        while(match(TokenType.BANG_EQUAL,TokenType.EQUAL_EQUAL)){
            Token operator=previous();
            Expr right=comparison();
            expression=new Binary(expression,operator,right);
        }
        return expression;
    }
    //comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison(){
        Expr expression=term();
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){
            Token operator=previous();
            Expr right=term();
            expression=new Binary(expression,operator,right);
        }
return  expression;
    }
    //term -> factor ( ( "-" | "+" ) factor )* ;
    private Expr term(){
        Expr expression=factor();
        while(match(TokenType.MINUS,TokenType.PLUS)){
            Token operator = previous();
            Expr right=factor();
            expression=new Binary(expression,operator,right);
        }
        return expression;
    }
    private Expr factor(){
        Expr expression=unary();
        while(match(TokenType.SLASH,TokenType.STAR)){
            Token operator=previous();
            Expr right=unary();
            expression=new Binary(expression,operator,right);
        }
        return expression;
    }
    private Expr unary(){
        if(match(TokenType.BANG,TokenType.MINUS)){
            Token operator=previous();
            Expr right=unary();
            return new Unary(operator,right);
        }
        return primary();
    }
        private Expr primary(){
        if(match(TokenType.FALSE)) return new Literal(false);
        if(match(TokenType.TRUE)) return new Literal(true);
        if(match(TokenType.NIL)) return new Literal(null);
        if(match(TokenType.NUMBER,TokenType.STRING)){
            return new Literal(previous().literal);
        }
       // if(match(TokenType.LEFT_BRACE))
       //return null;
            //     return new Block(block());
        if(match(TokenType.IDENTIFIER)){
            return new Variable(previous());
        }
        if(match(TokenType.LEFT_PAREN)){
            Expr expression=expression();
            consume(TokenType.RIGHT_PAREN,"Expect ')' after expression.");
            return new Grouping(expression);
        }
        throw error(peek(),"Expect expression.");
    }
private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE) && !isEnd()){
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE,"Except '}' after block");
        return statements;
}

    private Token consume(TokenType type,String message){
        if(check(type)) return advance();
        throw error(peek(),message);

    }
    private ParseError error(Token token,String message){
        Lox.Error.error(token,message);
        return new ParseError();
    }
private void synchronize(){
            advance();
            while(!isEnd()){
                if(previous().type==TokenType.SEMICOLON) return;
                switch(peek().type){
                    case CLASS:
                    case FUN:
                    case VAR:
                    case FOR:
                    case IF:
                    case WHILE:
                    case PRINT:
                    case RETURN:
                        return;
                }
                advance();

            }
}
        private boolean match(TokenType... types){
        for(TokenType type:types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type){
        if(!isEnd()){
            return peek().type==type;
        }
        return false;
    }

    private boolean isEnd(){
        return peek().type==TokenType.EOF;

    }
    private Token advance(){
        if(!isEnd()) current++;
        return previous();
    }

    private Token peek(){
        return tokens.get(current);
    }
    private Token previous(){
        return tokens.get(current-1);
    }



}
