| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------

|   标题   |        描述        |   标签    | 添加人 | 版本 |
| :------: | :----------------: | :-------: | :----: | :--: |
| List排序 | 对List中的内容排序 | List,Sort |  cxy   | 1.0  |

```   java
List<String> list = new ArrayList<>();
Collections.sort(list, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
});
```

------

|    标题     |       描述        |     标签      | 添加人 | 版本 |
| :---------: | :---------------: | :-----------: | :----: | :--: |
| Base64 编码 | Base64 编码字符串 | Base64,encode |  cxy   | 1.0  |

```   java
 public static String encodeBase64(String input) {
    return Base64.getEncoder().encodeToString(input.getBytes());
  }
```

------

|    标题     |       描述        |     标签      | 添加人 | 版本 |
| :---------: | :---------------: | :-----------: | :----: | :--: |
| Base64 解码 | Base64 解码字符串 | Base64,decode |  cxy   | 1.0  |

```   java
  public static String decodeBase64(String input) {
    return new String(Base64.getDecoder().decode(input.getBytes()));
  }
```

------

|   标题   |     描述     |  标签   | 添加人 | 版本 |
| :------: | :----------: | :-----: | :----: | :--: |
| 压缩文件 | 压缩单个文件 | zipFile |  cxy   | 1.0  |

```   java
  public static void zipFile(String srcFilename, String zipFilename) throws IOException {
    var srcFile = new File(srcFilename);
    try (
            var fileOut = new FileOutputStream(zipFilename);
            var zipOut = new ZipOutputStream(fileOut);
            var fileIn = new FileInputStream(srcFile);
    ) {
      var zipEntry = new ZipEntry(srcFile.getName());
      zipOut.putNextEntry(zipEntry);
      final var bytes = new byte[1024];
      int length;
      while ((length = fileIn.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
    }
  }
```

------

|   标题   |     描述     |            标签            | 添加人 | 版本 |
| :------: | :----------: | :------------------------: | :----: | :--: |
| 压缩文件 | 压缩多个文件 | zipFile,Zip multiple files |  cxy   | 1.0  |

```   java
  public static void zipFiles(String[] srcFilenames, String zipFilename) throws IOException {
    try (
      var fileOut = new FileOutputStream(zipFilename);
      var zipOut = new ZipOutputStream(fileOut);
    ) {
      for (var i=0; i<srcFilenames.length; i++) {
        var srcFile = new File(srcFilenames[i]);
        try (var fileIn = new FileInputStream(srcFile)) {
          var zipEntry = new ZipEntry(srcFile.getName());
          zipOut.putNextEntry(zipEntry);
          final var bytes = new byte[1024];
          int length;
          while ((length = fileIn.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
          }
        }
      }
    }
  }
```

------

|   标题   |   描述   |          标签           | 添加人 | 版本 |
| :------: | :------: | :---------------------: | :----: | :--: |
| 压缩文件 | 压缩目录 | zipFile,Zip a directory |  cxy   | 1.0  |

```   java
  public static void zipDirectory (String srcDirectoryName, String zipFileName) throws IOException {
    var srcDirectory = new File(srcDirectoryName);
    try (
      var fileOut = new FileOutputStream(zipFileName);
      var zipOut = new ZipOutputStream(fileOut)
    ) {
      zipFile(srcDirectory, srcDirectory.getName(), zipOut);
    }
  }
  public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) 
      throws IOException {
    if (fileToZip.isHidden()) { // Ignore hidden files as standard
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName)); // To be zipped next
        zipOut.closeEntry();
      } else {
        // Add the "/" mark explicitly to preserve structure while unzipping action is performed
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }
      var children = fileToZip.listFiles();
      for (var childFile : children) { // Recursively apply function to all children
        zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
      }
      return;
    }
    try (
        var fis = new FileInputStream(fileToZip) // Start zipping once we know it is a file
    ) {
      var zipEntry = new ZipEntry(fileName);
      zipOut.putNextEntry(zipEntry);
      var bytes = new byte[1024];
      var length = 0;
      while ((length = fis.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
    }
  }
```

------

|      标题      |      描述      |     标签     | 添加人 | 版本 |
| :------------: | :------------: | :----------: | :----: | :--: |
| 正则匹配字符串 | 正则匹配字符串 | String,match |  cxy   | 1.0  |

```   java
public static List<String> match(String input, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(input);
    List<String> matchedParts = new ArrayList<>();
    while (matcher.find()) {
        matchedParts.add(matcher.group(0));
    }
    return matchedParts;
}
```

------

|           标题            |            描述             |        标签        | 添加人 | 版本 |
| :-----------------------: | :-------------------------: | :----------------: | :----: | :--: |
| InputStream转换为字符串。 | InputStream()转换为字符串。 | String,InputStream |  cxy   | 1.0  |

```   java
public static String convertInputStreamToString(final InputStream in) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length;
    while ((length = in.read(buffer)) != -1) {
        result.write(buffer, 0, length);
    }
    return result.toString(StandardCharsets.UTF_8.name());
}
```

------

|        标题        |        描述        |              标签              | 添加人 | 版本 |
| :----------------: | :----------------: | :----------------------------: | :----: | :--: |
| 获取当前工作目录。 | 获取当前工作目录。 | getCurrentWorkingDirectoryPath |  cxy   | 1.0  |

```   java
public static String getCurrentWorkingDirectoryPath() {
    return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
}
```

------