package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class VarExprNode extends ExprNode {
    public String varName;

    public VarExprNode(Position pos, String str) {
        super(pos);
        varName = str;
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
