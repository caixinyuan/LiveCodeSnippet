package com.cxy.livecodesnippet.Util;

import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.compress.utils.Lists;

import javax.swing.table.TableModel;
import java.io.File;
import java.sql.*;
import java.util.List;

public class SQLiteUtil {

    private Connection connection;

    private static final Logger log = Logger.getInstance(SQLiteUtil.class);

    private SQLiteUtil() {
        // 初始化数据库连接
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Util.defaultLocalDBFilePath.getPath());
            createTable();
        } catch (ClassNotFoundException | SQLException e) {
            log.error("open db error", e);
        }
    }

    private static class SingletonInstance {
        private static final SQLiteUtil INSTANCE = new SQLiteUtil();
    }

    public static SQLiteUtil getInstance() {
        return SQLiteUtil.SingletonInstance.INSTANCE;
    }

    public Boolean dbFileIsExist() {
        File dbFile = new File(Util.defaultLocalDBFilePath);
        return dbFile.exists() && dbFile.isFile();
    }

    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE  if not exists  codesnippet (" +
                    "id INTEGER NOT NULL UNIQUE," +
                    "title TEXT," +
                    "describe TEXT," +
                    "tag TEXT," +
                    "people TEXT," +
                    "version NUMERIC," +
                    "codetype TEXT," +
                    "codesnippet TEXT," +
                    "PRIMARY KEY(id AUTOINCREMENT)" +
                    ");";
            statement.executeUpdate(query);
            statement.close();
            if (getSnippetCount() == 0) {
                insertDefaultCodeSnippet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getSnippetCount() {
        try {
            String query = "SELECT count(id) as count FROM codesnippet";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getInt("count");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void insertDefaultCodeSnippet() {
        CodeSnippetModel codeSnippetModel = new CodeSnippetModel();
        codeSnippetModel.setTitle("Test");
        codeSnippetModel.setDescribe("Test");
        codeSnippetModel.setTag("Test1,Test2");
        codeSnippetModel.setPeople("Test");
        codeSnippetModel.setVersion("1.0");
        codeSnippetModel.setCodeType("java");
        codeSnippetModel.setCodeSnippet("This is a Test CodeSnippet");
        insert(codeSnippetModel);
    }


    public void insert(List<CodeSnippetModel> codeSnippetList) {
        try {
            String query = "INSERT INTO codesnippet(title,describe,tag,people,version,codetype,codesnippet) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (CodeSnippetModel data : codeSnippetList) {
                preparedStatement.setString(1, data.getTitle());
                preparedStatement.setString(2, data.getDescribe());
                preparedStatement.setString(3, String.join(",", data.getTag()));
                preparedStatement.setString(4, data.getPeople());
                preparedStatement.setFloat(5, data.getVersion());
                preparedStatement.setString(6, data.getCodeType());
                preparedStatement.setString(7, data.getCodeSnippet());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(CodeSnippetModel codeSnippetModel) {
        try {
            String query = "INSERT INTO codesnippet(title,describe,tag,people,version,codetype,codesnippet) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, codeSnippetModel.getTitle());
            preparedStatement.setString(2, codeSnippetModel.getDescribe());
            preparedStatement.setString(3, String.join(",", codeSnippetModel.getTag()));
            preparedStatement.setString(4, codeSnippetModel.getPeople());
            preparedStatement.setFloat(5, codeSnippetModel.getVersion());
            preparedStatement.setString(6, codeSnippetModel.getCodeType());
            preparedStatement.setString(7, codeSnippetModel.getCodeSnippet());
            preparedStatement.addBatch();
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateById(CodeSnippetModel codeSnippetModel) {
        try {
            String query = "UPDATE codesnippet SET title=?,describe=?,tag=?,people=?,version=?,codetype=?,codesnippet=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, codeSnippetModel.getTitle());
            preparedStatement.setString(2, codeSnippetModel.getDescribe());
            preparedStatement.setString(3, String.join(",", codeSnippetModel.getTag()));
            preparedStatement.setString(4, codeSnippetModel.getPeople());
            preparedStatement.setFloat(5, codeSnippetModel.getVersion());
            preparedStatement.setString(6, codeSnippetModel.getCodeType());
            preparedStatement.setString(7, codeSnippetModel.getCodeSnippet());
            preparedStatement.setInt(8, codeSnippetModel.getId());
            preparedStatement.addBatch();
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public String getSnippetById(Integer id) {
        try {
            String query = "SELECT codesnippet FROM codesnippet where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("codesnippet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public CodeSnippetModel getSnippetModelById(Integer id) {
        try {
            String query = "SELECT * FROM codesnippet where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            CodeSnippetModel temp = new CodeSnippetModel();
            temp.setId(resultSet.getInt("id"));
            temp.setTitle(resultSet.getString("title"));
            temp.setDescribe(resultSet.getString("describe"));
            temp.setTag(resultSet.getString("tag"));
            temp.setPeople(resultSet.getString("people"));
            temp.setVersion(resultSet.getString("version"));
            temp.setCodeType(resultSet.getString("codetype"));
            temp.setCodeSnippet(resultSet.getString("codesnippet"));

            resultSet.close();
            preparedStatement.close();
            return temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<CodeSnippetModel> getTitleList(String title) {
        try {
            String query = "SELECT * FROM codesnippet where title like ? or describe like ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + title + "%");
            preparedStatement.setString(2, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<CodeSnippetModel> codeSnippetModelList = Lists.newArrayList();
            CodeSnippetModel temp;
            while (resultSet.next()) {
                temp = new CodeSnippetModel();
                temp.setId(resultSet.getInt("id"));
                temp.setTitle(resultSet.getString("title"));
                temp.setDescribe(resultSet.getString("describe"));
                temp.setCodeType(resultSet.getString("codetype"));
                codeSnippetModelList.add(temp);
            }
            resultSet.close();
            preparedStatement.close();
            return codeSnippetModelList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<CodeSnippetModel> getTitleList(int a, int b) {
        try {
            String query = "SELECT * FROM codesnippet limit " + a + " ," + b;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<CodeSnippetModel> codeSnippetModelList = Lists.newArrayList();
            CodeSnippetModel temp;
            while (resultSet.next()) {
                temp = new CodeSnippetModel();
                temp.setId(resultSet.getInt("id"));
                temp.setTitle(resultSet.getString("title"));
                temp.setDescribe(resultSet.getString("describe"));
                temp.setCodeType(resultSet.getString("codetype"));
                codeSnippetModelList.add(temp);
            }
            resultSet.close();
            preparedStatement.close();
            return codeSnippetModelList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<CodeSnippetModel> getTitleList() {
        try {
            String query = "SELECT * FROM codesnippet";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<CodeSnippetModel> codeSnippetModelList = Lists.newArrayList();
            CodeSnippetModel temp;
            while (resultSet.next()) {
                temp = new CodeSnippetModel();
                temp.setId(resultSet.getInt("id"));
                temp.setTitle(resultSet.getString("title"));
                temp.setDescribe(resultSet.getString("describe"));
                temp.setCodeType(resultSet.getString("codetype"));
                codeSnippetModelList.add(temp);
            }
            resultSet.close();
            preparedStatement.close();
            return codeSnippetModelList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteCodeSnippetById(int id) {
        try {
            String query = "DELETE FROM codesnippet WHERE ID=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            String query = "DELETE FROM codesnippet;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
