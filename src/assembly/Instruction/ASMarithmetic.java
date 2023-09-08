package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.*;

public class ASMarithmetic extends ASMIns {
    public Reg rd;
    public Reg rs1;
    public Reg rs2;
    public Imm imm;
    String operator;

    public ASMarithmetic(Reg rd, Reg rs1, Reg rs2, String irOperator) {
        switch (irOperator) {
            case "ashr" -> operator = "sra";
            case "sdiv" -> operator = "div";
            case "srem" -> operator = "rem";
            case "shl" -> operator = "sll";
            default -> operator = irOperator;
        }
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    public ASMarithmetic(Reg rd, Reg rs1, Reg rs2, String irOperator, String c) {
        switch (irOperator) {
            case "ashr" -> operator = "sra";
            case "sdiv" -> operator = "div";
            case "srem" -> operator = "rem";
            case "shl" -> operator = "sll";
            default -> operator = irOperator;
        }
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        comment = c;
    }

    public ASMarithmetic(Reg rd, Reg rs1, Imm imm, String irOperator) {
        switch (irOperator) {
            case "add", "sub" -> operator = "addi";
            case "and" -> operator = "andi";
            case "ashr" -> operator = "srai";
            case "or" -> operator = "ori";
            case "shl" -> operator = "slli";
            case "xor" -> operator = "xori";
            default -> throw new RuntimeException();
        }
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
        if (irOperator.equals("sub")) this.imm.val = -this.imm.val;
    }

    public ASMarithmetic(Reg rd, Reg rs1, Imm imm, String irOperator, String c) {
        switch (irOperator) {
            case "add", "sub" -> operator = "addi";
            case "and" -> operator = "andi";
            case "ashr" -> operator = "srai";
            case "or" -> operator = "ori";
            case "shl" -> operator = "slli";
            case "xor" -> operator = "xori";
            default -> throw new RuntimeException();
        }
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
        comment = c;
        if (irOperator.equals("sub")) this.imm.val = -this.imm.val;
    }

    @Override
    public String toString() {
        String result;
        result = operator + "  " + rd.toString() + ", " + rs1.toString() + ", ";
        if (imm != null) {
            result += imm.toString();
        } else {
            result += rs2.toString();
        }
        return result+'\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
