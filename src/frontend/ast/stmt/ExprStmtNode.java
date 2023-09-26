package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;

public class ExprStmtNode extends StmtNode{
    public ExprNode expr;

    public ExprStmtNode(Position pos,ExprNode expr){
        super(pos);
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
