package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;
import IR.Entity.variable.RegVar;

import java.util.HashSet;

public class GetElementPtrIns extends Instruction {
    public IRType type;

    public Entity ptrVal;

    public Entity idx1;

    public Entity idx2;

    public RegVar valuePtr;

    public GetElementPtrIns(IRType baseType, Entity ptrVal, Entity idx1, Entity idx2, RegVar value) {
        this.type = baseType;
        this.ptrVal = ptrVal;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.valuePtr = value;
    }


    @Override
    public String toString() {
        if (idx2 != null) {
            return valuePtr.name + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString() + ", " + idx2.type.toString() + " " + idx2.toString() + '\n';
        } else {
            return valuePtr.name + " = getelementptr " + type.toString() + ", ptr " + ptrVal.toString() + ", " + idx1.type.toString() + " " + idx1.toString() + '\n';
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt

    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(ptrVal);
        result.add(idx1);
        result.add(idx2);
        return result;
    }

    @Override
    public Entity getDef() {
        return ptrVal;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        ptrVal = ptrVal.equals(olde) ? newe : ptrVal;
        idx1 = idx1.equals(olde) ? newe : idx1;
        if(idx2!=null) idx2 = idx2.equals(olde) ? newe : idx2;
    }

}
