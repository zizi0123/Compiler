package ast.stmt;

import ast.ASTNode;
import ast.util.position.Position;

public abstract class StmtNode extends ASTNode {

    public StmtNode(Position pos){
        super(pos);
    }

}