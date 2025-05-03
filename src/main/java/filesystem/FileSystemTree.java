package filesystem;

public class FileSystemTree {
    private Node root;

    public FileSystemTree() {
        resetFileSystem();
    }

    public void resetFileSystem() {
        this.root = new Node("Root", true);
    }

    public Node getRoot() {
        return root;
    }

    public Node findNode(Node currentNode, String name) {
        if (currentNode.getName().equals(name)) {
            return currentNode;
        }
        if (currentNode.isFolder()) {
            Linked_List<Node> children = currentNode.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                Node result = findNode(child, name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void addNode(String parentName, String nodeName, boolean isFolder) {
        Node parentNode = findNode(root, parentName);
        if (parentNode != null && parentNode.isFolder()) {
            Node newNode = new Node(nodeName, isFolder);
            parentNode.addChild(newNode);
        }
    }

    public void addContentToFile(String fileName, String content) {
        Node fileNode = findNode(root, fileName);
        if (fileNode != null && !fileNode.isFolder()) {
            fileNode.addContent(content);
        }
    }

    public boolean removeContentFromFile(String fileName, String content) {
        Node fileNode = findNode(root, fileName);
        if (fileNode != null && !fileNode.isFolder()) {
            return fileNode.removeContent(content);
        }
        return false;
    }

    public Linked_List<Node> getFilesAndFolders() {
        return root.getChildren();
    }

    public void removeNode(String name) {
        Node nodeToRemove = findNode(root, name);
        if (nodeToRemove != null) {
            Node parentNode = findParent(root, nodeToRemove);
            if (parentNode != null) {
                parentNode.removeChild(nodeToRemove);
            }
        }
    }

    private void clearAllChildren(Node currentNode) {
        Linked_List<Node> children = currentNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (!child.isFolder()) {
                child.clearContent();
            } else {
                clearAllChildren(child);
            }
        }
    }

    private Node findParent(Node currentNode, Node nodeToFind) {
        Linked_List<Node> children = currentNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).equals(nodeToFind)) {
                return currentNode;
            }
        }

        if (currentNode.isFolder()) {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                Node result = findParent(child, nodeToFind);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
