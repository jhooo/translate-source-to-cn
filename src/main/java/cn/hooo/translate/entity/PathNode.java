package cn.hooo.translate.entity;

import java.util.HashSet;

public class PathNode implements Comparable<PathNode> {
    private String name;
    private HashSet<PathNode> childs;
    private Boolean dir;

    public PathNode() {
    }

    public PathNode(String name, HashSet<PathNode> childs, Boolean dir) {
        this.name = name;
        this.childs = childs;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<PathNode> getChilds() {
        return childs;
    }

    public void setChilds(HashSet<PathNode> childs) {
        this.childs = childs;
    }

    public Boolean isDir() {
        return dir;
    }

    public void setDir(Boolean dir) {
        this.dir = dir;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.name == null) {
            return false;
        }
        return this.name.equals(((PathNode) obj).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(PathNode o) {
        return this.name.compareToIgnoreCase(o.getName());
    }

}
