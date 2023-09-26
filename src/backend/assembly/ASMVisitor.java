package backend.assembly;

import backend.assembly.Instruction.*;

public interface ASMVisitor {
    public void visit(Module node);

    public void visit(ASMFunction node);

    public void visit(ASMBlock node);

    public void visit(ASMarithmetic node);

    public void visit(Branch node);

    public void visit(Call node);

    public void visit(J node);

    public void visit(La node);

    public void visit(Lb node);

    public void visit(Li node);

    public void visit(Lw node);

    public void visit(Mv node);

    public void visit(Ret node);

    public void visit(Sb node);

    public void visit(Slt node);

    public void visit(Sw node);


}
