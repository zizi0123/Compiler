package backend.IR.instruction;

import backend.IR.Entity.Entity;
import backend.IR.IRVisitor;
import backend.IR.type.IRType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static backend.IR.type.IRTypes.irVoidType;

public class CallIns extends Instruction {
    public IRType returnType;
    public String funcName;
    public ArrayList<Entity> args = new ArrayList<>();
    public Entity result;

    public CallIns(String funcName, Entity result, Entity... arguments) {
        returnType = result.type;
        this.funcName = funcName;
        this.result = result;
        args.addAll(Arrays.asList(arguments));
    }

    public CallIns(IRType type, String funcName, Entity result, Entity... arguments) {
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

    @Override
    public String toString() {
        StringBuilder resultstr;
        if (returnType.equals(irVoidType)) {
            resultstr = new StringBuilder("call void " + funcName + "(");
        } else {
            resultstr = new StringBuilder(result.toString() + " = call " + returnType.toString() + " " + funcName + "(");
        }
        for (int i = 0; i < args.size(); ++i) {
            resultstr.append(args.get(i).type.toString()).append(" ").append(args.get(i).toString());
            if (i != args.size() - 1) {
                resultstr.append(", ");
            } else {
                resultstr.append(")\n");
            }
        }
        if (args.isEmpty()) resultstr.append(")\n");
        return resultstr.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    //opt

    @Override
    public HashSet<Entity> getUse() {
        return new HashSet<>(args);
    }

    @Override
    public Entity getDef() {
        return result;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        for(int i = 0;i<args.size();++i){
            if(args.get(i).equals(olde)){
                args.remove(i);
                args.add(i,newe);
            }
        }
    }

}
