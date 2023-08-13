package util.scope;

import ast.def.ClassDefNode;
import util.type.ConstType;
import util.type.Type;

import java.util.HashMap;

public class Scope implements ConstType {
    public HashMap<String, Type> members;
    public Scope parentScope;
    public boolean isInFunction;
    public boolean isInLoop;
    public boolean hasReturned;
    public boolean isInClass;

    public Type returnType;

    public ClassDefNode classDefNode;

    public Scope(Scope parentScope){
        members = new HashMap<>();
        this.parentScope = parentScope;
        if(parentScope!=null) {
            this.isInFunction = parentScope.isInFunction;
            this.hasReturned = parentScope.hasReturned;
            this.returnType = parentScope.returnType;
            this.isInClass = parentScope.isInClass;
            this.isInLoop = parentScope.isInLoop;
            this.classDefNode = parentScope.classDefNode;
        }
    }


    public void addMember(String str,Type type){
        members.put(str,type);
    }

    public Type getTypeInAllScope(String name){
        if(members.containsKey(name)){
            return members.get(name);
        }
        if(parentScope != null){
            return parentScope.getTypeInAllScope(name);
        }else{
            return null;
        }
    }

    public boolean containInThisScope(String name){
        return members.containsKey(name);
    }


}
