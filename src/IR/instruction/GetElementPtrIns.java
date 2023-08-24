package IR.instruction;

import IR.Entity;
import IR.literal.IntLiteral;
import IR.type.IRClassType;
import IR.type.IRPtrType;
import IR.type.IRType;
import IR.variable.GlobalVar;
import IR.variable.RegVar;

import static IR.type.IRTypes.irPtrType;

public class GetElementPtrIns extends Instruction{
    IRType type;

    Entity ptrVal;

    Entity idx1;

    Entity idx2;

    public RegVar value;

    public GetElementPtrIns(IRType type,Entity ptrVal,Entity idx1,Entity idx2,String valueName){
        this.type = type;
        this.ptrVal = ptrVal;
        this.idx1 = idx1;
        this.idx2 = idx2;
        value = new RegVar(irPtrType,valueName);
    }



}
