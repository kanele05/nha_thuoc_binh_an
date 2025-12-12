package dao;

import connectDB.ConnectDB;
import entities.Thuoc;
import entities.NhomThuoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThuocDAO {

    private Connection connection;

    public ThuocDAO(Connection connection) {
        this.connection = connection;
    }

    public ThuocDAO() {}

    private Thuoc mapThuoc(ResultSet rs) throws SQLException {
        NhomThuoc nt = new NhomThuoc(
                rs.getString("maNhom"),
                rs.getString("tenNhom")
        );

        return new Thuoc(
                rs.getString("maThuoc"),
                rs.getString("tenThuoc"),
                rs.getString("hoatChat"),
                rs.getString("donViTinh"),
                rs.getBoolean("trangThai"),
                nt
        );
    }

    public List<Thuoc> getAllThuoc() throws SQLException {
        
        List<Thuoc> list = new ArrayList<>();
        String sql = 
               " SELECT t.maThuoc, t.tenThuoc, t.hoatChat, t.donViTinh, t.trangThai,"
               +       "n.maNhom, n.tenNhom"
               + "FROM Thuoc t"
               + "LEFT JOIN NhomThuoc n ON t.maNhom = n.maNhom";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()) {
                list.add(mapThuoc(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Thuoc getThuocById(String maThuoc) {
        Thuoc thuoc = null;
        String sql = "SELECT * FROM Thuoc WHERE maThuoc = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maThuoc);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                thuoc = new Thuoc();
                thuoc.setMaThuoc(rs.getString("maThuoc"));
                thuoc.setTenThuoc(rs.getString("tenThuoc"));
                thuoc.setHoatChat(rs.getString("hoatChat"));
                thuoc.setDonViTinh(rs.getString("donViTinh"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thuoc;
    }
    public String getTenThuocById(String maThuoc) {
        String tenThuoc = null;
        String sql = "SELECT tenThuoc FROM Thuoc WHERE maThuoc = ?";

        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maThuoc);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tenThuoc = rs.getString("tenThuoc");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tenThuoc;
    }

    public boolean insertThuoc(Thuoc t) {
        String sql = "INSERT INTO Thuoc(maThuoc, tenThuoc, hoatChat, donViTinh, trangThai, maNhom) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getMaThuoc());
            ps.setString(2, t.getTenThuoc());
            
            // Cho phép null
            if (t.getHoatChat() != null && !t.getHoatChat().trim().isEmpty()) {
                ps.setString(3, t.getHoatChat());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }
            
            if (t.getDonViTinh() != null && !t.getDonViTinh().trim().isEmpty()) {
                ps.setString(4, t.getDonViTinh());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            
            ps.setBoolean(5,true);
            if (t.getNhomThuoc() != null && t.getNhomThuoc().getMaNhom() != null) {
                ps.setString(6, t.getNhomThuoc().getMaNhom());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateThuoc(Thuoc t) {
        String sql = 
                "UPDATE Thuoc"
               + "SET tenThuoc = ?, hoatChat = ?, donViTinh = ?, trangThai = ?, maNhom = ?"
               + "WHERE maThuoc = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, t.getTenThuoc());
            ps.setString(2, t.getHoatChat());
            ps.setString(3, t.getDonViTinh());
            ps.setBoolean(4, t.isTrangThai());
            ps.setString(5, t.getNhomThuoc().getMaNhom());
            ps.setString(6, t.getMaThuoc());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteThuoc(String maThuoc) {
        String sql = "DELETE FROM Thuoc WHERE maThuoc = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maThuoc);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
