package semantic;

import ast.*;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import util.error.SemanticError;
import util.scope.GlobalScope;

public class SymbolCollector implements ASTVisitor {

    public GlobalScope globalScope;

    public SymbolCollector(GlobalScope globalScope){
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        for (var def : node.defNodes) {
            def.accept(this);
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        if (this.globalScope.getFunc(node.funcName) != null) {
            throw new SemanticError("function" + node.funcName + "already exists!");
        }
        if (this.globalScope.getClass(node.funcName) != null) {
            throw new SemanticError("class" + node.funcName + "already exists!");
        }
        this.globalScope.addFunc(node);
    }

    @Override
    public void visit(ClassDefNode node) {
        if (this.globalScope.getFunc(node.className) != null) {
            throw new SemanticError("function" + node.className + "already exists!");
        }
        if (this.globalScope.getClass(node.className) != null) {
            throw new SemanticError("class" + node.className + "already exists!");
        }
        this.globalScope.classes.put(node.className, node);
    }

    @Override
    public void visit(VarDefNode node) {

    }

    @Override
    public void visit(SingleVarDefNode node) {

    }

    @Override
    public void visit(FuncParameterListNode node) {

    }

    @Override
    public void visit(ClassConstructorNode node) {

    }

    @Override
    public void visit(BlockStmtNode node) {

    }

    @Override
    public void visit(IfStmtNode node) {

    }

    @Override
    public void visit(WhileStmtNode node) {

    }

    @Override
    public void visit(ForStmtNode node) {

    }

    @Override
    public void visit(ContinueStmtNode node) {

    }

    @Override
    public void visit(BreakStmtNode node) {

    }

    @Override
    public void visit(ReturnStmtNode node) {

    }

    @Override
    public void visit(ExprStmtNode node) {

    }

    @Override
    public void visit(ConstExprNode node) {

    }

    @Override
    public void visit(VarExprNode node) {

    }

    @Override
    public void visit(BinaryExprNode node) {

    }

    @Override
    public void visit(UnaryExprNode node) {

    }

    @Override
    public void visit(PreOpExprNode node) {

    }

    @Override
    public void visit(AssignExprNode node) {

    }

    @Override
    public void visit(FuncCallExprNode node) {

    }

    @Override
    public void visit(ArrayExprNode node) {

    }

    @Override
    public void visit(MemberExprNode node) {

    }

    @Override
    public void visit(NewExprNode node) {

    }

    @Override
    public void visit(TernaryExprNode node) {

    }
}