package IR;

import IR.Entity.literal.voidLiteral;
import IR.instruction.ReturnIns;
import IR.type.IRType;
import IR.Entity.variable.LocalVar;
import IR.Entity.variable.RegVar;
import ast.SingleParameter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static IR.type.IRTypes.irPtrType;
import static IR.type.IRTypes.irVoidType;

public class IRFunction {

    public boolean isClassMethod;
    public String irFuncName;

    public IRType returnType;
    public ArrayList<RegVar> parameters = new ArrayList<>();
    public HashMap<String, LocalVar> localVars = new HashMap<>();
    //    public HashMap<String,Integer> localVarsLoadNum = new HashMap<>();
    public ArrayList<BasicBlock> blocks = new ArrayList<>();
    public BasicBlock entryBlock;
    public int ifStmtNum = 0;
    public int whileStmtNum = 0;
    public int forStmtNum = 0;
    public int shortCutNum = 0;
    public int callNum = 0; //此函数本身被调用的次数
    public int arithNum = 0; //进行算数计算的次数
    public int ternaryNum = 0;

    public int newNum = 0;

    public int arrayNum = 0;

    public IRFunction(String funcName, boolean isClassMethod, IRType returnType) {
        this.irFuncName = funcName;
        this.isClassMethod = isClassMethod;
        this.returnType = returnType;
        entryBlock = new BasicBlock("entry");
        blocks.add(entryBlock);
        if(returnType.equals(irVoidType)){
            entryBlock.exitInstruction = new ReturnIns(new voidLiteral());
        }
    }

    public RegVar addPara(SingleParameter para) {
        RegVar regVar = new RegVar(IRType.toIRType(para.type), para.irVarName + "_param");
        this.parameters.add(regVar);
        return regVar;
    }

    public void addParaThis() {
        this.parameters.add(new RegVar(irPtrType, "%this_param"));
    }

    public void addBlock(BasicBlock block) {
        this.blocks.add(block);
    }

    public void addLocalVar(LocalVar var) {
        localVars.put(var.name, var);
    }

    void Print(PrintWriter pw) {
        pw.print("define " + returnType.toString() + " " + irFuncName + "(");
        for (int i = 0; i < parameters.size(); ++i) {
            pw.print(parameters.get(i).type.toString() + " " + parameters.get(i).toString());
            if (i != parameters.size() - 1) {
                pw.print(", ");
            } else {
                pw.print(") {\n");
            }
        }
        if(parameters.isEmpty()) pw.print(") {\n");
        for (var block : blocks) {
            block.print(pw);
        }
        pw.print("}\n");
    }

    void Print() {
        System.out.print("define " + returnType.toString() + " " + irFuncName + "(");
        for (int i = 0; i < parameters.size(); ++i) {
            System.out.print(parameters.get(i).type.toString() + " " + parameters.get(i).toString());
            if (i != parameters.size() - 1) {
                System.out.print(", ");
            } else {
                System.out.print(") {\n");
            }
        }
        if(parameters.isEmpty()) System.out.print(") {\n");
        for (var block : blocks) {
            block.print();
        }
        System.out.print("}\n");
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}


