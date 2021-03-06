package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class FavoInsertServlet
 */
@WebServlet("/FavoInsertServlet")
public class FavoInsertServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String sql1 = "select * from FavoTB where UserName = ? and RyouriID = ?";
		String sql2 = "insert into FavoTB values (?, ?, ?)";
		HttpSession session = request.getSession();

		//認証チェック
		//if (!Util.checkAuth(request, response)) return; とするとログイン中ではない時ログインページに飛ぶ
		if (session == null || request.getRemoteUser() == null) {
			session.setAttribute("favoInsert", false);
			System.out.println("ログアウト状態でFavoされました");
			return;
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {

			prestmt.setString(1, userName);
			prestmt.setInt(2, recipeID);
			System.out.println("Favo重複チェックSQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					System.out.println("すでにFavoされています");
					session.setAttribute("favoInsert", true);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favo重複チェックSQL完了");

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {

			prestmt.setString(1, userName);
			prestmt.setInt(2, recipeID);
			prestmt.setString(3, f.format(new Date())); //P323参照
			System.out.println("Favo登録SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favo登録SQL完了");
		session.setAttribute("favoInsert", true);
	}

}
