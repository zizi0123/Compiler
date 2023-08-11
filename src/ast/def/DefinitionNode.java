package ast.def;

import ast.ASTNode;
import ast.ASTVisitor;
import util.position.Position;

public abstract class DefinitionNode extends ASTNode {
    DefinitionNode(Position pos){
        super(pos);
    }


}
