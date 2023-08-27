package IR.instruction;

import IR.Entity.Entity;
import IR.type.IRType;
import IR.Entity.variable.RegVar;

import static IR.type.IRTypes.irPtrType;

public class GetElementPtrIns extends Instruction {
    IRType type;

    Entity ptrVal;

    Entity idx1;

    Entity idx2;

    public String valuePtrName;

    public GetElementPtrIns(IRType baseType, Entity ptrVal, Entity idx1, Entity idx2, String valueName) {
        this.type = baseType;
        this.ptrVal = ptrVal;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.valuePtrName = valueName;
    }


    @Override
    public void Print() {
        if (idx2 != null) {
            System.out.println(valuePtrName + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString() + ", " + idx2.type.toString() + " " + idx2.toString());
        } else {
            System.out.println(valuePtrName + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString());
        }
    }
}
