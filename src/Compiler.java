import backend.IR.IRBuilder;
import backend.assembly.InsSelector;
import backend.assembly.Module;
import frontend.ast.ASTBuilder;
import frontend.ast.ProgramNode;
import frontend.grammar.MxLexer;
import frontend.grammar.MxParser;
import Opt.irOptimizer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import frontend.semantic.TypeChecker;
import frontend.semantic.SymbolCollector;
import frontend.ast.util.MxErrorListener;
import frontend.ast.util.error.SemanticError;
import frontend.ast.util.error.SyntaxError;
import frontend.ast.util.scope.GlobalScope;

import java.io.IOException;
import java.io.InputStream;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Compiler {
    public static void main(String[] args) throws Exception {
        InputStream input = System.in;
        try {
            compile(input, args);
        } catch (SyntaxError syntaxError) {
            System.err.println(syntaxError.message + " in line" + syntaxError.pos.getRow() + ", column" + syntaxError.pos.getCol());
            System.exit(1);
        } catch (SemanticError semanticError) {
            System.err.println(semanticError.message + " in line" + semanticError.pos.getRow() + ", column" + semanticError.pos.getCol());
            System.exit(2);
        } catch (Exception error) {
            System.err.println("COMPILER FAIL!!");
            System.exit(3);
        }
        System.exit(0);
    }

    public static void compile(InputStream input, String[] arg) throws IOException {
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

        //symbol collect
        SymbolCollector symbolCollector = new SymbolCollector(globalScope);
        symbolCollector.visit(programNode);

        //semantic check
        TypeChecker typeChecker = new TypeChecker(globalScope);
        typeChecker.visit(programNode);

        //ir
        IRBuilder irBuilder = new IRBuilder();
        irBuilder.visit(programNode);

        //irOpt
        irOptimizer optimizer = new irOptimizer(irBuilder.irProgram);
        optimizer.work();

        if (arg[0].equals("-ir")) {
            irBuilder.irProgram.Print();
        } else if(arg[0].equals("-S")){
            //   asm
            Module module = new Module();
            InsSelector insSelector = new InsSelector(module);
            insSelector.visit(irBuilder.irProgram);
        }
    }
}