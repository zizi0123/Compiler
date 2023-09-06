package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Ne extends IcmpIns {
    public Ne(Entity l,Entity r,Entity res){
        super(l,r,"icmp ne",res);
    }
}
