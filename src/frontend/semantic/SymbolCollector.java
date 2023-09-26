package frontend.semantic;

import frontend.ast.*;
import frontend.ast.def.ClassDefNode;
import frontend.ast.def.FuncDefNode;
import frontend.ast.def.VarDefNode;
import frontend.ast.expr.*;
import frontend.ast.stmt.*;
import frontend.ast.util.error.SemanticError;
import frontend.ast.util.scope.GlobalScope;

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
            throw new SemanticError("function" + node.funcName + "already exists!",node.pos);
        }
        if (this.globalScope.getClass(node.funcName) != null) {
            throw new SemanticError("class" + node.funcName + "already exists!",node.pos);
        }
        this.globalScope.addFunc(node);
        node.irFuncName = "@"+node.funcName;
    }

    @Override
    public void visit(ClassDefNode node) {
        if (this.globalScope.getFunc(node.className) != null) {
            throw new SemanticError("function" + node.className + "already exists!",node.pos);
        }
        if (this.globalScope.getClass(node.className) != null) {
            throw new SemanticError("class" + node.className + "already exists!",node.pos);
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
    public void visit(VarDefStmtNode node) {

    }

    @Override
    public void visit(ParenExprNode node) {

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
