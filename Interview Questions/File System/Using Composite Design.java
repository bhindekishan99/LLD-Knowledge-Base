import java.time.LocalDateTime;
import java.util.*;

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

    // File      -> own size
    // Directory -> recursive size of children
    public abstract int getSize();

    // File      -> display itself
    // Directory -> recursively display subtree
    public abstract void display(String indent);

    // File      -> copy itself into destination 
1    // Directory -> recursively copy entire subtree
    public abstract void copy(Directory destination);

    /*
     * Other operations where Composite can be useful:
     *
     * changePermission(...)
     *      File      -> change own permission
     *      Directory -> recursively change children's permission
     *
     * search(...)
     *      File      -> check itself
     *      Directory -> recursively search children
     *
     * delete(...)
     *      File      -> delete itself
     *      Directory -> recursively delete subtree
     */
}


// ============================================================
// 3. FILE - LEAF
// ============================================================

class File extends FileSystemItem {

    private String content;
    private int size;

    public File(
            String name,
            String content,
            Permission permission) {

        super(name, permission);

        this.content = content;
        this.size = content.length();
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        this.size = content.length();
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void copy(Directory destination) {

        File copiedFile = new File(
                this.name,
                this.content,
                this.permission
        );

        destination.add(copiedFile);
    }

    @Override
    public void display(String indent) {
        System.out.println(
                indent + "- " + name + " (" + size + " bytes)"
        );
    }
}


// ============================================================
// 4. DIRECTORY - COMPOSITE
// ============================================================

class Directory extends FileSystemItem {

    /*
     * name -> FileSystemItem
     *
     * Example:
     *
     * "resume.txt" -> File
     * "notes.txt"  -> File
     * "photos"     -> Directory
     */
    private final Map<String, FileSystemItem> children =
            new HashMap<>();


    public Directory(
            String name,
            Permission permission) {

        super(name, permission);
    }


    // --------------------------------------------------------
    // CHILD MANAGEMENT
    // --------------------------------------------------------

    public void add(FileSystemItem item) {

        if (children.containsKey(item.getName())) {
            throw new IllegalArgumentException(
                    "Item already exists: " + item.getName()
            );
        }

        children.put(item.getName(), item);
    }


    public FileSystemItem remove(String name) {

        FileSystemItem removed =
                children.remove(name);

        if (removed == null) {
            throw new IllegalArgumentException(
                    "Item not found: " + name
            );
        }

        return removed;
    }


    public FileSystemItem getChild(String name) {
        return children.get(name);
    }


    // --------------------------------------------------------
    // COMPOSITE OPERATION - SIZE
    // --------------------------------------------------------

    @Override
    public int getSize() {

        int totalSize = 0;

        /*
         * We don't care whether child is File or Directory.
         *
         * File.getSize()
         *      -> returns own size
         *
         * Directory.getSize()
         *      -> recursively calculates subtree size
         */
        for (FileSystemItem child : children.values()) {
            totalSize += child.getSize();
        }

        return totalSize;
    }


    // --------------------------------------------------------
    // COMPOSITE OPERATION - COPY
    // --------------------------------------------------------

    @Override
    public void copy(Directory destination) {

        // Create a copy of this directory.
        Directory copiedDirectory =
                new Directory(
                        this.name,
                        this.permission
                );

        // Add copied directory to destination.
        destination.add(copiedDirectory);

        /*
         * Tell every child:
         *
         * "Copy yourself inside copiedDirectory."
         *
         * We don't care whether child is File or Directory.
         */
        for (FileSystemItem child : children.values()) {
            child.copy(copiedDirectory);
        }
    }


    // --------------------------------------------------------
    // RECURSIVE FILE SEARCH
    // --------------------------------------------------------

    public List<File> findFile(String fileName) {

        List<File> result =
                new ArrayList<>();

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
    // COMPOSITE OPERATION - DISPLAY
    // --------------------------------------------------------

    @Override
    public void display(String indent) {

        System.out.println(
                indent + "+ " + name + "/"
        );

        for (FileSystemItem child : children.values()) {
            child.display(indent + "  ");
        }
    }
}


// ============================================================
// 5. FILE MANAGER
// ============================================================

class FileManager {

    private final Directory root;


    public FileManager() {

        this.root =
                new Directory(
                        "root",
                        Permission.READ_WRITE
                );
    }


    // ========================================================
    // GENERAL PATH RESOLUTION
    // Can return either File or Directory
    // ========================================================

    private FileSystemItem getItem(String path) {

        if (path == null ||
                path.isEmpty() ||
                path.equals("/")) {

            return root;
        }

        String[] parts =
                path.split("/");

        FileSystemItem current =
                root;


        for (String part : parts) {

            if (part.isEmpty()) {
                continue;
            }

            if (!(current instanceof Directory directory)) {

                throw new IllegalArgumentException(
                        "Invalid path: " + path
                );
            }

            current =
                    directory.getChild(part);

            if (current == null) {

                throw new IllegalArgumentException(
                        "Path not found: " + path
                );
            }
        }

        return current;
    }


    // ========================================================
    // DIRECTORY PATH RESOLUTION
    // ========================================================

