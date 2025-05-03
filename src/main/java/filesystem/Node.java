package filesystem;

public class Node {
    private String name;
    private boolean isFolder;
    private Linked_List<Node> children;
    private Linked_List<String> content;

    public Node(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
        this.children = new Linked_List<>();
        this.content = new Linked_List<>();
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public Linked_List<Node> getChildren() {
        return children;
    }

    public Linked_List<String> getContent() {
        return content;
    }

    public void addChild(Node node) {
        this.children.add(node);
    }

    public void removeChild(Node node) {
        this.children.remove(node);
    }

    public void addContent(String contentData) {
        if (!isFolder && !content.contains(contentData)) {
            this.content.add(contentData);
        }
    }

    public boolean removeContent(String contentData) {
        return this.content.remove(contentData);
    }

    public String getContentAt(int index) {
        return this.content.get(index);
    }

    public boolean containsContent(String contentData) {
        return this.content.contains(contentData);
    }

    public void clearContent() {
        this.content.clear();
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void setContent(Linked_List<String> newContent) {
        this.content = newContent;
    }
}


