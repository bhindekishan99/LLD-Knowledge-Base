import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ============================================================
// 1. PERMISSION
// ============================================================

enum Permission {
    READ_ONLY,
    READ_WRITE
}


// ============================================================
// 2. COMPONENT - COMPOSITE PATTERN
// ============================================================

abstract class FileSystemItem {

    protected final String name;
    protected final LocalDateTime creationTime;
    protected Permission permission;

    public FileSystemItem(String name, Permission permission) {
        this.name = name;
        this.permission = permission;
        this.creationTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public Permission getPermission() {
        return permission;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public abstract void display(String indent);
}


// ============================================================
// 3. FILE - LEAF
// ============================================================

class File extends FileSystemItem {

    private String content;
    private int size;

    public File(String name, String content, Permission permission) {
        super(name, permission);
        this.content = content;
        this.size = content.length();
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public void updateContent(String content) {
        this.content = content;
        this.size = content.length();
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "- " + name);
    }
}


// ============================================================
// 4. DIRECTORY - COMPOSITE
// ============================================================

class Directory extends FileSystemItem {

    // Name -> File/Directory
    private final Map<String, FileSystemItem> children =
            new HashMap<>();

    public Directory(String name, Permission permission) {
        super(name, permission);
    }

    public void add(FileSystemItem item) {

        if (children.containsKey(item.getName())) {
            throw new IllegalArgumentException(
                    "Item already exists: " + item.getName()
            );
        }

        children.put(item.getName(), item);
    }

    public void remove(String name) {

        if (!children.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Item not found: " + name
            );
        }

        children.remove(name);
    }

    public FileSystemItem getChild(String name) {
        return children.get(name);
    }

    // --------------------------------------------------------
    // Recursive File Search
    // --------------------------------------------------------

    public List<File> findFile(String fileName) {

        List<File> result = new ArrayList<>();

        for (FileSystemItem item : children.values()) {

            if (item instanceof File file) {

                if (file.getName().equals(fileName)) {
                    result.add(file);
                }

            } else if (item instanceof Directory directory) {

                result.addAll(
                        directory.findFile(fileName)
                );
            }
        }

        return result;
    }

    // --------------------------------------------------------
    // Composite operation
    // --------------------------------------------------------

    @Override
    public void display(String indent) {

        System.out.println(indent + "+ " + name + "/");

        for (FileSystemItem item : children.values()) {
            item.display(indent + "  ");
        }
    }
}


// ============================================================
// 5. FILE MANAGER
// ============================================================

class FileManager {

    private final Directory root;

    public FileManager() {
        root = new Directory(
                "root",
                Permission.READ_WRITE
        );
    }

    // ========================================================
    // PATH RESOLUTION
    // ========================================================

    private Directory findDirectory(String path) {

        if (path == null ||
            path.isEmpty() ||
            path.equals("/")) {

            return root;
        }

        String[] parts = path.split("/");

        Directory current = root;

        for (String part : parts) {

            if (part.isEmpty()) {
                continue;
            }

            FileSystemItem item =
                    current.getChild(part);

            if (!(item instanceof Directory)) {
                throw new IllegalArgumentException(
                        "Directory not found: " + part
                );
            }

            current = (Directory) item;
        }

        return current;
    }


    // ========================================================
    // DIRECTORY OPERATIONS
    // ========================================================

    public void createDirectory(
            String path,
            String directoryName) {

        Directory parent =
                findDirectory(path);

        Directory directory =
                new Directory(
                        directoryName,
                        Permission.READ_WRITE
                );

        parent.add(directory);
    }


    public void deleteDirectory(
            String path,
            String directoryName) {

        Directory parent =
                findDirectory(path);

        FileSystemItem item =
                parent.getChild(directoryName);

        if (!(item instanceof Directory)) {
            throw new IllegalArgumentException(
                    "Directory not found: "
                    + directoryName
            );
        }

        parent.remove(directoryName);
    }


    // ========================================================
    // FILE OPERATIONS
    // ========================================================

    public void createFile(
            String path,
            String fileName,
            String content) {

        Directory directory =
                findDirectory(path);

        File file =
                new File(
                        fileName,
                        content,
                        Permission.READ_WRITE
                );

        directory.add(file);
    }


    public void updateFile(
            String path,
            String fileName,
            String content) {

        Directory directory =
                findDirectory(path);

        FileSystemItem item =
                directory.getChild(fileName);

        if (!(item instanceof File)) {
            throw new IllegalArgumentException(
                    "File not found: " + fileName
            );
        }

        File file = (File) item;

        file.updateContent(content);
    }


    public String readFile(
            String path,
            String fileName) {

        Directory directory =
                findDirectory(path);

        FileSystemItem item =
                directory.getChild(fileName);

        if (!(item instanceof File)) {
            throw new IllegalArgumentException(
                    "File not found: " + fileName
            );
        }

        return ((File) item).getContent();
    }


    public void deleteFile(
            String path,
            String fileName) {

        Directory directory =
                findDirectory(path);

        FileSystemItem item =
                directory.getChild(fileName);

        if (!(item instanceof File)) {
            throw new IllegalArgumentException(
                    "File not found: " + fileName
            );
        }

        directory.remove(fileName);
    }


    // ========================================================
    // SEARCH
    // ========================================================

    public List<File> findFile(String fileName) {
        return root.findFile(fileName);
    }


    // ========================================================
    // DISPLAY
    // ========================================================

    public void display() {
        root.display("");
    }
}


// ============================================================
// 6. MAIN
// ============================================================

public class Main {

    public static void main(String[] args) {

        FileManager fileManager =
                new FileManager();


        // ----------------------------------------------------
        // Create Directories
        // ----------------------------------------------------

        fileManager.createDirectory(
                "/",
                "home"
        );

        fileManager.createDirectory(
                "/home",
                "kishan"
        );

        fileManager.createDirectory(
                "/home/kishan",
                "documents"
        );

        fileManager.createDirectory(
                "/home/kishan",
                "photos"
        );


        // ----------------------------------------------------
        // Create Files
        // ----------------------------------------------------

        fileManager.createFile(
                "/home/kishan/documents",
                "resume.txt",
                "My Resume"
        );

        fileManager.createFile(
                "/home/kishan/documents",
                "notes.txt",
                "LLD Notes"
        );

        fileManager.createFile(
                "/home/kishan/photos",
                "info.txt",
                "Photo information"
        );


        // ----------------------------------------------------
        // Display
        // ----------------------------------------------------

        System.out.println("=== FILE SYSTEM ===");

        fileManager.display();


        // ----------------------------------------------------
        // Read File
        // ----------------------------------------------------

        String content =
                fileManager.readFile(
                        "/home/kishan/documents",
                        "notes.txt"
                );

        System.out.println(
                "\nContent: " + content
        );


        // ----------------------------------------------------
        // Update File
        // ----------------------------------------------------

        fileManager.updateFile(
                "/home/kishan/documents",
                "notes.txt",
                "Updated LLD Notes"
        );


        // ----------------------------------------------------
        // Search File
        // ----------------------------------------------------

        List<File> files =
                fileManager.findFile(
                        "notes.txt"
                );

        System.out.println(
                "\nFound files: " + files.size()
        );


        // ----------------------------------------------------
        // Delete File
        // ----------------------------------------------------

        fileManager.deleteFile(
                "/home/kishan/documents",
                "resume.txt"
        );


        // ----------------------------------------------------
        // Final Structure
        // ----------------------------------------------------

        System.out.println(
                "\n=== AFTER DELETION ==="
        );

        fileManager.display();
    }
}
