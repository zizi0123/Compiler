package frontend.ast.expr;

import frontend.ast.ASTVisitor;
import frontend.ast.util.position.Position;

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
