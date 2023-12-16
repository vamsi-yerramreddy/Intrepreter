package Lox;

import Interp.RuntimeError;

public class Error {
        public static boolean hadError= false;
        public static   boolean hadRuntimeError=false;

    public static void error(int line, String message){
        reportError(line," " ,message);
    }
    public static void error(Token token, String message){
        if(token.type==TokenType.EOF){
            reportError(token.line," at end",message);
        }else{
            reportError(token.line," at '"+token.lexeme+"'",message);
        }

    }

   public static void RuntimeError(RuntimeError error){
        System.err.println(error.getMessage()+"\n[line "+error.token.line   +"]");
        hadError=true;
        hadRuntimeError=true;
    }


    public static void reportError(int line, String where , String message){
        System.err.println("[line "+ line + "] Error" +where +   ": "+ message);
        hadError=true;
    }
}