    private Directory findDirectory(String path) {

        FileSystemItem item =
                getItem(path);

        if (!(item instanceof Directory)) {

            throw new IllegalArgumentException(
                    "Not a directory: " + path
            );
        }

        return (Directory) item;
    }


    // ========================================================
    // DIRECTORY OPERATIONS
    // ========================================================

    public void createDirectory(
            String path,
            String directoryName) {

        Directory parent =
                findDirectory(path);

        parent.add(
                new Directory(
                        directoryName,
                        Permission.READ_WRITE
                )
        );
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

        directory.add(
                new File(
                        fileName,
                        content,
                        Permission.READ_WRITE
                )
        );
    }


    public String readFile(
            String path,
            String fileName) {

        Directory directory =
                findDirectory(path);

        FileSystemItem item =
                directory.getChild(fileName);

        if (!(item instanceof File file)) {

            throw new IllegalArgumentException(
                    "File not found: " + fileName
            );
        }

        return file.getContent();
    }


    public void updateFile(
            String path,
            String fileName,
            String newContent) {

        Directory directory =
                findDirectory(path);

        FileSystemItem item =
                directory.getChild(fileName);

        if (!(item instanceof File file)) {

            throw new IllegalArgumentException(
                    "File not found: " + fileName
            );
        }

        file.updateContent(newContent);
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
    // GET SIZE
    // Works for BOTH File and Directory
    // ========================================================

    public int getSize(String path) {

        FileSystemItem item =
                getItem(path);

        /*
         * Composite:
         *
         * File      -> own size
         * Directory -> recursive subtree size
         */
        return item.getSize();
    }


    // ========================================================
    // COPY
    // ========================================================

    /*
     * Example:
     *
     * copy(
     *   "/home/kishan/documents",
     *   "/home/kishan/backup"
     * )
     *
     * documents/
     *      resume.txt
     *      notes.txt
     *
     * becomes:
     *
     * backup/
     *      documents/
     *          resume.txt
     *          notes.txt
     */
    public void copy(
            String sourcePath,
            String destinationPath) {

        FileSystemItem source =
                getItem(sourcePath);

        Directory destination =
                findDirectory(destinationPath);

        source.copy(destination);
    }


    // ========================================================
    // MOVE
    // ========================================================

    /*
     * Move does NOT create a copy.
     *
     * It removes the SAME object from its old parent
     * and adds it to the destination directory.
     */
    public void move(
            String sourceParentPath,
            String itemName,
            String destinationPath) {

        Directory sourceParent =
                findDirectory(sourceParentPath);

        Directory destination =
                findDirectory(destinationPath);

        FileSystemItem item =
                sourceParent.getChild(itemName);

        if (item == null) {

            throw new IllegalArgumentException(
                    "Item not found: " + itemName
            );
        }

        // Remove same object from old parent
        sourceParent.remove(itemName);

        // Add same object to new parent
        destination.add(item);
    }


    // ========================================================
    // DISPLAY
    // ========================================================

    public void display() {
        root.display("");
    }
}


// ============================================================
// 6. DRIVER
// ============================================================

public class Main {

    public static void main(String[] args) {

        FileManager fileManager =
                new FileManager();


        // ----------------------------------------------------
        // CREATE DIRECTORY STRUCTURE
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

        fileManager.createDirectory(
                "/home/kishan",
                "backup"
        );


        // ----------------------------------------------------
        // CREATE FILES
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
                "photo-info.txt",
                "My Photos"
        );


        // ----------------------------------------------------
        // INITIAL STRUCTURE
        // ----------------------------------------------------

        System.out.println(
                "===== INITIAL FILE SYSTEM ====="
        );

        fileManager.display();


        // ----------------------------------------------------
        // GET SIZE
        // ----------------------------------------------------

        System.out.println(
                "\nDocuments size = " +
                        fileManager.getSize(
                                "/home/kishan/documents"
                        )
        );


        // ----------------------------------------------------
        // COPY COMPLETE DIRECTORY
        // ----------------------------------------------------

        fileManager.copy(
                "/home/kishan/documents",
                "/home/kishan/backup"
        );


        System.out.println(
                "\n===== AFTER COPYING DOCUMENTS ====="
        );

        fileManager.display();


        // ----------------------------------------------------
        // MOVE FILE
        // ----------------------------------------------------

        fileManager.move(
                "/home/kishan/photos",
                "photo-info.txt",
                "/home/kishan/documents"
        );


        System.out.println(
                "\n===== AFTER MOVING FILE ====="
        );

        fileManager.display();


        // ----------------------------------------------------
        // UPDATE + READ
        // ----------------------------------------------------

        fileManager.updateFile(
                "/home/kishan/documents",
                "notes.txt",
                "Updated LLD Notes"
        );

        System.out.println(
                "\nNotes Content = " +
                        fileManager.readFile(
                                "/home/kishan/documents",
                                "notes.txt"
                        )
        );


        // ----------------------------------------------------
        // SEARCH
        // ----------------------------------------------------

        List<File> foundFiles =
                fileManager.findFile(
                        "resume.txt"
                );

        System.out.println(
                "\nresume.txt occurrences = "
                        + foundFiles.size()
        );
    }
}
