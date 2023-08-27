package IR.instruction;

import IR.Entity.Entity;
import IR.type.IRType;
import IR.Entity.variable.GlobalVar;
import IR.Entity.variable.LocalVar;
import IR.Entity.variable.RegVar;

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
            value = new RegVar(type, "%" + globalVar.name.substring(1) + "_val." + loadNum);
            ++globalVar.loadNum;
        } else {
            throw new RuntimeException();
        }
    }

    public LoadIns(String ptrName, IRType type, String valueName) {  //从一个指针寄存器变量中Load信息
        ptrVal = ptrName;
        this.type = type;
        this.value = new RegVar(type, valueName);
    }


    @Override
    public void Print() {
        System.out.println(value.toString() + " = load " + type.toString() + ", ptr " + ptrVal);
    }
}
