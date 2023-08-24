package IR.instruction;

import IR.Entity;
import IR.literal.Literal;
import IR.type.IRPtrType;
import IR.type.IRType;
import IR.variable.GlobalVar;
import IR.variable.LocalVar;
import IR.variable.RegVar;

import static IR.type.IRTypes.irPtrType;

public class LoadIns extends Instruction {
    public Entity value;
    public String ptrVal;

    public IRType type;

    public LoadIns(Entity target) {
        //target为localVar或globalVar
        if (target instanceof LocalVar localVar) {
            this.ptrVal = localVar.name;
            type = localVar.type;
            int loadNum = localVar.loadNum;
            value = new RegVar(type, localVar.name + "_val." + loadNum);
            ++localVar.loadNum;
        } else if (target instanceof GlobalVar globalVar) {
            this.ptrVal = globalVar.name;
            type = globalVar.type;
            int loadNum = globalVar.loadNum;
            value = new RegVar(type, globalVar.name + "_val." + loadNum);
            ++globalVar.loadNum;
        } else {
            throw new RuntimeException();
        }
    }

    public LoadIns(RegVar ptr, IRType type, String valueName) {  //从一个指针寄存器变量中Load信息
        ptrVal = ptr.name;
        this.type = type;
        this.value = new RegVar(type, valueName);
    }


}
