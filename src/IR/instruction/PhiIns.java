package IR.instruction;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;
import java.util.ArrayList;

public class PhiIns extends Instruction{
    public Entity result;
    IRType type;

    public ArrayList<Entity> values = new ArrayList<>();

    public ArrayList<BasicBlock> blocks = new ArrayList<>();

    public PhiIns(Entity result){
        this.result = result;
        type = result.type;
    }

    public void addPair(Entity value,BasicBlock block){
        values.add(value);
        blocks.add(block);
    }
    @Override
    public String toString() {
        return result.toString()+" = phi "+type.toString()+" [ "+values.get(0).toString()+", %"+blocks.get(0).label+" ], [ "+values.get(1).toString()+", %"+blocks.get(1).label+" ]\n";
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
