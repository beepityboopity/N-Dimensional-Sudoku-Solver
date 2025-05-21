// For making a tree of possible values for each index in a Sudoku game

import java.util.ArrayList;

public class BFSNode{
    int value;
    int[] index;
    BFSNode parent;
    ArrayList<BFSNode> children;
    int childInd;

    BFSNode(){
        this.index = null;
        this.parent = null;
        this.children = new ArrayList<BFSNode>();
        this.childInd = 0;
    }
    BFSNode(int v, int[] i){
        this.value = v;
        this.index = i;
        this.parent = null;
        this.children = new ArrayList<BFSNode>();
        this.childInd = 0;
    }

    public BFSNode(int v, int[] i, BFSNode p){
        this.value = v;
        this.index = i;
        this.parent = p;
        this.children = new ArrayList<BFSNode>();
        this.childInd = 0;
    }

    public BFSNode addNewChild(int v, int[] i){
        BFSNode newChild = new BFSNode(v, i);
        newChild.parent = this;
        this.children.add(newChild);
        return newChild;
    }
    public boolean hasParent(){
        return !(this.parent == null);
    }
}
