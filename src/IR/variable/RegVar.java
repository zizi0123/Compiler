package IR.variable;

import IR.Entity;
import IR.type.IRType;

public class RegVar extends Entity {

    public String name;

    public RegVar(IRType type,String name){
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
