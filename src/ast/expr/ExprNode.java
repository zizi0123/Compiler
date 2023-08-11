package ast.expr;

import ast.ASTNode;
import ast.def.FuncDefNode;
import util.position.Position;
import util.type.Type;

public abstract class ExprNode extends ASTNode {

    public Type type;

    public FuncDefNode function;


    ExprNode(Position pos){
        super(pos);
    }

    public abstract boolean isLeftValue();

}
