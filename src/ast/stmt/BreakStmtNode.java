package ast.stmt;

import ast.ASTVisitor;
import ast.util.position.Position;

public class BreakStmtNode extends StmtNode{
    public BreakStmtNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
