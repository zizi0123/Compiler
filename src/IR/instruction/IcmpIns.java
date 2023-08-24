package IR.instruction;

import IR.Entity;
import IR.type.IRType;
import IR.variable.LocalVar;

public class IcmpIns extends Instruction{
    LocalVar result;
    String cond;
    IRType operandType;
    Entity operand1,operand2;

    static int eqCnt = 0;
    static int neCnt = 0;
    static int ugtCnt = 0;
    static int ugeCnt = 0;
    static int ultCnt = 0;
    static int uleCnt = 0;
    static int sgtCnt = 0;
    static int sgeCnt = 0;
    static int sltCnt = 0;
    static int sleCnt = 0;

    public IcmpIns(LocalVar result,String cond,IRType operandType,Entity op1,Entity op2){
        this.result = result;
        this.cond = cond;
        this.operandType = operandType;
        operand1 = op1;
        operand2 = op2;
    }
}
