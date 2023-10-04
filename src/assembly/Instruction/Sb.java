package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.*;

import java.util.HashSet;

public class Sb extends ASMIns {
    public Reg rd;
    public Reg rs;
    public Reg rt;
    public Imm offset;
    public GlobalVal symbol;


    public Sb(Reg rs, Reg rd, Imm offset, String c) {
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
    }

    public Sb(Reg rs, GlobalVal symbol, Reg rt, String c) {
        this.rs = rs;
        this.symbol = symbol;
        comment = c;
        this.rt = rt;
    }

    @Override
    public String toString() {
        String result = "sb  " + rs.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString() + ", " + rt.toString();
        } else {
            result += offset.toString() + "(" + rd.toString() + ")";
        }
        result += "       #" + comment;
        return result;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        result.add(rs);
        if(rd!=null){
            result.add(rd);
        }
        if(rt!=null){
            result.add(rt);
        }
        return result;
    }

    @Override
    public Reg getDef() {
        if (rt != null) {
            return rt;
        } else {
            return null;
        }
    }
    @Override
    public void replace(Reg olde, Reg newe) {
        rs = (rs == olde) ? newe : rs;
        if (rd != null) {
            rd = (rd == olde) ? newe : rd;
        }
        if (rt != null) {
            rt = (rt == olde) ? newe : rt;
        }
    }


}
