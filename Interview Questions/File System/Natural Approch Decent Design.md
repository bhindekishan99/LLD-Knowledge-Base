# LLD: File System

## 1. Requirements

Design a basic file system supporting:

- Hierarchical structure of files and directories.
- File metadata such as name, size, creation time, and permissions.
- Directories containing files and other directories.
- Create, read/update, and delete operations.
- Path-based access such as `/home/user/documents`.
- Search files recursively by name.

---

# 2. Start With File

A file needs to maintain:

```text
File
----
name
content
size
creationDate
permission
```

We don't need to store `path` inside `File`.

The tree structure itself represents where the file is located.

Example:

```text
root
└── home
    └── user
        └── resume.txt
```

---

# 3. We Need Directory

A file system is hierarchical.

A directory can contain:

- Files
- Other directories

For our simple design:

```text
Directory
---------
name
creationDate
permission

List<File> files
List<Directory> directories
```

This naturally creates a tree:

```text
root
├── home
│   └── user
│       ├── resume.txt
│       └── documents
│           └── notes.txt
│
└── temp
```

---

# 4. Directory Owns Its Contents

Since a directory contains files and directories, it should manage them.

```text
Directory

Can:
- addFile(file)
- removeFile(fileName)
- updateFile(fileName, content)
- findFile(fileName)

- addDirectory(directory)
- removeDirectory(directoryName)
```

`FileManager` should not directly manipulate the internal lists.

Prefer:

```java
directory.addFile(file);
```

instead of:

```java
directory.getFiles().add(file);
```

---

# 5. FileManager

The user interacts with the file system using paths.

Example:

```java
createFile(
    "/home/user/documents",
    "resume.txt",
    "content"
);
```

We therefore need a coordinator:

```text
FileManager
-----------
rootDirectory

Can:
- createFile(path, name, content)
- updateFile(path, name, content)
- deleteFile(path, name)

- createDirectory(path, name)
- deleteDirectory(path, name)

- findDirectory(path)
- findFile(name)
```

`FileManager` starts from the root directory and coordinates operations.

---

# 6. Path Navigation

For:

```text
/home/user/documents
```

start from root and navigate one directory at a time.

```text
root
 ↓
home
 ↓
user
 ↓
documents
```

We can keep this common logic in:

```java
Directory findDirectory(String path)
```

Then operations become simple.

```text
createFile(path, ...)
        ↓
findDirectory(path)
        ↓
directory.addFile(...)
```

Similarly:

```text
deleteFile(path, ...)
        ↓
findDirectory(path)
        ↓
directory.removeFile(...)
```

---

# 7. Searching a File

When the user provides only:

```java
findFile("resume.txt");
```

we don't know the directory.

Therefore we recursively search the directory tree.

```text
Current Directory
       ↓
Search its files
       ↓
Search each child directory
       ↓
Repeat recursively
```

The responsibility belongs to `Directory` because a directory knows its own children.

```java
List<File> findFile(String fileName)
```

We return a list because multiple directories can contain files with the same name.

`FileManager` only starts the search:

```java
return rootDirectory.findFile(fileName);
```

---

# 8. File Content Update

`File` should own changes to its content.

```text
File

updateContent(content)
```

When content changes, the file can also update its `size`.

This prevents `content` and `size` from becoming inconsistent.

---

# Final Design

## File

```text
Knows:
- name
- content
- size
- creationDate
- permission

Can:
- updateContent(content)
```

## Directory

```text
Knows:
- name
- creationDate
- permission
- List<File> files
- List<Directory> directories

Can:
- addFile(file)
- removeFile(fileName)
- updateFile(fileName, content)
- findFile(fileName)

- addDirectory(directory)
- removeDirectory(directoryName)
```

## FileManager

```text
Knows:
- rootDirectory

Can:
- createFile(path, name, content)
- updateFile(path, name, content)
- deleteFile(path, name)

- createDirectory(path, name)
- deleteDirectory(path, name)

- findDirectory(path)
- findFile(name)
```

---

# Complete Flow

### Create File

```text
User
 ↓
FileManager.createFile(path, name, content)
 ↓
findDirectory(path)
 ↓
Directory.addFile(file)
```

### Update File

```text
User
 ↓
FileManager.updateFile(path, name, content)
 ↓
findDirectory(path)
 ↓
Directory.updateFile(...)
 ↓
File.updateContent(...)
```

### Delete File

```text
User
 ↓
FileManager.deleteFile(path, name)
 ↓
findDirectory(path)
 ↓
Directory.removeFile(...)
```

### Search File

```text
User
 ↓
FileManager.findFile(name)
 ↓
rootDirectory.findFile(name)
 ↓
Recursive directory traversal
 ↓
List<File>
```

---

# Key Design Decisions

1. **File system is represented as a tree**, not as a global `List<File>`.
2. **Directory owns its files and child directories.**
3. **FileManager coordinates path-based operations.**
4. **Directory handles recursive searching of its subtree.**
5. **File owns its content and size.**
6. **Path is derived from the hierarchy rather than stored inside every file.**

This design is sufficient for the basic File System LLD requirements without unnecessary complexity.
