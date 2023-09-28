package ast.expr;

import IR.Entity.Entity;
import IR.Entity.variable.RegVar;
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
    public Entity irVal;  //如果这个节点的值是一个源代码定义过的变量，则储存这个变量；如果为一个临时的寄存器值，则储存这个值；如果为一个字面量，则储存这个字面量。

    public RegVar irPtr; //储存这个节点的值的指针。

}
