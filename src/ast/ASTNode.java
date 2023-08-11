package ast;

import util.position.Position;

abstract public class ASTNode{
    public Position pos;

    public ASTNode(Position pos){
        this.pos = pos;
    }

    public abstract void accept(ASTVisitor visitor);

}

