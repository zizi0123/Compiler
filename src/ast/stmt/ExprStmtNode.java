package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import ast.util.position.Position;

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
