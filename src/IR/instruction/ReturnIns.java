package IR.instruction;

import IR.Entity;
import IR.type.IRType;
import ast.expr.ParenExprNode;
import ast.expr.TernaryExprNode;

public class ReturnIns extends Instruction{
    IRType type;
    Entity value;

    public ReturnIns(Entity val){
        this.type = val.type;
        this.value = val;
    }
}
