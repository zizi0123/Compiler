package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;
import IR.Entity.variable.RegVar;

import static IR.type.IRTypes.irPtrType;

public class GetElementPtrIns extends Instruction {
    public IRType type;

    public Entity ptrVal;

    public Entity idx1;

    public Entity idx2;

    public String valuePtrName;

    public GetElementPtrIns(IRType baseType, Entity ptrVal, Entity idx1, Entity idx2, String valueName) {
        this.type = baseType;
        this.ptrVal = ptrVal;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.valuePtrName = valueName;
    }


    @Override
    public String toString() {
        if (idx2 != null) {
            return valuePtrName + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString() + ", " + idx2.type.toString() + " " + idx2.toString() + '\n';
        } else {
            return valuePtrName + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString() + '\n';
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


}
