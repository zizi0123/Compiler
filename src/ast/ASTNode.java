package ast;

import ast.util.position.Position;

import java.io.IOException;

abstract public class ASTNode{
    public Position pos;

    public ASTNode(Position pos){
        this.pos = pos;
    }

    public abstract void accept(ASTVisitor visitor) ;

}

