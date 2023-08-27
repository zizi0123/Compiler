package IR.Entity.variable;

import IR.Entity.Entity;
import IR.type.IRType;

public class RegVar extends Entity {

    public String name;

    int loadNum = 0;

    public RegVar(IRType type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getLoadNum() {
        return loadNum++;
    }

    @Override
    public String toString() {
        return name;
    }
}
