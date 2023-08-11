package ast.expr;

import ast.ASTVisitor;
import util.position.Position;

public class MemberExprNode extends ExprNode{
    public ExprNode obj;

    public String memberName;

    public MemberExprNode(Position pos,String memberName){
        super(pos);
        this.memberName = memberName;
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
