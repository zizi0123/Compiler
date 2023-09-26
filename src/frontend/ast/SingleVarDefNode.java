package frontend.ast;

import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;
import frontend.ast.util.type.ASTType;

public class SingleVarDefNode extends ASTNode{

    public ASTType type;

    public String name;
    public String irVarName;

    public ExprNode expr; //assign

    SingleVarDefNode(Position pos, String name){
        super(pos);
        this.name = name;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
