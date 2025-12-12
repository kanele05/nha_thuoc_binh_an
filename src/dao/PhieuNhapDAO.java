package dao;

import connectDB.ConnectDB;
import entities.NhanVien;
import entities.NhaCungCap;
import entities.PhieuNhap;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class PhieuNhapDAO {

    public PhieuNhapDAO() {}
    public ArrayList<PhieuNhap> getAllPhieuNhap() {
        ArrayList<PhieuNhap> list = new ArrayList<>();
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM PhieuNhap";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String maPN = rs.getString("maPN");
                LocalDate ngayTao = rs.getDate("ngayTao").toLocalDate();
                double tongTien = rs.getDouble("tongTien");
                String trangThai = rs.getString("trangThai");

                // Chỉ lấy mã
                String maNV = rs.getString("maNV");
                String maNCC = rs.getString("maNCC");

                // Tạo object nhưng chỉ có mã
                NhanVien nhanVien = new NhanVien(maNV);
                NhaCungCap nhaCungCap = new NhaCungCap(maNCC);

                PhieuNhap pn = new PhieuNhap(maPN, ngayTao, tongTien, trangThai, nhanVien, nhaCungCap);
                list.add(pn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public String generateMaPhieuNhap() {
         try {
             ConnectDB.getInstance();
             Connection con = ConnectDB.getConnection();
             
             // Lấy mã phiếu nhập lớn nhất hiện tại (ví dụ: PN0123)
             String sql = "SELECT MAX(maPN) FROM PhieuNhap WHERE maPN LIKE 'PN%'";
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             
             int nextNumber = 1; // nếu chưa có phiếu nào
             
             if (rs.next()) {
                 String maxMa = rs.getString(1);
                 if (maxMa != null && maxMa.startsWith("PN")) {
                     String numberPart = maxMa.substring(2); // bỏ "PN"
                     try {
                         int current = Integer.parseInt(numberPart);
                         nextNumber = current + 1;
                     } catch (NumberFormatException e) {
                         nextNumber = 1;
                     }
                 }
             }
             
             rs.close();
             ps.close();
             return String.format("PN%04d", nextNumber);
             
         } catch (Exception e) {
             e.printStackTrace();
             return "PN0001"; 
         }
     }
    public PhieuNhap getPhieuNhapById(String id) {
        PhieuNhap pn = null;

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM PhieuNhap WHERE maPN = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String maPN = rs.getString("maPN");
                LocalDate ngayTao = rs.getDate("ngayTao").toLocalDate();
                double tongTien = rs.getDouble("tongTien");
                String trangThai = rs.getString("trangThai");

                String maNV = rs.getString("maNV");
                String maNCC = rs.getString("maNCC");

                pn = new PhieuNhap(
                        maPN, ngayTao, tongTien, trangThai,
                        new NhanVien(maNV), new NhaCungCap(maNCC)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pn;
    }

    public boolean addPhieuNhap(PhieuNhap pn) {
        boolean result = false;

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "INSERT INTO PhieuNhap(maPN, ngayTao, tongTien, trangThai, maNV, maNCC) VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pn.getMaPN());
            ps.setDate(2, Date.valueOf(pn.getNgayTao()));
            ps.setDouble(3, pn.getTongTien());
            ps.setString(4, pn.getTrangThai());
            String maNV = (pn.getNhanVien() != null) ? pn.getNhanVien().getMaNV() : "Admin";
            ps.setString(5, maNV);
            ps.setString(6, pn.getNcc().getMaNCC());

            result = ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // UPDATE
    public boolean updatePhieuNhap(PhieuNhap pn) {
        boolean result = false;

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE PhieuNhap SET ngayTao=?, tongTien=?, trangThai=?, maNV=?, maNCC=? WHERE maPN=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(pn.getNgayTao()));
            ps.setDouble(2, pn.getTongTien());
            ps.setString(3, pn.getTrangThai());
            ps.setString(4, pn.getNhanVien().getMaNV());
            ps.setString(5, pn.getNcc().getMaNCC());
            ps.setString(6, pn.getMaPN());

            result = ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // DELETE
    public boolean deletePhieuNhap(String maPN) {
        boolean result = false;

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "DELETE FROM PhieuNhap WHERE maPN = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maPN);

            result = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
