import Interp.Interpreter;
import Lox.Error;
import Lox.Lexer;
import Lox.Token;
import Parser.ExParser;
import Statement.Stmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Main {
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws Exception {

        if(args.length>1){
            System.out.println("Usage : jlox [script]");
            System.exit(64);
        }else if(args.length==1) {
            runFile(args[0]);
        }else{
           runPrompt();
        }
    }
    public static void runPrompt() throws IOException{
        InputStreamReader inputreader = new InputStreamReader(System.in);
        BufferedReader br  = new BufferedReader(inputreader);
        for(;;){
            System.out.print(">> ");
            String line = br.readLine();
            if(line==null)break;
            run(line);
            //we should not kill the session if there are any errors in the interactive shell
            //so resetting the variable to false;
            Error.hadError=false;
            System.out.println(">>");
        }
    }
    public static void runFile(String file) throws IOException {
        byte [] bytes = Files.readAllBytes(Paths.get(file));
        /*System.out.println("contents as bytes");
        for(byte b : bytes){
            System.out.print((char)b);
        }*/
        String fileContent = new String(bytes);//, Charset.defaultCharset());
        run(fileContent);
            if(Error.hadError)System.exit(65);
            if(Error.hadRuntimeError)System.exit(70);

    }
    public static void run(String content){
        Lexer sc = new Lexer(content);
        List<Token> tokens = sc.scanTokens();
        ExParser parser= new ExParser(tokens);
        List<Stmt>statements = parser.parse();

        //Expression expression = parser.parse();
        if(Error.hadError)  return;
        interpreter.interpret(statements);
        //System.out.println(new AstPrinter().print(expression));

      //  for(Token token : tokens){
          //  System.out.println(token);
        //}


       // System.out.println();
    }

}