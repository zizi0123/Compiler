package IR;

import IR.type.IRType;
import IR.variable.LocalVar;
import IR.variable.RegVar;
import ast.SingleParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static IR.type.IRTypes.irPtrType;

public class IRFunction {

    public boolean isClassMethod;
    String irFuncName;

    public IRType returnType;
    ArrayList<RegVar> parameters = new ArrayList<>();
    public HashMap<String, LocalVar> localVars = new HashMap<>();
//    public HashMap<String,Integer> localVarsLoadNum = new HashMap<>();
    HashMap<String, BasicBlock> blocks = new HashMap<>();
    public BasicBlock entryBlock;
    int ifStmtNum = 0;
    int whileStmtNum = 0;
    int forStmtNum = 0;
    int shortCutNum = 0;
    int retNum = 0;
    int callNum = 0; //此函数本身被调用的次数
    int arithNum = 0; //进行算数计算的次数
    int ternaryNum = 0;

    int newNum = 0;

    int arrayNum = 0;
    public IRFunction(String funcName, boolean isClassMethod, IRType returnType) {
        this.irFuncName = funcName;
        this.isClassMethod = isClassMethod;
        this.returnType = returnType;
        entryBlock = new BasicBlock("entry");
        blocks.put(entryBlock.label, entryBlock);
    }

    public RegVar addPara(SingleParameter para) {
        RegVar regVar = new RegVar(IRType.toIRType(para.type), para.irVarName + "_param");
        this.parameters.add(regVar);
        return regVar;
    }

    public void addParaThis() {
        this.parameters.add(new RegVar(irPtrType, "this_param"));
    }

    public void addBlock(BasicBlock block) {
        this.blocks.put(block.label, block);
    }

    public void addLocalVar(LocalVar var){
        localVars.put(var.name,var);
    }

}


