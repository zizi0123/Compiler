package ast.util.scope;

import IR.Entity;
import ast.def.ClassDefNode;
import ast.util.type.ASTTypes;
import ast.util.type.ASTType;

import java.util.HashMap;

public class Scope implements ASTTypes {

    public HashMap<String, ASTType> vars = new HashMap<>();
    public Scope parentScope;
    public boolean isInFunction;
    public boolean isInLoop;
    public boolean hasReturned;
    public boolean isInClass;

    public ASTType returnType;
    public ClassDefNode classDefNode;

    public int scopeNum;
    static int scopeCnt = 0;


    //--------------------------------------------------IR------------------------------------------------------

    public Scope(Scope parentScope) {
        ++scopeCnt;
        scopeNum = scopeCnt;
        this.parentScope = parentScope;
        if (parentScope != null) {
            this.isInFunction = parentScope.isInFunction;
            this.hasReturned = parentScope.hasReturned;
            this.returnType = parentScope.returnType;
            this.isInClass = parentScope.isInClass;
            this.isInLoop = parentScope.isInLoop;
            this.classDefNode = parentScope.classDefNode;
        }
    }


    public void addVar(String str, ASTType type) {
        vars.put(str, type);
    }

    public ASTType getTypeInAllScope(String name) {
        if (vars.containsKey(name)) {
            return vars.get(name);
        }
        if (parentScope != null) {
            return parentScope.getTypeInAllScope(name);
        } else {
            return null;
        }
    }

    public String getIRNameInAllScope(String name) {
        if (vars.containsKey(name)) {
            if (parentScope != null) {
                return "%" + name + "." + scopeNum;
            } else {
                return "@" + name;
            }
        }
        if (parentScope != null) {
            return parentScope.getIRNameInAllScope(name);
        } else {
            return null;
        }
    }

    public boolean containInThisScope(String name) {
        return vars.containsKey(name);
    }


}
