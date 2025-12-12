package dao;

import connectDB.ConnectDB;
import entities.NhaCungCap;
import java.util.ArrayList;
import java.sql.*;
/**
 *
 * @author LENOVO
 */
public class NhaCungCapDAO {    
       public NhaCungCapDAO(){};
       public ArrayList<NhaCungCap> getAllTblNhaCungCap() {
           ArrayList<NhaCungCap> dsncc = new ArrayList<>();
           try {
               ConnectDB.getInstance();
               Connection con = ConnectDB.getConnection();
               String sql = "Select * from NhaCungCap";
              Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String maNhaCungCap = rs.getString(1);
                String tenNhaCungCap = rs.getString(2);
                String soDienThoai = rs.getString(3);
                String email = rs.getString(4);
                String diaChi = rs.getString(5);
                NhaCungCap ncc = new NhaCungCap(maNhaCungCap, tenNhaCungCap, soDienThoai, email, diaChi);
                dsncc.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsncc;
       }
       public ArrayList<String> getAllTenNhaCungCap(){
           ArrayList<String> dsTenNCC = new ArrayList<>();
           try {
               ConnectDB.getInstance();
               Connection con = ConnectDB.getConnection();
               String sql = "Select tenNCC from NhaCungCap";
               Statement statement = con.createStatement();
               ResultSet rs = statement.executeQuery(sql);
               while (rs.next()) {                   
                   dsTenNCC.add(rs.getString("tenNCC"));
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
           return dsTenNCC;
       }

       public String getMaNCCByTen(String tenNCC) {
           String ma = null;
           try {
               ConnectDB.getInstance();
               Connection con = ConnectDB.getConnection();
               String sql = "SELECT maNCC FROM NhaCungCap WHERE tenNCC = ?";
               PreparedStatement ps = con.prepareStatement(sql);
               ps.setString(1, tenNCC);
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                   ma = rs.getString("maNCC");
               }
               rs.close();
               ps.close();
           } catch (Exception e) {
               e.printStackTrace();
           }
           return ma;
       }
       public String getTenNCCByMa(String maNCC) {
    	    try {
    	        Connection con = ConnectDB.getConnection();
    	        PreparedStatement ps = con.prepareStatement("SELECT tenNCC FROM NhaCungCap WHERE maNCC = ?");
    	        ps.setString(1, maNCC);
    	        ResultSet rs = ps.executeQuery();
    	        if (rs.next()) return rs.getString("tenNCC");
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	    return null;
    	}

}
