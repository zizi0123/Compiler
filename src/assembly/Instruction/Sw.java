package assembly.Instruction;

import assembly.operand.*;

public class Sw extends ASMIns {
    Reg rd;
    Reg rs;
    Reg rt;
    Imm offset;

    GlobalVal symbol;


    public Sw(Reg rs, Reg rd, Imm offset, String c) {
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
    }

    public Sw(Reg rs, GlobalVal symbol, Reg rt, String c) {
        this.rs = rs;
        this.symbol = symbol;
        comment = c;
        this.rt = rt;
    }

    @Override
    public String toString() {
        String result = "sw  " + rs.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString() + ", " + rt.toString();
        } else {
            result += offset.toString() + "(" + rd.toString() + ")";
        }
        result += "       #" + comment+'\n';
        return result;
    }
}
