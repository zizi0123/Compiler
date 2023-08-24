package IR.instruction;

import IR.BasicBlock;
import IR.Entity;
import IR.type.IRType;
import java.util.ArrayList;

public class PhiIns extends Instruction{
    public Entity result;
    IRType type;

    ArrayList<Entity> values;

    ArrayList<BasicBlock> blocks;

    public PhiIns(Entity result){
        this.result = result;
        type = result.type;
    }

    public void addPair(Entity value,BasicBlock block){
        values.add(value);
        blocks.add(block);
    }
}
