package Lox;

import Interp.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public final Map<String, Object> values= new HashMap<>();
    public Environment enclosing;

    //Global scope
    public Environment(){
        enclosing=null;
    }
    //local scope
    public Environment(Environment enclosing){
        this.enclosing = enclosing;

    }
    public void define(String name, Object value){
        values.put(name, value);
    }
  public   Object get (Token token){
        if(values.containsKey(token.lexeme)){
            return values.get(token.lexeme);
        }
        if(enclosing!=null) return enclosing.get(token);

        throw new RuntimeError(token, "Undefined variable '"+token.lexeme+"'.");
    }

    public void assign(Token name, Object value) {
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
           // return;
        }
        if(enclosing!=null){
            enclosing.assign(name,value);
           // return ;

        }
        throw new RuntimeError(name, "Undefined variable '"+name.lexeme+"'.");

    }
}
