package backend.IR.Entity.variable;

import backend.IR.Entity.Entity;
import backend.IR.type.IRType;

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
