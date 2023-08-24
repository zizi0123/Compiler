package IR.variable;

import IR.Entity;
import IR.type.IRType;

import javax.lang.model.element.TypeElement;

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
    public String getName() {
        return name;
    }

}
