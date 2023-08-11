package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class TernaryExprNode extends ExprNode{
    public ExprNode lExpr;

    public ExprNode mExpr;

    public ExprNode rExpr;

    public TernaryExprNode(Position pos){
        super(pos);
    }

    @Override
    public boolean isLeftValue() {
        return mExpr.isLeftValue();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
