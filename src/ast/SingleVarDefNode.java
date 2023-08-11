package ast;

import ast.expr.ExprNode;
import util.position.Position;
import util.type.Type;

public class SingleVarDefNode extends ASTNode{

    public Type type;

    public String name;

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
