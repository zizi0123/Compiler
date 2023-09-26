package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.def.VarDefNode;
import frontend.ast.util.position.Position;

public class VarDefStmtNode extends StmtNode{

    public VarDefNode varDefNode;

    public VarDefStmtNode(VarDefNode varDefNode, Position pos){
        super(pos);
        this.varDefNode = varDefNode;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
