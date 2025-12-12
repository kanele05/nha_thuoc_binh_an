package gui.application.form.PhieuNhap;

import com.formdev.flatlaf.FlatClientProperties;
import connectDB.ConnectDB;
import dao.ChiTietPhieuNhapDAO;
import dao.LoThuocDAO;
import dao.NhaCungCapDAO;
import dao.PhieuNhapDAO;
import dao.ThuocDAO;
import entities.ChiTietPhieuNhap;
import entities.LoThuoc;
import entities.NhaCungCap;
import entities.NhanVien;
import entities.NhomThuoc;
import entities.PhieuNhap;
import entities.Thuoc;
import gui.application.form.NhaCungCap.DialogNhaCungCap;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import raven.toast.Notifications;
public class FormNhapHang extends javax.swing.JPanel {

    private JComboBox<String> cbNhaCungCap;
    private JTextField txtNguoiNhap;
    private JTextField txtGhiChu;
    private JLabel lbTongTien;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongCong;
    private boolean isUpdating = false;
    private NhaCungCap nhaCungCap;
    private PhieuNhapDAO pnDao = new PhieuNhapDAO(); 
    public FormNhapHang() {
        initComponents();
        init();
    }
    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow][]"));

        add(createHeaderPanel(), "wrap 20");
        add(createActionPanel(), "wrap 10");
        add(createTablePanel(), "grow");
        add(createFooterPanel(), "growx");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Nhập Hàng");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 15", "[][grow]push[][]", "[]10[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");
        NhaCungCapDAO nccDao = new NhaCungCapDAO();
        ArrayList<String> dsNCC = nccDao.getAllTenNhaCungCap();
        String[] items = dsNCC.toArray(new String[0]);
        cbNhaCungCap = new JComboBox<>(items);
        
        txtNguoiNhap = new JTextField("Admin"); 
        txtNguoiNhap.setEditable(false);
        JButton btnAddNCC = new JButton("+");
        btnAddNCC.putClientProperty(FlatClientProperties.STYLE,
                "arc:15; background:#FFFFFF; font:bold;");

        btnAddNCC.setMargin(new Insets(2, 10, 2, 10));
        btnAddNCC.addActionListener(e -> actionThem());

        panel.add(new JLabel("Nhà cung cấp:"));  
        panel.add(cbNhaCungCap, "w 300, split 2");
        panel.add(btnAddNCC, "gapleft 5, wrap");
        
        panel.add(new JLabel("Người nhập:"));
        panel.add(txtNguoiNhap, "w 300");
        JButton btnAddHang = new JButton("Thêm hàng");
        JButton btnXoaHang = new JButton("Xóa hàng");
        JButton btnMau = new JButton("Tải file mẫu");
        btnMau.addActionListener(e -> actionDownloadTemplate());
        btnAddHang.addActionListener(e -> actionThemHang());
        JButton btnImport = new JButton("Nhập từ Excel");
        btnImport.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#009688;"
                + "foreground:#ffffff;"
                + "font:bold;"
                + "iconTextGap:10"); 
        btnXoaHang.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#F44336;"
                + "foreground:#ffffff;"
                + "font:bold;"
                + "iconTextGap:10"); 
        btnAddHang.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#35e838;"
                + "foreground:#ffffff;"
                + "font:bold;"
                + "iconTextGap:10");
        btnMau.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#ffffff;"              
                + "font:bold;"
                + "iconTextGap:10");
        btnImport.addActionListener(e -> importExcel());

        panel.add(btnMau, "cell 2 0, span 1 2, right");
        panel.add(btnImport, "cell 3 0, span 1 2");
        panel.add(btnXoaHang,"cell 4 0, span 1 2, left");
        panel.add(btnAddHang,"cell 5 0, span 1 2");

        return panel;
    }

    private void actionDownloadTemplate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file mẫu nhập hàng");
        fileChooser.setSelectedFile(new File("Mau_Nhap_Hang.csv")); // Đặt tên mặc định

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            saveCSVFile(fileToSave);
        }
    }

    private void saveCSVFile(File file) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write("\ufeff");            
            // Header 
            writer.write("Mã thuốc, Tên Thuốc, Đơn vị tính, Đơn giá, Số lượng, Lô 1, Hạn sử dụng 1, Số lượng 1, Lô 2, Hạn sử dụng 2, Số lượng 2");
            writer.newLine();            
            // Dữ liệu mẫu dòng 1
            writer.write("T001,Paracetamol 500mg,Vỉ,L001,12/2025,100,3500");
            writer.newLine();           
            // Dữ liệu mẫu dòng 2
            writer.write("T002,Amoxicillin 500mg,Hộp,L002,06/2026,50,45000");
            writer.newLine();            
            // Dữ liệu mẫu dòng 3
            writer.write("T003,Vitamin C 1000mg,Lọ,L003,12/2025,200,25000");
            writer.newLine();
            
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã lưu file mẫu thành công!");
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Không thể mở file");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Lỗi khi lưu file!");
        }
    }
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = {
            "Mã thuốc", "Tên thuốc", "Đơn vị tính", 
            "Đơn giá", "Số lượng tổng", "Số lô", 
            "Số lượng lô", "Hạn sử dụng", "Thành tiền"
        };
        model = new DefaultTableModel(columns, 0) {
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true; gridColor:#e0e0e0");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel createFooterPanel() {
    JPanel panel = new JPanel(new MigLayout("insets 15, fillx", "[grow][][200!]", "[]"));
    panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:lighten(#E8F5E9,3%)");

    txtGhiChu = new JTextField();
    txtGhiChu.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ghi chú phiếu nhập...");
    
    lbTongTien = new JLabel("0 ₫");
    lbTongTien.putClientProperty(FlatClientProperties.STYLE, "font:bold +10; foreground:#D32F2F");

    JButton btnHuy = new JButton("HỦY");
    btnHuy.putClientProperty(FlatClientProperties.STYLE, ""
            + "background:#F44336;"
            + "foreground:#ffffff;"
            + "font:bold +2;"
            + "borderWidth:0;"
            + "arc:10");
    btnHuy.addActionListener(e -> actionHuy());

    JButton btnLuu = new JButton("LƯU PHIẾU NHẬP");
    btnLuu.putClientProperty(FlatClientProperties.STYLE, ""
            + "background:#4CAF50;"
            + "foreground:#ffffff;"
            + "font:bold +2;"
            + "borderWidth:0;"
            + "arc:10");
    btnLuu.addActionListener(e -> actionLuuPhieu());

    panel.add(new JLabel("Ghi chú:"), "split 2, gapright 10");
    panel.add(txtGhiChu, "growx");
    
    panel.add(new JLabel("TỔNG CỘNG:"), "right");
    panel.add(lbTongTien, "right, wrap");
    panel.add(btnHuy, "split 2, growx, h 50!");
    panel.add(btnLuu, "growx, h 50!"); 

    return panel;
}
    private class RightAlignRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }


    private void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel");

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fileChooser.getSelectedFile();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = file.getName().toLowerCase().endsWith(".xlsx")
                 ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            model.setRowCount(0);

            List<ExcelImportError> errors = validateExcelData(sheet);

            if (!errors.isEmpty()) {
                StringBuilder msg = new StringBuilder("<html><b>Không thể import do có lỗi:</b><br><br>");
                for (int i = 0; i < Math.min(20, errors.size()); i++) {
                    ExcelImportError e = errors.get(i);
                    msg.append("Dòng ").append(e.row).append(": ").append(e.message).append("<br>");
                }
                if (errors.size() > 20) {
                    msg.append("... và ").append(errors.size() - 20).append(" lỗi khác.");
                }
                msg.append("</html>");

                JOptionPane.showMessageDialog(this, msg.toString(),
                    "Lỗi dữ liệu Excel", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            final int LOT_START_COL = 5;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String ma = getCellString(row.getCell(0));
                String ten = getCellString(row.getCell(1));
                String dvt = getCellString(row.getCell(2));
                Double dongia = getCellNumber(row.getCell(3));
                Double tongSL = getCellNumber(row.getCell(4));

                int lastCellNum = row.getLastCellNum();

                for (int c = LOT_START_COL; c + 2 < lastCellNum; c += 3) {
                    String lo = getCellString(row.getCell(c));
                    Date hsdDate = getCellDate(row.getCell(c + 1));
                    Double sl = getCellNumber(row.getCell(c + 2));

                    boolean allEmpty = (lo == null || lo.trim().isEmpty())
                            && hsdDate == null
                            && (sl == null || sl <= 0);
                    if (allEmpty) continue;

                    double thanhTien = (dongia != null ? dongia : 0) * (sl != null ? sl : 0);

                    String hsdFormatted = hsdDate == null ? "" :
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(hsdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                    model.addRow(new Object[]{
                        ma,
                        ten.isEmpty() ? "Không tên" : ten,
                        dvt.isEmpty() ? "Hộp" : dvt,
                        formatMoney(dongia),
                        tongSL,
                        lo,
                        sl,
                        hsdFormatted,
                        formatMoney(thanhTien)
                    });
                }
            }

            JOptionPane.showMessageDialog(this,
                "Import thành công " + model.getRowCount() + " lô thuốc!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            tinhTienHang();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
}
     private void actionThem() {
        DialogNhaCungCap dialog = new DialogNhaCungCap(this, null);
        dialog.setVisible(true);
        if(dialog.isSave()){
            model.addRow(dialog.getData());
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm nhà cung cấp thành công!");
        }
    }     
    private void actionThemHang() {
        DialogAddHang dialog = new DialogAddHang(this, null);
        dialog.setVisible(true);
        if(dialog.isSave()){
            model.addRow(dialog.getData());
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm hàng hóa thành công!");
        }
    }
    private void actionHuy() {
    	    int confirm = JOptionPane.showConfirmDialog(this, 
    	            "Bạn có chắc muốn hủy phiếu nhập này?\nTất cả dữ liệu sẽ bị xóa.", 
    	            "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
    	    
    	    if (confirm == JOptionPane.YES_OPTION) {
    	        model.setRowCount(0);
    	        lbTongTien.setText("0 ₫");
    	        txtGhiChu.setText("");
    	        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Đã hủy phiếu nhập.");
    	    }
    	}
    private void actionLuuPhieu() {
         if (!kiemTraDauVao()) return;

         int confirm = JOptionPane.showConfirmDialog(this,
                 "Xác nhận lưu phiếu nhập với tổng tiền: " + lbTongTien.getText() + "?",
                 "Xác nhận nhập hàng", JOptionPane.YES_NO_OPTION);
         if (confirm != JOptionPane.YES_OPTION) return;

         Connection con = null;
         try {
             con = ConnectDB.getConnection();
             String maPhieuNhap = generateMaPhieuNhap();
             PhieuNhap pn = taoPhieuNhap(maPhieuNhap);

             luuPhieuNhap(pn);
             luuChiTietVaLoThuoc(pn);

             Notifications.getInstance().show(Notifications.Type.SUCCESS,
                     Notifications.Location.TOP_CENTER,
                     "NHẬP KHO THÀNH CÔNG! Mã phiếu: " + maPhieuNhap);
             lamSachForm();

         } catch (Exception e) {
             e.printStackTrace();
             rollbackVaBaoLoi(con, e);
         } finally {
             dongKetNoi(con);
         }
     }

    private boolean kiemTraDauVao() {
    	    if (model.getRowCount() == 0) {
    	        thongBao("Chưa có dữ liệu để lưu!");
    	        return false;
    	    }
    	    if (cbNhaCungCap.getSelectedItem() == null) {
    	        thongBao("Chưa chọn nhà cung cấp!");
    	        return false;
    	    }

    	    Set<String> loSet = new HashSet<>();

    	    for (int i = 0; i < model.getRowCount(); i++) {
    	        String maThuoc = getCell(i, 0);
    	        String soLo = getCell(i, 5);
    	        String hsdStr = getCell(i, 7);
    	        String slStr = getCell(i, 6);
    	        String donGiaStr = getCell(i, 3);

    	        if (maThuoc.isEmpty() || soLo.isEmpty() || hsdStr.isEmpty() || slStr.isEmpty()) {
    	            thongBao("Dòng " + (i+1) + ": Thiếu thông tin bắt buộc!");
    	            table.setRowSelectionInterval(i, i);
    	            return false;
    	        }
    	        try {
    	            Double sl = Double.parseDouble(slStr.replace(",", ""));
    	            double donGia = parseMoney(donGiaStr);
    	            if (sl <= 0 || donGia <= 0) {
    	                thongBao("Dòng " + (i+1) + ": Số lượng hoặc đơn giá phải > 0!");
    	                table.setRowSelectionInterval(i, i);
    	                return false;
    	            }
    	        } catch (Exception e) {
    	            thongBao("Dòng " + (i+1) + ": Số lượng hoặc đơn giá không hợp lệ!");
    	            table.setRowSelectionInterval(i, i);
    	            return false;
    	        }
    	        try {
    	            LocalDate hsd = LocalDate.parse(hsdStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    	            if (hsd.isBefore(LocalDate.now())) {
    	                thongBao("Dòng " + (i+1) + ", Lô " + soLo + ": Thuốc đã hết hạn!");
    	                table.setRowSelectionInterval(i, i);
    	                return false;
    	            }
    	        } catch (Exception e) {
    	            thongBao("Dòng " + (i+1) + ": Ngày hạn sử dụng sai định dạng (dd/MM/yyyy)!");
    	            table.setRowSelectionInterval(i, i);
    	            return false;
    	        }

    	        String key = maThuoc + "_" + soLo;
    	        if (!loSet.add(key)) {
    	            thongBao("Dòng " + (i+1) + ": Lô '" + soLo + "' bị trùng trong danh sách!");
    	            table.setRowSelectionInterval(i, i);
    	            return false;
    	        }
    	    }
    	    return true;
    	}
     private PhieuNhap taoPhieuNhap(String maPhieuNhap) {
         PhieuNhap pn = new PhieuNhap();
         pn.setMaPN(maPhieuNhap);
         pn.setNgayTao(LocalDate.now());
         pn.setTongTien(tinhTienHang2());
         pn.setTrangThai("Đã nhập");

         String nguoiNhap = txtNguoiNhap.getText().trim();
         if (nguoiNhap.isEmpty()) nguoiNhap = "Admin";
         pn.setNhanVien(nguoiNhap);

         String tenNCC = cbNhaCungCap.getSelectedItem().toString().trim();
         String maNCC = new NhaCungCapDAO().getMaNCCByTen(tenNCC);
         if (maNCC == null || maNCC.isEmpty()) maNCC = "NCC001";

         NhaCungCap ncc = new NhaCungCap();
         ncc.setMaNCC(maNCC);
         pn.setNcc(ncc);

         return pn;
     }

     private void luuPhieuNhap(PhieuNhap pn) throws Exception {
         if (!new PhieuNhapDAO().addPhieuNhap(pn)) {
             throw new Exception("Lưu phiếu nhập thất bại!");
         }
     }
    private boolean isLoThuocDaTonTai(String maThuoc, String soLo) {
    	    String sql = "SELECT 1 FROM LoThuoc WHERE maThuoc = ? AND maLo = ?";
    	    try (Connection con = ConnectDB.getConnection();
    	         PreparedStatement ps = con.prepareStatement(sql)) {
    	        ps.setString(1, maThuoc.trim());
    	        ps.setString(2, soLo.trim());
    	        return ps.executeQuery().next();
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        return false;
    	    }
    	}
     private void luuChiTietVaLoThuoc(PhieuNhap pn) throws Exception {
    	    ChiTietPhieuNhapDAO ctDao = new ChiTietPhieuNhapDAO();
    	    LoThuocDAO loDao = new LoThuocDAO();
    	    ThuocDAO thuocDao = new ThuocDAO();

    	    for (int i = 0; i < model.getRowCount(); i++) {
    	        String maThuoc = getCell(i, 0);
    	        String tenThuoc = getCell(i, 1);
    	        String donViTinh = getCell(i, 2);
    	        double donGia = parseMoney(getCell(i, 3));
    	        String soLo = getCell(i, 5);
    	        int soLuongLo = parseIntFromCell(getCell(i, 6));
    	        LocalDate hanSuDung = LocalDate.parse(getCell(i, 7), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    	        double thanhTien = donGia * soLuongLo;

    	        if (thuocDao.getThuocById(maThuoc) == null) {
    	            Thuoc t = new Thuoc();
    	            t.setMaThuoc(maThuoc);
    	            t.setTenThuoc(tenThuoc.isEmpty() ? "Thuốc mới" : tenThuoc);
    	            t.setDonViTinh(donViTinh.isEmpty() ? "Hộp" : donViTinh);
    	            t.setTrangThai(true);

    	            NhomThuoc nhom = new NhomThuoc();
    	            nhom.setMaNhom("NT001");
    	            t.setNhomThuoc(nhom);

    	            thuocDao.insertThuoc(t);
    	        }
    	        if (isLoThuocDaTonTai(maThuoc, soLo)) {
    	            throw new Exception("Dòng " + (i+1) + ": Lô '" + soLo + "' của thuốc '" + maThuoc + "' đã tồn tại trong hệ thống!");
    	        }
    	        LoThuoc lo = new LoThuoc();
    	        lo.setMaLo(soLo);
    	        lo.setThuoc(new Thuoc(maThuoc));
    	        lo.setNgayNhap(LocalDate.now());
    	        lo.setHanSuDung(hanSuDung);
    	        lo.setSoLuongTon(soLuongLo);
    	        lo.setTrangThai(true);
    	        loDao.insertLoThuoc(lo);

    	        ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
    	        ct.setPn(pn);
    	        ct.setThuoc(new Thuoc(maThuoc));
    	        ct.setLoThuoc(lo);
    	        ct.setHanSuDung(hanSuDung);
    	        ct.setSoLuong(soLuongLo);
    	        ct.setDonGia(donGia);
    	        ct.setThanhTien(thanhTien);

    	        ctDao.insert(ct);
    	    }
    	}

     private String getCell(int row, int col) {
         Object val = model.getValueAt(row, col);
         return val == null ? "" : val.toString().trim();
     }

     private void thongBao(String msg) {
         Notifications.getInstance().show(Notifications.Type.WARNING,
                 Notifications.Location.TOP_CENTER, msg);
     }

     private void lamSachForm() {
         model.setRowCount(0);
         lbTongTien.setText("0 đ");
         txtGhiChu.setText("");
     }

     private void rollbackVaBaoLoi(Connection con, Exception e) {
         if (con != null) {
             try { con.rollback(); } catch (Exception ex) {}
         }
         Notifications.getInstance().show(Notifications.Type.ERROR,
                 Notifications.Location.TOP_CENTER, "Lỗi: " + e.getMessage());
     }

     private void dongKetNoi(Connection con) {
         if (con != null) {
             try {
                 con.setAutoCommit(true);
                 con.close();
             } catch (Exception e) {}
         }
     }

     private String generateMaPhieuNhap() {
         try (Connection c = ConnectDB.getConnection();
              PreparedStatement ps = c.prepareStatement("SELECT MAX(maPN) FROM PhieuNhap");
              ResultSet rs = ps.executeQuery()) {
             if (rs.next() && rs.getString(1) != null) {
                 String max = rs.getString(1);
                 int num = Integer.parseInt(max.replace("PN", "")) + 1;
                 return String.format("PN%04d", num);
             }
         } catch (Exception e) {}
         return "PN0001";
     }

     private void tinhTienHang() {
    	    isUpdating = true;
    	    double tongCong = 0;
    	    try {
    	        for (int i = 0; i < model.getRowCount(); i++) {

    	            try {
    	                String giaStr = model.getValueAt(i, 3).toString();
    	                String slStr  = model.getValueAt(i, 6).toString();
    	                double gia = 0;
    	                double sl  = 0;

    	                if (giaStr != null) {
    	                    giaStr = giaStr.replace(".", "").replace(",", "").replace("đ", "").trim();
    	                    gia = giaStr.isEmpty() ? 0 : Double.parseDouble(giaStr);
    	                }
                         if (slStr != null) {
                            slStr = slStr.trim();
                            sl = slStr.isEmpty() ? 0 : Double.parseDouble(slStr);
                        }
    	                double thanhTien = gia * sl;
    	                model.setValueAt(formatMoney(thanhTien), i, 8);
    	                tongCong += thanhTien;
    	            } catch (Exception e) {
                        e.printStackTrace();
    	            }
    	        }
    	        lbTongTien.setText(formatMoney(tongCong));
    	    } finally {
    	        isUpdating = false;
    	    }
    	}
     private double tinhTienHang2() {
    	    isUpdating = true;
    	    double tongCong = 0;

    	    try {
    	        for (int i = 0; i < model.getRowCount(); i++) {
    	            try {

    	                String giaStr = model.getValueAt(i, 3).toString()
    	                        .replace(".", "").replace("đ", "").trim();
    	                String slStr = model.getValueAt(i, 6).toString()
    	                        .replace(".", "").trim();

    	                double donGia = giaStr.isEmpty() ? 0 : Double.parseDouble(giaStr);
    	                double soLuong = slStr.isEmpty() ? 0 : Double.parseDouble(slStr);

    	                double thanhTien = donGia * soLuong;

    	                model.setValueAt(formatMoney(thanhTien), i, 8);

    	                tongCong += thanhTien;
    	            } catch (Exception e) {
    	                model.setValueAt("0 đ", i, 8); 
    	            }
    	        }
    	        lbTongTien.setText(formatMoney(tongCong));
    	    } finally {
    	        isUpdating = false;
    	    }
    	    return tongCong;
    	} 
     private double parseMoney(String moneyStr) {
    	    if (moneyStr == null || moneyStr.trim().isEmpty()) {
    	        return 0.0;
    	    }
    	    String clean = moneyStr
    	            .replace("đ", "")
    	            .replace("Đ", "")
    	            .replace(".", "")
    	            .replace(",", "")  
    	            .replace(" ", "")
    	            .trim();
    	    try {
    	        return Double.parseDouble(clean);
    	    } catch (NumberFormatException e) {
    	        System.err.println("Lỗi parse tiền: " + moneyStr);
    	        return 0.0;
    	    }
    	}
    private Object getCellValue(Cell cell) {
            if (cell == null) return "";
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    }
                    return cell.getNumericCellValue();
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case FORMULA:
                    try {
                        return cell.getNumericCellValue();
                    } catch (Exception e) {
                        return cell.getStringCellValue();
                    }
                default:
                    return "";
            }
        }
    private static class ExcelImportError {
        final int row;         
        final String message;

        ExcelImportError(int row, String message) {
            this.row = row;
            this.message = message;
        }
    }
    private List<ExcelImportError> validateExcelData(Sheet sheet) {
        List<ExcelImportError> errors = new ArrayList<>();
        Set<String> maThuocSet = new HashSet<>();   
        Set<String> loKeySet = new HashSet<>();     

        final int LOT_START_COL = 5;

        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            int excelRow = r + 1;
            String ma = getCellString(row.getCell(0));
            String ten = getCellString(row.getCell(1));
            String dvt = getCellString(row.getCell(2));
            Double dongia = getCellNumber(row.getCell(3));
            if (ma == null || ma.trim().isEmpty()) {
                errors.add(new ExcelImportError(excelRow, "Mã thuốc không được để trống"));
                continue; 
            }
            if (dongia == null || dongia <= 0) {
                errors.add(new ExcelImportError(excelRow, "Đơn giá phải lớn hơn 0"));
            }
            if (!maThuocSet.add(ma.trim().toUpperCase())) {
                errors.add(new ExcelImportError(excelRow, "Mã thuốc '" + ma + "' bị trùng trong file"));
            }
            int lastCellNum = row.getLastCellNum();
            if (lastCellNum <= LOT_START_COL) {
                errors.add(new ExcelImportError(excelRow, "Không có dữ liệu lô nào"));
                continue;
            }
            boolean coLoHopLe = false;
            for (int c = LOT_START_COL; c + 2 < lastCellNum; c += 3) {
                String lo = getCellString(row.getCell(c));
                Date hsdDate = getCellDate(row.getCell(c + 1));
                Double sl = getCellNumber(row.getCell(c + 2));
                boolean allEmpty = (lo == null || lo.trim().isEmpty())
                        && hsdDate == null
                        && (sl == null || sl <= 0);
                if (allEmpty) continue;
                coLoHopLe = true;
                if (lo == null || lo.trim().isEmpty()) {
                    errors.add(new ExcelImportError(excelRow, "Số lô không được để trống (cột " + (c+1) + ")"));
                }
                if (hsdDate == null) {
                    errors.add(new ExcelImportError(excelRow, "Hạn sử dụng không hợp lệ (lô: " + lo + ")"));
                } else {
                    LocalDate hsd = hsdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (hsd.isBefore(LocalDate.now())) {
                        errors.add(new ExcelImportError(excelRow, "Thuốc đã hết hạn: " + hsd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (lô: " + lo + ")"));
                    }
                }
                if (sl == null || sl <= 0) {
                    errors.add(new ExcelImportError(excelRow, "Số lượng lô phải > 0 (lô: " + lo + ")"));
                }
                String key = ma.trim() + "_" + (lo != null ? lo.trim() : "");
                if (!loKeySet.add(key)) {
                    errors.add(new ExcelImportError(excelRow, "Lô '" + lo + "' bị trùng với lô khác của cùng mã thuốc trong file"));
                }
            }
            if (!coLoHopLe) {
                errors.add(new ExcelImportError(excelRow, "Có mã thuốc nhưng không có lô hợp lệ nào"));
            }
        }
        return errors;
    }
    
    
    private String getCellString(Cell cell) {
            if (cell == null) return "";
            CellType type = cell.getCellType();
            switch (type) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date d = cell.getDateCellValue();
                        return d == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(d);
                    }
                    double num = cell.getNumericCellValue();
                    if (num == (long) num) {
                        return String.valueOf((long) num);
                    } else {
                        DecimalFormat df = new DecimalFormat("#.######");
                        return df.format(num);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:                
                    try {
                        double val = cell.getNumericCellValue();
                        if (val == (long) val) return String.valueOf((long) val);
                        return String.valueOf(val);
                    } catch (Exception ex) {
                        return cell.getStringCellValue();
                    }
                case BLANK:
                default:
                    return "";
            }
        }
    private Double getCellNumber(Cell cell) {
        if (cell == null) return 0.0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String s = cell.getStringCellValue().replaceAll("[^0-9\\.,-]", "").replace(",", "");
                if (s.isEmpty()) return 0.0;
                return Double.parseDouble(s);
            } else if (cell.getCellType() == CellType.FORMULA) {
                return cell.getNumericCellValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    private Date getCellDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String s = cell.getStringCellValue().trim();
                try {
                    return new SimpleDateFormat("dd/MM/yyyy").parse(s);
                } catch (Exception ex) {
                    return null;
                }
            }
        } catch (Exception e) { }
        return null;
    }     
    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
    
     private String formatMoney(double money) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(money).replace(",", ".") + " đ";
    }
     private int parseIntFromCell(String raw) {
        if (raw == null) return 0;
        String s = raw.trim().replaceAll("\\s+", ""); 
        if (s.isEmpty()) return 0;
        if (s.contains(".") && s.contains(",")) {
            s = s.replace(".", "");  
            s = s.replace(',', '.');  
        } else if (s.contains(",")) {
            s = s.replace(',', '.');
        } else {
            int firstDot = s.indexOf('.');
            int lastDot = s.lastIndexOf('.');
            if (firstDot != lastDot) {
                s = s.replace(".", "");
            }
        }
        double d;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            String fallback = s.replaceAll("[^0-9.\\-]", "");
            if (fallback.isEmpty()) return 0;
            d = Double.parseDouble(fallback);
        }
        return (int) Math.round(d);
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
