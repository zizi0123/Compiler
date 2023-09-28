package ast.expr;

import ast.ASTVisitor;
import ast.util.position.Position;

import java.util.ArrayList;

public class FuncCallExprNode extends ExprNode {
    public ExprNode func;

    public ArrayList<ExprNode> args = new ArrayList<>();


    public FuncCallExprNode(Position pos) {
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
