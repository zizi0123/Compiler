package ast.stmt;

import ast.ASTVisitor;
import ast.def.VarDefNode;
import util.position.Position;

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
