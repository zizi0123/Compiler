package ast.expr;

import IR.Entity;
import ast.ASTNode;
import ast.def.FuncDefNode;
import ast.util.position.Position;
import ast.util.type.ASTType;

public abstract class ExprNode extends ASTNode {

    public ASTType type;

    public FuncDefNode function;
    public String irFuncName;


    ExprNode(Position pos){
        super(pos);
    }

    public abstract boolean isLeftValue();

    public Entity irVal;

}
