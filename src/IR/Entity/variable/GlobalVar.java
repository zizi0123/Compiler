package IR.Entity.variable;

import IR.Entity.Entity;
import IR.type.IRType;

public class GlobalVar extends Entity {

    public String name;
    public boolean InitFunc = false;

    public int loadNum;
    public Entity initVal;


    public GlobalVar(String name, IRType type){
        this.name = name;
        this.type = type; //指向元素的类型
        loadNum = 0;
    }
    @Override
    public String toString() {
        return name;
    }

}
