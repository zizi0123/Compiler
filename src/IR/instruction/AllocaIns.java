package IR.instruction;

import IR.type.IRType;
import IR.variable.LocalVar;
import IR.variable.RegVar;

public class AllocaIns extends Instruction{
    public IRType type;
    public String varName;



    public AllocaIns(LocalVar var){
        this.type = var.type;
        this.varName = var.name;
    }

    public AllocaIns(RegVar var,IRType type){ //var:ptr regvar
        this.type = type;
        this.varName = var.name;
    }

}
