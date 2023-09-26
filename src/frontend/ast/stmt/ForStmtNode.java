package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.def.VarDefNode;
import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;

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
