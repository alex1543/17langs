import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class srvDB {

    public static void main(String[] args) throws Exception {
		System.out.println("Service running at http://localhost:8091/ ...");
		
        HttpServer server = HttpServer.create(new InetSocketAddress(8091), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
			
			System.out.println("Generation HTML...");

			String sOut="<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"></head><body>";
			sOut+="<style>html{font-family: sans-serif;} table {width: 100%;border-collapse: collapse;} td {border: 1px solid black;padding: 3px;} tr:hover {background: #d0e3f7;} p {font-size: large;font-weight: bold;}</style>";
			sOut+="<p>Применённые технологии: Java + MySQL.</p>";

			// Получение таблицы из MySQL ...
			try {
				Connection conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
				Statement stmt=conn.createStatement();
				ResultSet rs=stmt.executeQuery("SELECT * FROM myarttable WHERE id>14 ORDER BY id DESC");
				sOut+="<table>";
				while (rs.next()){
					int colCount=rs.getMetaData().getColumnCount();
					sOut+="<tr>";
					for (int i=1; i <= colCount;i++)
						sOut+="<td>"+rs.getString(i)+"</td>";
					sOut+="</tr>";
				}
				sOut+="</table>";
				System.out.println("DB is ok.");
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			sOut+="</body></html>";
			System.out.println("HTML is complete.");
				
			// отображение страницы в браузере.
			t.sendResponseHeaders(200, 0);
			OutputStream os = t.getResponseBody();
			os.write(sOut.getBytes());
			os.close();	
        }
    }
}