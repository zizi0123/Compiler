package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class PreOpExprNode extends ExprNode{
    public ExprNode expr;

    public String op;

    public PreOpExprNode(Position pos){
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
