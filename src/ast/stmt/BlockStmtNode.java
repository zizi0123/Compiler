package ast.stmt;

import ast.ASTVisitor;
import util.position.Position;

import java.util.ArrayList;

public  class BlockStmtNode extends StmtNode{
    public ArrayList<StmtNode> stmts;

    public BlockStmtNode(Position pos){
        super(pos);
        stmts = new ArrayList<>();
    }

    public boolean isEmpty(){
        return stmts.isEmpty();
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
