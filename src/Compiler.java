import IR.IRBuilder;
import IR.IRProgram;
import assembly.InsSelector;
import assembly.Module;
import ast.ASTBuilder;
import ast.ProgramNode;
import grammar.MxLexer;
import grammar.MxParser;
import irOpt.Mem2Reg;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import semantic.TypeChecker;
import semantic.SymbolCollector;
import ast.util.MxErrorListener;
import ast.util.error.SemanticError;
import ast.util.error.SyntaxError;
import ast.util.scope.GlobalScope;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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
//        if (arg.length > 1 && arg[1].equals("-mem2reg")) {
            new Mem2Reg(irBuilder.irProgram).Mem2RegOpt();
//        }
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