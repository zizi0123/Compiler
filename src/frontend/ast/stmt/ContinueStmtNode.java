package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.util.position.Position;

public class ContinueStmtNode extends StmtNode{
    public ContinueStmtNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
