package frontend.ast.expr;

import frontend.ast.ASTVisitor;
import frontend.ast.util.position.Position;

public class ParenExprNode extends ExprNode{

    public ExprNode exprNode;

    public ParenExprNode(Position pos,ExprNode expr){
        super(pos);
        exprNode = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isLeftValue() {
        return exprNode.isLeftValue();
    }
}
