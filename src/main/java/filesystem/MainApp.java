package filesystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.controlsfx.glyphfont.Glyph;


public class MainApp extends Application {

    private FileSystemTree fileSystemTree = new FileSystemTree();
    private Node currentCategoryNode = fileSystemTree.getRoot();
    private Node currentFileNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showMainCategoryPage(primaryStage);
    }

    private void showMainCategoryPage(Stage stage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f9f9f9;");

        Label title = new Label("Categories");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox categoryList = new VBox(10);
        categoryList.setPadding(new Insets(10));
        categoryList.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");

        ScrollPane categoryScrollPane = new ScrollPane(categoryList);
        categoryScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        categoryScrollPane.setFitToWidth(true);
        categoryScrollPane.setStyle("-fx-background-color: transparent;");
        categoryScrollPane.setPrefHeight(500);

        Linked_List<Node> categories = fileSystemTree.getFilesAndFolders();
        for (int i = 0; i < categories.size(); i++) {
            Node category = categories.get(i);
            if (category.isFolder()) {
                HBox categoryCard = createCategoryCard(category, stage, categoryList);
                categoryList.getChildren().add(categoryCard);
            }
        }

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"
        );
        addCategoryButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Category");
            dialog.setHeaderText("Enter category name:");
            dialog.showAndWait().ifPresent(categoryName -> {
                if (!categoryName.trim().isEmpty()) {
                    addCategory(categoryName, categoryList, stage);
                }
            });
        });

        layout.getChildren().addAll(title, categoryScrollPane, addCategoryButton);

        Scene scene = new Scene(layout, 400, 700);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/System-documents-icon.png")));
        stage.setTitle("File System Simulation");
        stage.show();
    }


    private void addCategory(String categoryName, VBox categoryList, Stage stage) {

        if (fileSystemTree.findNode(fileSystemTree.getRoot(), categoryName) != null) {
            showAlert("Category already exists!", "This category name is already taken.");
            return;
        }

        fileSystemTree.addNode(fileSystemTree.getRoot().getName(), categoryName, true);
        Node categoryNode = fileSystemTree.findNode(fileSystemTree.getRoot(), categoryName);
        HBox categoryCard = createCategoryCard(categoryNode, stage, categoryList);
        categoryList.getChildren().add(categoryCard);
    }


    private HBox createCategoryCard(Node categoryNode, Stage stage, VBox categoryList) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6); -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 2, 2);");

        Label nameLabel = new Label(categoryNode.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        card.setPrefWidth(300);
        nameLabel.setMaxWidth(card.getPrefWidth() / 2);
        nameLabel.setStyle(nameLabel.getStyle() + " -fx-text-overflow: ellipsis; -fx-ellipsize: middle;");

        Button deleteButton = new Button();
        Glyph deleteIcon = new Glyph("FontAwesome", "TRASH");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-border-color: #e60000; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnAction(event -> removeCategoryNode(categoryNode, categoryList));

        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        HBox.setHgrow(deleteButton, Priority.NEVER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                currentCategoryNode = categoryNode;
                showCategoryDetailsPage(stage, categoryNode);
                card.getChildren().clear();
                card.getChildren().add(nameLabel);
            }
        });

        card.getChildren().addAll(nameLabel, spacer, deleteButton);
        return card;
    }


    private void removeCategoryNode(Node categoryNode, VBox categoryList) {

        fileSystemTree.removeNode(categoryNode.getName());


        categoryList.getChildren().removeIf(card -> {
            HBox hBox = (HBox) card;
            Label label = (Label) hBox.getChildren().get(0);
            return label.getText().equals(categoryNode.getName());
        });
    }

    private void showCategoryDetailsPage(Stage stage, Node categoryNode) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f9f9f9;");

        Label title = new Label("Folders in " + categoryNode.getName());
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox fileList = new VBox(10);
        fileList.setPadding(new Insets(10));
        fileList.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");

        ScrollPane fileScrollPane = new ScrollPane(fileList);
        fileScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        fileScrollPane.setFitToWidth(true);
        fileScrollPane.setStyle("-fx-background-color: transparent;");
        fileScrollPane.setPrefHeight(500);

        for (int i = 0; i < categoryNode.getChildren().size(); i++) {
            Node file = categoryNode.getChildren().get(i);
            HBox fileCard = createFileCard(file, stage, fileList);
            fileList.getChildren().add(fileCard);
        }

        Button addFileButton = new Button("Add Folder");
        addFileButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"
        );
        addFileButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Folder");
            dialog.setHeaderText("Enter Folder name:");
            dialog.showAndWait().ifPresent(fileName -> {
                if (!fileName.trim().isEmpty()) {
                    if (fileSystemTree.findNode(categoryNode, fileName) != null) {
                        showAlert("File already exists!", "A file with the same name already exists.");
                    } else {
                        fileSystemTree.addNode(categoryNode.getName(), fileName, true);
                        Node fileNode = fileSystemTree.findNode(categoryNode, fileName);
                        HBox fileCard = createFileCard(fileNode, stage, fileList);
                        fileList.getChildren().add(fileCard);
                    }
                }
            });
        });

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> showMainCategoryPage(stage));

        HBox buttonBox = new HBox(10, addFileButton, backButton);
        buttonBox.setSpacing(20);
        buttonBox.setStyle("-fx-alignment: center;");

        layout.getChildren().addAll(title, fileScrollPane, buttonBox);

        Scene scene = new Scene(layout, 400, 700);
        stage.setScene(scene);
    }


    private HBox createFileCard(Node fileNode, Stage stage, VBox fileList) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6); -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 2, 2);");

        Label nameLabel = new Label(fileNode.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        card.setPrefWidth(300);
        nameLabel.setMaxWidth(card.getPrefWidth() / 2);
        nameLabel.setStyle(nameLabel.getStyle() + " -fx-text-overflow: ellipsis; -fx-ellipsize: middle;");

        Button deleteButton = new Button();
        Glyph deleteIcon = new Glyph("FontAwesome", "TRASH");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-border-color: #e60000; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnAction(event -> {
            removeFileNode(fileNode, fileList, card);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                currentFileNode = fileNode;
                showFileContentPage(stage);
                card.getChildren().clear();
                card.getChildren().add(nameLabel);
            }
        });

        card.getChildren().addAll(nameLabel, spacer, deleteButton);
        return card;
    }


    private void removeFileNode(Node fileNode, VBox fileList, HBox card) {

        fileSystemTree.removeNode(fileNode.getName());


        int cardIndex = fileList.getChildren().indexOf(card);
        if (cardIndex != -1) {
            fileList.getChildren().remove(cardIndex);
        }
    }


    private void saveCurrentFileContent() {
        Linked_List<String> content = currentFileNode.getContent();

    }


    private Map<String, LinkedList<String>> fileContentsMap = new HashMap<>();

    private void showFileContentPage(Stage stage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f9f9f9;");

        Label title = new Label("Content in " + currentFileNode.getName());
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox contentList = new VBox(10);
        contentList.setPadding(new Insets(10));
        contentList.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");

        ScrollPane contentScrollPane = new ScrollPane(contentList);
        contentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setStyle("-fx-background-color: transparent;");
        contentScrollPane.setPrefHeight(500);

        LinkedList<String> content = fileContentsMap.getOrDefault(currentFileNode.getName(), new LinkedList<>());
        for (String contentItem : content) {
            HBox contentCard = createContentCard(contentItem, stage, contentList);
            contentList.getChildren().add(contentCard);
        }

        Button addContentButton = new Button("Add Content");
        addContentButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"
        );
        addContentButton.setOnAction(e -> addContentToFile(stage, contentList));

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> {
            saveCurrentFileContent();
            showCategoryDetailsPage(stage, currentCategoryNode);
        });

        HBox buttonBox = new HBox(10, addContentButton, backButton);
        buttonBox.setSpacing(20);
        buttonBox.setStyle("-fx-alignment: center;");

        layout.getChildren().addAll(title, contentScrollPane, buttonBox);

        Scene scene = new Scene(layout, 400, 700);
        stage.setScene(scene);
    }


    private void addContentToFile(Stage stage, VBox contentList) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"), new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"), new FileChooser.ExtensionFilter("Videos", "*.mp4"), new FileChooser.ExtensionFilter("Text Files", "*.txt"), new FileChooser.ExtensionFilter("PDF", "*.pdf"), new FileChooser.ExtensionFilter("Word Documents", "*.docx"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String fileName = file.getName();
            String filePath = file.getAbsolutePath();

            filePaths.put(fileName, filePath);

            LinkedList<String> content = fileContentsMap.getOrDefault(currentFileNode.getName(), new LinkedList<>());
            if (!content.contains(fileName)) {
                content.add(fileName);
                fileContentsMap.put(currentFileNode.getName(), content);

                HBox contentCard = createContentCard(fileName, stage, contentList);
                contentList.getChildren().add(contentCard);
            } else {
                showAlert("Content already exists!", "This content is already added to the file.");
            }
        }
    }

    private HBox createContentCard(String contentName, Stage stage, VBox contentList) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6); -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 2, 2);");

        Label nameLabel = new Label(contentName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        card.setPrefWidth(300);
        nameLabel.setMaxWidth(card.getPrefWidth() / 2);
        nameLabel.setStyle(nameLabel.getStyle() + " -fx-text-overflow: ellipsis; -fx-ellipsize: middle;");

        Button deleteButton = new Button();
        Glyph deleteIcon = new Glyph("FontAwesome", "TRASH");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-border-color: #e60000; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-border-color: #ff4d4d; -fx-border-radius: 5; -fx-background-radius: 5;"));
        deleteButton.setOnAction(event -> removeContent(contentName, contentList));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openContent(contentName);
            }
        });

        card.getChildren().addAll(nameLabel, spacer, deleteButton);
        return card;
    }


    private void removeContent(String contentName, VBox contentList) {
        LinkedList<String> content = fileContentsMap.get(currentFileNode.getName());
        if (content != null) {
            content.remove(contentName);
            fileContentsMap.put(currentFileNode.getName(), content);
        }

        contentList.getChildren().removeIf(card -> {
            HBox hBox = (HBox) card;
            Label label = (Label) hBox.getChildren().get(0);
            return label.getText().equals(contentName);
        });
    }

    private Map<String, String> filePaths = new HashMap<>();

    private void openContent(String contentName) {
        try {
            String filePath = filePaths.get(contentName);

            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert("Error", "The file does not exist.");
                }
            } else {
                showAlert("Error", "File path not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open the file.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}