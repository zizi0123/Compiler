package ast.stmt;

import ast.ASTVisitor;
import util.position.Position;

public class ContinueStmtNode extends StmtNode{
    public ContinueStmtNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}