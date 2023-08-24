package IR.instruction;

import IR.Entity;
import IR.literal.Literal;
import IR.type.IRType;
import IR.variable.RegVar;

public class StoreIns extends Instruction{
    public Entity val;  //可能是一个寄存器变量，也可能是一个字面量
    public String ptrName;

    public IRType type;

    public StoreIns(Entity val,String ptrName){
        this.val = val;
        this.ptrName = ptrName;
        this.type = val.type;
    }
}
