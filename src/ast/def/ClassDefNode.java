package ast.def;

import ast.*;
import util.position.Position;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassDefNode extends DefinitionNode {

    public String className;

    public HashMap<String,SingleVarDefNode> members;

    public HashMap<String,FuncDefNode> functions;

    public ClassConstructorNode constructor;


    public ClassDefNode(Position pos, String name){
        super(pos);
        this.className = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
