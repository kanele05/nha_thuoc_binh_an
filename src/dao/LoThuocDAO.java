package dao;

import connectDB.ConnectDB;
import entities.LoThuoc;
import entities.Thuoc;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoThuocDAO {

    private ThuocDAO thuocDAO;
    public LoThuocDAO(){
        this.thuocDAO = new ThuocDAO();
    }

    public ArrayList<LoThuoc> getAllLoThuoc() {
        ArrayList<LoThuoc> ds = new ArrayList<>();
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT * FROM LoThuoc";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String maLo = rs.getString("maLo");
                String maThuoc = rs.getString("maThuoc");
                LocalDate ngayNhap = rs.getDate("ngayNhap").toLocalDate();
                LocalDate hanSuDung = rs.getDate("hanSuDung").toLocalDate();
                int soLuongTon = rs.getInt("soLuongTon");
                boolean trangThai = rs.getBoolean("trangThai");
                Thuoc thuoc = thuocDAO.getThuocById(maThuoc);
                LoThuoc lt = new LoThuoc(
                        maLo,
                        thuoc,
                        ngayNhap,
                        hanSuDung,
                        soLuongTon,
                        trangThai
                );

                ds.add(lt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    public LoThuoc getLoByMa(String maLo) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT * FROM LoThuoc WHERE maLo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maLo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String maThuoc = rs.getString("maThuoc");
                LocalDate ngayNhap = rs.getDate("ngayNhap").toLocalDate();
                LocalDate hanSuDung = rs.getDate("hanSuDung").toLocalDate();
                int soLuongTon = rs.getInt("soLuongTon");
                boolean trangThai = rs.getBoolean("trangThai");

                Thuoc thuoc = thuocDAO.getThuocById(maThuoc);

                return new LoThuoc(
                        maLo,
                        thuoc,
                        ngayNhap,
                        hanSuDung,
                        soLuongTon,
                        trangThai
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public LoThuoc getLoByMaLoAndMaThuoc(String maLo, String maThuoc) {
        LoThuoc lt = null;
        String sql = "SELECT * FROM LoThuoc WHERE maLo = ? AND maThuoc = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maLo);
            ps.setString(2, maThuoc);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                lt = new LoThuoc();
                lt.setMaLo(rs.getString("maLo"));
                Thuoc thuoc = thuocDAO.getThuocById(rs.getString("maThuoc"));
                lt.setThuoc(thuoc);
                lt.setNgayNhap(rs.getDate("ngayNhap").toLocalDate());
                lt.setHanSuDung(rs.getDate("hanSuDung").toLocalDate());
                lt.setSoLuongTon(rs.getInt("soLuongTon"));
                lt.setTrangThai(rs.getBoolean("trangThai"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lt;
    }

    public boolean capNhatSoLuongTon(String maLo, String maThuoc, int soLuongMoi) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE LoThuoc SET soLuongTon = ? WHERE maLo = ? AND maThuoc = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, soLuongMoi);
            ps.setString(2, maLo);
            ps.setString(3, maThuoc);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertLoThuoc(LoThuoc lt) {
        String sql = "INSERT INTO LoThuoc(maLo, maThuoc, ngayNhap, hanSuDung, soLuongTon, trangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, lt.getMaLo());
            ps.setString(2, lt.getThuoc().getMaThuoc());
            ps.setDate(3, Date.valueOf(lt.getNgayNhap()));
            ps.setDate(4, Date.valueOf(lt.getHanSuDung()));
            ps.setInt(5, lt.getSoLuongTon());
            ps.setBoolean(6, lt.isTrangThai());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateLoThuoc(LoThuoc lt) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "UPDATE LoThuoc SET maThuoc=?, ngayNhap=?, hanSuDung=?, soLuongTon=?, trangThai=? WHERE maLo=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, lt.getThuoc().getMaThuoc());
            ps.setDate(2, Date.valueOf(lt.getNgayNhap()));
            ps.setDate(3, Date.valueOf(lt.getHanSuDung()));
            ps.setInt(4, lt.getSoLuongTon());
            ps.setBoolean(5, lt.isTrangThai());
            ps.setString(6, lt.getMaLo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteLoThuoc(String maLo) {
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "DELETE FROM LoThuoc WHERE maLo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maLo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
