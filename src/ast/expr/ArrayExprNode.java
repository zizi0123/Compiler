package ast.expr;

import ast.ASTVisitor;
import ast.util.position.Position;

public class ArrayExprNode extends ExprNode{
    public ExprNode array;
    public ExprNode index;

    public ArrayExprNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isLeftValue() {
        return true;
    }
}
