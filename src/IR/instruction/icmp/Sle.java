package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Sle extends IcmpIns {
    public Sle(Entity l,Entity r,Entity res){
        super(l,r,"icmp sle",res);
    }
}
