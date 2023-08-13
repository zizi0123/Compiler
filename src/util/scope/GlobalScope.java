package util.scope;

import ast.SingleParameter;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import util.error.SemanticError;
import util.position.Position;
import util.type.ConstType;
import util.type.Type;
import util.type.TypeName;

import java.util.HashMap;

public class GlobalScope implements ConstType {
    public HashMap<String, Type> members = new HashMap<>();

    public GlobalScope() {
        this.addFunc(print);
        this.addFunc(println);
        this.addFunc(printInt);
        this.addFunc(printlnInt);
        this.addFunc(getString);
        this.addFunc(getInt);
        this.addFunc(toString);
    }

    static FuncDefNode print = new FuncDefNode(null, "print", voidType, 1, new SingleParameter(stringType, "str"), null);
    static FuncDefNode println = new FuncDefNode(null, "println", voidType, 1, new SingleParameter(stringType, "str"), null);
    static FuncDefNode printInt = new FuncDefNode(null, "printInt", voidType, 1, new SingleParameter(intType, "n"), null);
    static FuncDefNode printlnInt = new FuncDefNode(null, "printlnInt", voidType, 1, new SingleParameter(intType, "n"), null);
    static FuncDefNode getString = new FuncDefNode(null, "getString", stringType, 0, null, null);
    static FuncDefNode getInt = new FuncDefNode(null, "getInt", intType, 0, null, null);
    static FuncDefNode toString = new FuncDefNode(null, "toString", stringType, 1, new SingleParameter(intType, "i"), null);

    public static FuncDefNode size = new FuncDefNode(null, "size", intType, 0, null, null);
    public static FuncDefNode length = new FuncDefNode(null, "length", intType, 0, null, null);
    public static FuncDefNode substring = new FuncDefNode(null, "substring", stringType, 2, new SingleParameter(intType, "left"), new SingleParameter(intType, "right"));
    public static FuncDefNode parseInt = new FuncDefNode(null, "parseInt", intType, 0, null, null);
    public static FuncDefNode ord = new FuncDefNode(null, "ord", intType, 1, new SingleParameter(intType, "pos"), null);



    public HashMap<String, ClassDefNode> classes = new HashMap<>();

    public HashMap<String, FuncDefNode> functions = new HashMap<>();

    public FuncDefNode getFunc(String name) {
        return functions.get(name);
    }

    public ClassDefNode getClass(String name) {
        return classes.get(name);
    }

    public void addFunc(FuncDefNode funcDefNode) {
        functions.put(funcDefNode.funcName, funcDefNode);
    }

    public void checkType(Type type, Position pos) {
        if (type.typeName.isClass && (!classes.containsKey(type.typeName.typeName))) {
            throw new SemanticError("invalid type: " + type.typeName.typeName,pos);
        }
    }
     public void checkTypeName(TypeName typeName,Position pos){
         if (typeName.isClass && (!classes.containsKey(typeName.typeName))) {
             throw new SemanticError("invalid type: " + typeName.typeName,pos);
         }
     }
}
