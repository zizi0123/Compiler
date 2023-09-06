package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;
import IR.type.IRVoidType;

import java.util.ArrayList;
import java.util.Arrays;

import static IR.type.IRTypes.irVoidType;

public class CallIns extends Instruction {
    IRType returnType;
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
    public void Print() {
        if (returnType.equals(irVoidType)) {
            System.out.print("call void " + funcName + "(");
        } else {
            System.out.print(result.toString() + " = call " + returnType.toString() + " " + funcName + "(");
        }
        for (int i = 0; i < args.size(); ++i) {
            System.out.print(args.get(i).type.toString() + " " + args.get(i).toString());
            if (i != args.size() - 1) {
                System.out.print(", ");
            } else {
                System.out.println(")");
            }
        }
        if (args.isEmpty()) System.out.println(")");

    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
