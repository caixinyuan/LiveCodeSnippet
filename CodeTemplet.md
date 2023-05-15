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

|           标题            |            描述             |        标签        | 添加人 | 版本 |
| :-----------------------: | :-------------------------: | :----------------: | :----: | :--: |
| InputStream转换为字符串。 | InputStream()转换为字符串。 | String,InputStream |  cxy   | 1.0  |

```   java
//region 1
dsdsdsdsd
//endregion

//region 2
dsdsdsdsd

//region 2.2
dsdsdsdsd
//endregion
//endregion



//region 3
dsdsdsdsd
//endregion



//region 4
dsdsdsdsd
//endregion


```

------

|        标题        |        描述        |              标签              | 添加人 | 版本 |
| :----------------: | :----------------: | :----------------------------: | :----: | :--: |
| 获取当前工作目录。 | 获取当前工作目录。 | getCurrentWorkingDirectoryPath |  cxy   | 1.0  |

```   java
public static String getCurrentWorkingDirectoryPath() {
    return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
}
=======
|      标题       |            描述            |      标签       | 添加人 | 版本 |
| :-------------: | :------------------------: | :-------------: | :----: | :--: |
| Hutool-DateUtil | Hutool时间日期工具使用方法 | Hutool,DateUtil |  cxy   | 1.0  |

```   java
/**
 * Date、long、Calendar之间的相互转换
 */
    
    //region My Custom Region
    // Your code goes here...
    //endregion

    //当前时间
    Date date = DateUtil.date();
    //当前时间
    Date date2 = DateUtil.date(Calendar.getInstance());
    //当前时间
    Date date3 = DateUtil.date(System.currentTimeMillis());
    //当前时间字符串，格式：yyyy-MM-dd HH:mm:ss
    String now = DateUtil.now();
    //当前日期字符串，格式：yyyy-MM-dd
    String today= DateUtil.today();
    
============================字符串转日期  
    
    String dateStr = "2017-03-01";
	Date date = DateUtil.parse(dateStr);
	//自定义格式
	String dateStr = "2017-03-01";
	Date date = DateUtil.parse(dateStr, "yyyy-MM-dd");

============================格式化日期输出
    
    String dateStr = "2017-03-01";
    Date date = DateUtil.parse(dateStr);

    //结果 2017/03/01
    String format = DateUtil.format(date, "yyyy/MM/dd");

    //常用格式的格式化，结果：2017-03-01
    String formatDate = DateUtil.formatDate(date);

    //结果：2017-03-01 00:00:00
    String formatDateTime = DateUtil.formatDateTime(date);

    //结果：00:00:00
    String formatTime = DateUtil.formatTime(date);

============================获取Date对象的某个部分
    
    Date date = DateUtil.date();
    //获得年的部分
    DateUtil.year(date);
    //获得月份，从0开始计数
    DateUtil.month(date);
    //获得月份枚举
    DateUtil.monthEnum(date);

============================开始和结束时间
    
    String dateStr = "2017-03-01 22:33:23";
    Date date = DateUtil.parse(dateStr);

    //一天的开始，结果：2017-03-01 00:00:00
    Date beginOfDay = DateUtil.beginOfDay(date);

    //一天的结束，结果：2017-03-01 23:59:59
    Date endOfDay = DateUtil.endOfDay(date); 

============================日期时间偏移
    
    String dateStr = "2017-03-01 22:33:23";
    Date date = DateUtil.parse(dateStr);

    //结果：2017-03-03 22:33:23
    Date newDate = DateUtil.offset(date, DateField.DAY_OF_MONTH, 2);

    //常用偏移，结果：2017-03-04 22:33:23
    DateTime newDate2 = DateUtil.offsetDay(date, 3);

    //常用偏移，结果：2017-03-01 19:33:23
    DateTime newDate3 = DateUtil.offsetHour(date, -3);

	针对当前时间，提供了简化的偏移方法（例如昨天、上周、上个月等）：
    //昨天
    DateUtil.yesterday()
    //明天
    DateUtil.tomorrow()
    //上周
    DateUtil.lastWeek()
    //下周
    DateUtil.nextWeek()
    //上个月
    DateUtil.lastMonth()
    //下个月
    DateUtil.nextMonth()
        
============================日期时间差
        
    String dateStr1 = "2017-03-01 22:33:23";
    Date date1 = DateUtil.parse(dateStr1);

    String dateStr2 = "2017-04-01 23:33:23";
    Date date2 = DateUtil.parse(dateStr2);

    //相差一个月，31天
    long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);

============================格式化时间差
    
    //Level.MINUTE表示精确到分
    String formatBetween = DateUtil.formatBetween(between, Level.MINUTE);
    //输出：31天1小时
    Console.log(formatBetween);

============================其它
    
    //年龄
    DateUtil.ageOfNow("1990-01-30");

    //是否闰年
    DateUtil.isLeapYear(2017);
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------
| 标题 |   描述   |    标签    | 添加人 | 版本 |
| :--: | :------: | :--------: | :----: | :--: |
| Test | Test内容 | T,FU,caixy |  cxy   | 1.0  |

```   java
resultSet.addElement(LookupElementBuilder.create("Hello"));
```

------