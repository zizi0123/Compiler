package ast.expr;

import ast.ASTVisitor;
import ast.util.constValue.ConstValue;
import ast.util.position.Position;

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
