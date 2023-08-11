package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class BinaryExprNode extends ExprNode {

    public ExprNode lhs;

    public ExprNode rhs;

    public String op;

    public BinaryExprNode(Position pos) {
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
