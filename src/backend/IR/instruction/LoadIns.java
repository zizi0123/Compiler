package backend.IR.instruction;

import backend.IR.Entity.Entity;
import backend.IR.IRVisitor;
import backend.IR.type.IRType;
import backend.IR.Entity.variable.GlobalVar;
import backend.IR.Entity.variable.LocalVar;
import backend.IR.Entity.variable.RegVar;

import java.util.HashSet;

public class LoadIns extends Instruction {
    public Entity value;
    public Entity ptr;

    public IRType type;

    public LoadIns(Entity target) {
        //target为localVar或globalVar
        if (target instanceof LocalVar localVar) {
            this.ptr = localVar;
            type = localVar.type;
            int loadNum = localVar.loadNum;
            value = new RegVar(type, localVar.name + "_val." + loadNum);
            ++localVar.loadNum;
        } else if (target instanceof GlobalVar globalVar) {
            this.ptr = globalVar;
            type = globalVar.type;
            int loadNum = globalVar.loadNum;
            value = new RegVar(type, "%" + globalVar.name.substring(1) + "_val." + loadNum);
            ++globalVar.loadNum;
        } else {
            throw new RuntimeException();
        }
    }

    public LoadIns(Entity ptrName, IRType type, RegVar value) {  //从一个指针寄存器变量中Load信息
        this.ptr = ptrName;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString() + " = load " + type.toString() + ", ptr " + ptr + '\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt
    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(ptr);
        return result;
    }

    @Override
    public Entity getDef() {
        return value;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        ptr = ptr.equals(olde) ? newe : ptr;
    }
}

