package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class UnaryExprNode extends ExprNode{
    public ExprNode exprNode;
    public String op;

    public UnaryExprNode(Position pos){
        super(pos);
    }

    @Override
    public boolean isLeftValue() {
        return false;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
