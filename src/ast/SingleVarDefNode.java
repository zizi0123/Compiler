package ast;

import ast.expr.ExprNode;
import ast.util.position.Position;
import ast.util.type.ASTType;

public class SingleVarDefNode extends ASTNode{

    public ASTType type;

    public String name;
    public String irVarName;

    public ExprNode expr; //assign

    public SingleVarDefNode(Position pos, String name){
        super(pos);
        this.name = name;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
