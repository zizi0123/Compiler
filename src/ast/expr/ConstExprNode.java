package ast.expr;

import ast.ASTVisitor;
import util.constValue.ConstValue;
import util.position.Position;

public class ConstExprNode extends ExprNode{
    public ConstValue value;

    @Override
    public boolean isLeftValue() {
        return false;
    }

    public ConstExprNode(Position pos, ConstValue value){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
