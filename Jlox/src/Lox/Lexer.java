package Lox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    public final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String,TokenType> keywords;
    private int start=0;
    private int current=0;
    private int line=1;
    static {
        keywords = new HashMap<>();
        keywords.put("and",TokenType.AND);
        keywords.put("class",TokenType.CLASS);
        keywords.put("else",TokenType.ELSE);
        keywords.put("false",TokenType.FALSE);
        keywords.put("for",TokenType.FOR);
        keywords.put("fun",TokenType.FUN);
        keywords.put("if",TokenType.IF);
        keywords.put("nil",TokenType.NIL);
        keywords.put("or",TokenType.OR);
        keywords.put("print",TokenType.PRINT);
        keywords.put("return",TokenType.RETURN);
        keywords.put("super",TokenType.SUPER);
        keywords.put("this",TokenType.THIS);
        keywords.put("true",TokenType.TRUE);
        keywords.put("var",TokenType.VAR);
        keywords.put("while",TokenType.WHILE);
        keywords.put("true",TokenType.TRUE);
    }

    public Lexer(String source){
        this.source=source;
    }

    public List<Token> scanTokens(){
            while(!isEnd()){
                start = current;
                scanToken();
            }
        tokens.add(new Token(TokenType.EOF," ",null,line));
        return tokens;
    }
    private boolean isEnd(){

        return current>=source.length();
    }
    private  void scanToken(){
        char c=advance();
        switch(c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '!':
                addToken(match('=')?TokenType.BANG_EQUAL:TokenType.BANG);
                break;
            case'=':
                    addToken(match('=')?TokenType.EQUAL_EQUAL:TokenType.EQUAL);
                    break;
            case'<':
                addToken(match('=')?TokenType.LESS_EQUAL:TokenType.LESS);
                break;
            case'>':
                addToken(match('=')?TokenType.GREATER_EQUAL:TokenType.GREATER);
                break;
            case 'o':
                if(match('r')) {
                    addToken(TokenType.OR);
                }
                break;
                case '/':
                    if(match('/')){
                        while(peek()!='\n' && !isEnd())advance();
                    }else{
                        addToken(TokenType.SLASH);
                    }
                    break;
                    //calculating the string literals
            case'"':
                string();
                break;
            case'\n':
                line++;
                break;
                //we are ignoring whitespaces
                case ' ':
                    case'\r':
                        case'\t':
                            break;

            default:
                //report error
                if(isDigit(c)) {
                    number();
                }else if (isAlpha(c)) {
                    identifier();
                }else{
                    Error.reportError(line,"","Unexpected character");
                }
                    break;
        }
    }
    private boolean isAlpha(char c){

        return (c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_';
    }
    private void identifier(){
        while(isAlphaNumeric(peek()))advance();
        String text = source.substring(start,current);
        TokenType type = keywords.get(text);
        if(type==null)type=TokenType.IDENTIFIER;//if the token is not a keyword then it is an identifier
        addToken(type);
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }
    private boolean isDigit(char c){
        return c>='0' && c<='9';
    }
    private void number(){
        while(isDigit(peek()))advance();
        if(peek()=='.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek()))advance();
        }
        addToken(TokenType.NUMBER,Double.parseDouble(source.substring(start,current)));
    }
    private char peekNext(){
        if(current+1>=source.length())return '\0';
        return source.charAt(current+1);
    }
    private void string (){
        while(peek()!='"' && !isEnd()){
            if(peek()=='\n')line++;
            advance();
        }
        if(isEnd()){
            System.out.println("Unterminated string");
            return;
        }
        advance();
        String value = source.substring(start+1,current-1);
        addToken(TokenType.STRING,value);
    }
    private char peek(){
       if(isEnd())return '\0';
         return source.charAt(current);

    }
    private boolean match(char expected){
        if(isEnd())return false;
        if(source.charAt(current)!=expected)return false;
        current++;
        return true;
    }

    private  char advance(){
        return source.charAt(current++);
    }

    private  void addToken(TokenType type){
        addToken(type,null);
    }
    private  void addToken(TokenType type, Object literal){
        String text = source.substring(start,current);
        tokens.add(new Token(type,text,literal,line));
    }


}

