package ast;

import ast.def.DefinitionNode;
import ast.util.position.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTNode{
    public ArrayList<DefinitionNode> defNodes = new ArrayList<>();

    public ProgramNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
