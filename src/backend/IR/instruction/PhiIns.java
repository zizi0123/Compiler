package backend.IR.instruction;

import backend.IR.BasicBlock;
import backend.IR.Entity.Entity;
import backend.IR.Entity.variable.LocalVar;
import backend.IR.Entity.variable.RegVar;
import backend.IR.IRVisitor;
import backend.IR.type.IRType;

import java.util.ArrayList;
import java.util.HashSet;

public class PhiIns extends Instruction {
    public LocalVar srcVar;
    public Entity result;
    IRType type;

    public BasicBlock inBlock;

    public ArrayList<Entity> values = new ArrayList<>();

    public ArrayList<BasicBlock> blocks = new ArrayList<>();

    public PhiIns(Entity result, BasicBlock inBlock) {
        this.result = result;
        type = result.type;
        this.inBlock = inBlock;
    }

    public PhiIns(LocalVar s, RegVar result, BasicBlock inBlock) {
        srcVar = s;
        this.result = result;
        type = result.type;
        this.inBlock = inBlock;
    }

    public void addPair(Entity value, BasicBlock block) {
        values.add(value);
        blocks.add(block);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(result.toString() + " = phi " + type.toString());
        for (int i = 0; i < values.size(); ++i) {
            res.append(" [ ").append(values.get(i).toString()).append(", %").append(blocks.get(i).label).append(" ]");
            if (i != values.size() - 1) res.append(", ");
        }
        return res + "\n";
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt
    @Override
    public HashSet<Entity> getUse() {
        return new HashSet<>(values);
    }

    @Override
    public Entity getDef() {
        return result;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i).equals(olde)) {
                values.remove(i);
                values.add(i, newe);
            }
        }
    }


}
