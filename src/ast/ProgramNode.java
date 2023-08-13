package ast;

import ast.def.DefinitionNode;
import util.position.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTNode{
    public ArrayList<DefinitionNode> defNodes = new ArrayList<>();

    ProgramNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
