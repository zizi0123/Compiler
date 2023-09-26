package backend.IR.Entity.variable;

import backend.IR.Entity.Entity;
import backend.IR.type.IRType;

public class LocalVar extends Entity {
    public IRType type;

    public String name;
    public int loadNum;

    public LocalVar(String name,IRType type){
        this.type = type;
        this.name = name;
        loadNum = 0;
    }

    @Override
    public String toString() {
        return name;
    }

}
