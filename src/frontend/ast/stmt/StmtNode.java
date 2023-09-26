package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.util.position.Position;

public abstract class StmtNode extends ASTNode {

    public StmtNode(Position pos){
        super(pos);
    }

}