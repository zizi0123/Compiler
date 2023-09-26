package backend.IR;

import backend.IR.instruction.*;

public interface IRVisitor {
    public void visit(IRProgram node);

    public void visit(IRFunction node);

    public void visit(BasicBlock node);

    public void visit(AllocaIns node);

    public void visit(BranchIns node);

    public void visit(ArithmeticIns node);

    public void visit(IcmpIns node);

    public void visit(CallIns node);

    public void visit(GetElementPtrIns node);

    public void visit(LoadIns node);

    public void visit(ReturnIns node);

    public void visit(StoreIns node);

    public void visit(PhiIns node);
}