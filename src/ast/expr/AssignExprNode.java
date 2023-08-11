package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class AssignExprNode extends ExprNode{
    public ExprNode lhs;

    public ExprNode rhs;

    public AssignExprNode(Position pos){
        super(pos);
    }

    @Override
    public boolean isLeftValue() {
        return true;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
