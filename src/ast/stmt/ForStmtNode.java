package ast.stmt;

import ast.ASTVisitor;
import ast.def.VarDefNode;
import ast.expr.ExprNode;
import util.position.Position;

public class ForStmtNode extends StmtNode {
    public VarDefNode initVarDef;
    public ExprNode initExpr;

    public ExprNode condition;

    public ExprNode step;

    public StmtNode stmts;

    public ForStmtNode(Position pos){
        super(pos);
        initVarDef = null;
        initExpr = null;
        condition = null;
        step = null;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
