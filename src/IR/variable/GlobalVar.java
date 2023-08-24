package IR.variable;

import IR.Entity;
import IR.type.IRType;

import java.util.HashMap;
import java.util.Map;

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
    public String getName() {
        return name;
    }

}
