package IR.instruction;

import IR.BasicBlock;
import IR.Entity;
import IR.IRBuilder;
import IR.type.IRIntType;
import IR.type.IRType;
import IR.variable.LocalVar;

import static IR.type.IRTypes.irIntType;

public class BinaryOperationIns extends Instruction {
    public Entity result;
    public String operator;

    public IRType type;
    public Entity operand1;
    public Entity operand2;

    static int addCnt = 0;
    static int subCnt = 0;
    static int mulCnt = 0;
    static int sdivCnt = 0;
    static int sremCnt = 0;
    static int shlCnt = 0;
    static int ashrCnt = 0;
    static int andCnt = 0;
    static int orCnt = 0;
    static int xorCnt = 0;

    public BinaryOperationIns(Entity op1, Entity op2, String op, IRType type){
        operand1 = op1;
        operand2 = op2;
        operator = op;
        this.type = type;
        int num;
        switch (op) {
            case "add" -> num = ++addCnt;
            case "sub" -> num = ++subCnt;
            case "mul" -> num = ++mulCnt;
            case "sdiv" -> num = ++sdivCnt;
            case "srem" -> num = ++sremCnt;
            case "shl" -> num = ++shlCnt;
            case "ashr" -> num = ++ashrCnt;
            case "and" -> num = ++andCnt;
            case "or" -> num = ++orCnt;
            default -> num = ++xorCnt;
        }
        result = new LocalVar(op+"_result."+num, irIntType);
    }







}
