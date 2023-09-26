package frontend.ast.expr;

import frontend.ast.ASTVisitor;
import frontend.ast.util.position.Position;

public class MemberExprNode extends ExprNode{
    public ExprNode obj;
    public String memberName;

    public String irClassName;

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
