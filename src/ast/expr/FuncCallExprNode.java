package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

import java.util.ArrayList;

public class FuncCallExprNode extends ExprNode {
    public ExprNode func;

    public ArrayList<ExprNode> args;

    public FuncCallExprNode(Position pos) {
        super(pos);
        args = new ArrayList<>();
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
