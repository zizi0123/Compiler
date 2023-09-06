package assembly.Instruction;

import assembly.operand.GlobalVal;
import assembly.operand.Imm;
import assembly.operand.Reg;

public class Lb extends ASMIns {
    Reg rd;
    Reg rs;
    Imm offset;

    GlobalVal symbol;

    public Lb(Reg rd, Reg rs, Imm offset, String c) {
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
    }

    public Lb(Reg rd, GlobalVal symbol, String c) {
        this.rd = rd;
        this.symbol = symbol;
        comment = c;
    }

    @Override
    public String toString() {
        String result = "lb  " + rd.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString() + '\n';
        } else {
            result += offset.toString() + "(" + rs.toString() + ")\n";
        }
        return result;
    }
}
