package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Ne extends IcmpIns {
    public Ne(Entity l,Entity r,Entity res){
        super(l,r,"icmp ne",res);
    }
}
