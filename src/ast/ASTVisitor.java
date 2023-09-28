package ast;

import ast.expr.*;
import ast.stmt.*;
import ast.def.*;
import ast.util.type.ASTTypes;

public interface ASTVisitor extends ASTTypes {
    public void visit(ProgramNode node) ;
    public void visit(FuncDefNode node);
    public void visit(ClassDefNode node);
    public void visit(VarDefNode node) ;
    public void visit(SingleVarDefNode node) ;
    public void visit(FuncParameterListNode node);
    public void visit(ClassConstructorNode node);
    public void visit(BlockStmtNode node) ;
    public void visit(IfStmtNode node) ;
    public void visit(WhileStmtNode node) ;
    public void visit(ForStmtNode node) ;
    public void visit(ContinueStmtNode node);
    public void visit(BreakStmtNode node);
    public void visit(ReturnStmtNode node);
    public void visit(ExprStmtNode node);
    public void visit(VarDefStmtNode node);

    public void visit(ParenExprNode node);
    public void visit(ConstExprNode node);
    public void visit(VarExprNode node);
    public void visit(BinaryExprNode node);
    public void visit(UnaryExprNode node);
    public void visit(PreOpExprNode node);
    public void visit(AssignExprNode node);
    public void visit(FuncCallExprNode node);
    public void visit(ArrayExprNode node);
    public void visit(MemberExprNode node);
    public void visit(NewExprNode node);
    public void visit(TernaryExprNode node);

}
