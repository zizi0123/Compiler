package ast;

import ast.util.type.ASTType;

public class SingleParameter  {
    public ASTType type;
    public String varName;

    public String irVarName;

    public SingleParameter(ASTType type, String name){
        this.type = type;
        this.varName = name;
    }

}
