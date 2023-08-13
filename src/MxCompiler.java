import ast.ASTBuilder;
import ast.ProgramNode;
import grammar.MxLexer;
import grammar.MxParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import semantic.SemanticChecker;
import semantic.SymbolCollector;
import util.MxErrorListener;
import util.error.SemanticError;
import util.error.SyntaxError;
import util.scope.GlobalScope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class MxCompiler {
    public static void main(String[] args) throws Exception {
        InputStream input = System.in;
//        boolean online = true;
//
//        if (!online) { //local
//            input = new FileInputStream("test.mx");
//        }

        try {
            compile(input);
        } catch (SyntaxError syntaxError) {
            System.err.println(syntaxError.message + "in line" + syntaxError.pos.getRow() + ", column" + syntaxError.pos.getCol());
            System.exit(1);
        } catch (SemanticError semanticError) {
            System.err.println(semanticError.message + "in line" + semanticError.pos.getRow() + ", column" + semanticError.pos.getCol());
            System.exit(2);
        } catch (Exception error) {
            System.err.println("COMPILER FAIL!!");
            System.exit(3);
        }
    }

    public static void compile(InputStream input) throws IOException {
        MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxErrorListener());
        MxParser parser = new MxParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new MxErrorListener());
        MxParser.ProgramContext parseTreeRoot = parser.program();
        //build AST
        ASTBuilder astBuilder = new ASTBuilder();
        ProgramNode programNode;
        programNode = (ProgramNode) astBuilder.visitProgram(parseTreeRoot);
        GlobalScope globalScope = new GlobalScope();
        System.out.println("AST build finish!");

        //symbol collect
        SymbolCollector symbolCollector = new SymbolCollector(globalScope);
        symbolCollector.visit(programNode);
        System.out.println("symbol collect finish!");

        //semantic check
        SemanticChecker semanticChecker = new SemanticChecker(globalScope);
        semanticChecker.visit(programNode);
        System.out.println("semantic check finish!");

    }
}