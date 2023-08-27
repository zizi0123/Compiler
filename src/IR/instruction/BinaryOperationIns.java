package IR.instruction;

import IR.Entity.Entity;
import IR.Entity.variable.RegVar;
import IR.type.IRType;
import IR.Entity.variable.LocalVar;

import static IR.type.IRTypes.irIntType;

public class BinaryOperationIns extends Instruction {
    public Entity result;
    public String operator;

    public IRType type;
    public Entity operand1;
    public Entity operand2;

    public BinaryOperationIns(Entity op1, Entity op2, String op,Entity result){
        operand1 = op1;
        operand2 = op2;
        operator = op;
        this.type = op1.type;
        this.result = result;
    }

    @Override
    public void Print() {
        System.out.println(result.toString()+" = "+operator+" "+type.toString()+" "+operand1.toString()+", "+operand2.toString());
    }

//    static int addCnt = 0;
//    static int subCnt = 0;
//    static int mulCnt = 0;
//    static int sdivCnt = 0;
//    static int sremCnt = 0;
//    static int shlCnt = 0;
//    static int ashrCnt = 0;
//    static int andCnt = 0;
//    static int orCnt = 0;
//    static int xorCnt = 0;

//    static int eqCnt = 0;
//    static int neCnt = 0;
//    static int ugtCnt = 0;
//    static int ugeCnt = 0;
//    static int ultCnt = 0;
//    static int uleCnt = 0;
//    static int sgtCnt = 0;
//    static int sgeCnt = 0;
//    static int sltCnt = 0;
//    static int sleCnt = 0;

//    public BinaryOperationIns(Entity op1, Entity op2, String op){
//        operand1 = op1;
//        operand2 = op2;
//        operator = op;
//        this.type = op1.type;
//        int num;
//        switch (op) {
//            case "add" -> num = ++addCnt;
//            case "sub" -> num = ++subCnt;
//            case "mul" -> num = ++mulCnt;
//            case "sdiv" -> num = ++sdivCnt;
//            case "srem" -> num = ++sremCnt;
//            case "shl" -> num = ++shlCnt;
//            case "ashr" -> num = ++ashrCnt;
//            case "and" -> num = ++andCnt;
//            case "or" -> num = ++orCnt;
//            default -> num = ++xorCnt;
//        }
//        result = new RegVar(op+"_result."+num);
//    }







}
