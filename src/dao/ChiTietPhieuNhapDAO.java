package dao;

import connectDB.ConnectDB;
import entities.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ChiTietPhieuNhapDAO {

    public ChiTietPhieuNhapDAO() {}

    private ChiTietPhieuNhap mapRow(ResultSet rs) throws Exception {
        String maPN = rs.getString("maPN");
        String maThuoc = rs.getString("maThuoc");
        String maLo = rs.getString("maLo");

        LocalDate hsd = rs.getDate("hanSuDung").toLocalDate();
        int soLuong = rs.getInt("soLuong");
        double donGia = rs.getDouble("donGia");
        double thanhTien = rs.getDouble("thanhTien");

        PhieuNhap pn = new PhieuNhap(maPN);
        Thuoc thuoc = new Thuoc(maThuoc);
        LoThuoc loThuoc = new LoThuoc(maLo);

        return new ChiTietPhieuNhap(pn, thuoc, loThuoc, hsd, soLuong, donGia, thanhTien);
    }

    public ArrayList<ChiTietPhieuNhap> getAll() {
        ArrayList<ChiTietPhieuNhap> ds = new ArrayList<>();

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT * FROM ChiTietPhieuNhap";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ds.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public ArrayList<ChiTietPhieuNhap> getByMaPN(String maPN) {
        ArrayList<ChiTietPhieuNhap> ds = new ArrayList<>();

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPN = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maPN);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ds.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public boolean insert(ChiTietPhieuNhap ct) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "INSERT INTO ChiTietPhieuNhap VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, ct.getPn().getMaPN());
            pst.setString(2, ct.getThuoc().getMaThuoc());
            pst.setString(3, ct.getLoThuoc().getMaLo());

            pst.setDate(4, Date.valueOf(ct.getHanSuDung()));
            pst.setInt(5, ct.getSoLuong());
            pst.setDouble(6, ct.getDonGia());
            pst.setDouble(7, ct.getThanhTien());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(ChiTietPhieuNhap ct) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "UPDATE ChiTietPhieuNhap SET hanSuDung=?, soLuong=?, donGia=?, thanhTien=? " +
                         "WHERE maPN=? AND maThuoc=? AND maLo=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setDate(1, Date.valueOf(ct.getHanSuDung()));
            pst.setInt(2, ct.getSoLuong());
            pst.setDouble(3, ct.getDonGia());
            pst.setDouble(4, ct.getThanhTien());

            pst.setString(5, ct.getPn().getMaPN());
            pst.setString(6, ct.getThuoc().getMaThuoc());
            pst.setString(7, ct.getLoThuoc().getMaLo());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maPN, String maThuoc, String maLo) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPN=? AND maThuoc=? AND maLo=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, maPN);
            pst.setString(2, maThuoc);
            pst.setString(3, maLo);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteByMaPN(String maPN) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "DELETE FROM ChiTietPhieuNhap WHERE maPN=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, maPN);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
