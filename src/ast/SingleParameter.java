package ast;

import util.type.Type;

public class SingleParameter  {
    public Type type;
    public String varName;

    public SingleParameter(Type type, String name){
        this.type = type;
        this.varName = name;
    }

}
