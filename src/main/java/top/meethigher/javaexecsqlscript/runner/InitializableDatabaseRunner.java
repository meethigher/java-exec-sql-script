package top.meethigher.javaexecsqlscript.runner;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author chenchuancheng
 * @since 2021/12/2 11:27
 */
@Component
public class InitializableDatabaseRunner implements CommandLineRunner {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public void run(String... args) throws Exception {
        try {
            Connection conn =getConnection();
            ScriptRunner runner = new ScriptRunner(conn);
            Resources.setCharset(Charset.forName("UTF-8")); //设置字符集,不然中文乱码插入错误
            runner.setLogWriter(null);//设置是否输出日志
            // 从class目录下直接读取
            Reader read = Resources.getResourceAsReader("reset.sql");
            runner.runScript(read);
            runner.closeConnection();
            conn.close();
            System.out.println("sql脚本执行完毕");
        } catch (Exception e) {
            System.out.println("sql脚本执行发生异常");
            e.printStackTrace();
        }
    }


//    @Override
//    public void run(String... args) {
//        try {
//            Connection connection = getConnection();
//            SQLScriptRunner runner = new SQLScriptRunner(connection);
//            ClassPathResource classPathResource = new ClassPathResource("resetsql/reset.sql");
//            InputStream inputStream = classPathResource.getInputStream();
//            Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//            //打印日志
////            runner.setLogWriter(null);
//            runner.runScript(reader);
//            runner.closeConnection();
//            connection.close();
//            System.out.println("sql脚本执行完毕");
//        }catch (Exception e) {
//            System.out.println("sql脚本执行异常");
//            e.printStackTrace();
//        }
//    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(dataSourceProperties.getDriverClassName());
        return DriverManager.getConnection(dataSourceProperties.getUrl(),dataSourceProperties.getUsername(),dataSourceProperties.getPassword());
    }
}
