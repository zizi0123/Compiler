package IR.instruction;

import IR.Entity;
import IR.type.IRType;
import IR.variable.LocalVar;
import IR.variable.RegVar;

import java.util.ArrayList;
import java.util.Arrays;

public class CallIns extends Instruction {//todo print的时候需要考虑，如果是void就不用把result打印出来
    IRType returnType;
    String funcName;
    public ArrayList<Entity> args = new ArrayList<>();
    Entity result;

    public CallIns(String funcName, Entity result, Entity... arguments) {
        returnType = result.type;
        this.funcName = funcName;
        this.result = result;
        args.addAll(Arrays.asList(arguments));
    }

    public CallIns(IRType type,String funcName, Entity result, Entity... arguments) {
        returnType = type;
        this.funcName = funcName;
        this.result = result;
        args.addAll(Arrays.asList(arguments));
    }

    public CallIns(String funcName, Entity result) {
        returnType = result.type;
        this.funcName = funcName;
        this.result = result;
    }

}
