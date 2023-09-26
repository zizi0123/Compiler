package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;

public class IfStmtNode extends StmtNode {
    public ExprNode condition;
    public StmtNode trueStmts;
    public StmtNode falseStmts;

    public IfStmtNode(Position pos){
        super(pos);
        falseStmts = null;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
