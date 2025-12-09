/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;
public class FormNhapHang extends javax.swing.JPanel {

    private JComboBox<String> cbNhaCungCap;
    private JTextField txtNguoiNhap;
    private JTextField txtGhiChu;
    private JLabel lbTongTien;
    private JTable table;
    private DefaultTableModel model;
    
    // Biến cờ chặn loop sự kiện khi tính toán
    private boolean isUpdating = false;
    public FormNhapHang() {
        initComponents();
        init();
    }
    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow][]"));

        // 1. Header
        add(createHeaderPanel(), "wrap 20");

        // 2. Info & Actions Panel (Chọn NCC, Nút Import)
        add(createActionPanel(), "wrap 10");

        // 3. Table Panel (Danh sách hàng chờ nhập)
        add(createTablePanel(), "grow");

        // 4. Footer Panel (Tổng tiền & Lưu)
        add(createFooterPanel(), "growx");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Nhập Hàng & Quản Lý Lô");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 15", "[][grow]push[][]", "[]10[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");

        // Hàng 1: Thông tin phiếu
        cbNhaCungCap = new JComboBox<>(new String[]{"Công ty Dược Hậu Giang", "Sanofi Việt Nam", "Zuellig Pharma", "Nhà cung cấp khác..."});
        txtNguoiNhap = new JTextField("Admin"); // Thực tế lấy từ user đang login
        txtNguoiNhap.setEditable(false);
        
        panel.add(new JLabel("Nhà cung cấp:"));
        panel.add(cbNhaCungCap, "w 300, wrap");
        
        panel.add(new JLabel("Người nhập:"));
        panel.add(txtNguoiNhap, "w 300");

        // Nút chức năng bên phải
        JButton btnMau = new JButton("Tải file mẫu");
        btnMau.addActionListener(e -> actionDownloadTemplate());
        JButton btnImport = new JButton("Nhập từ Excel");
        btnImport.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#009688;"
                + "foreground:#ffffff;"
                + "font:bold;"
                + "iconTextGap:10");
        // Giả lập icon Excel (bạn có thể thêm icon thật sau)
        // btnImport.setIcon(new FlatSVGIcon("raven/icon/svg/excel.svg")); 
        btnImport.addActionListener(e -> actionImportExcel());

        panel.add(btnMau, "cell 2 0, span 1 2, right"); // Đặt vị trí bên phải
        panel.add(btnImport, "cell 3 0, span 1 2");

        return panel;
    }
    // Hàm tạo file mẫu CSV (Excel mở được)
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
            
            // Thêm BOM để Excel nhận diện đúng tiếng Việt (UTF-8)
            writer.write("\ufeff"); 
            
            // Header (Tiêu đề cột)
            writer.write("Mã thuốc,Tên thuốc,Đơn vị tính,Lô sản xuất,Hạn sử dụng,Số lượng,Giá nhập");
            writer.newLine();
            
            // Dữ liệu mẫu dòng 1
            writer.write("T001,Paracetamol 500mg,Vỉ,A123,12/2025,100,3500");
            writer.newLine();
            
            // Dữ liệu mẫu dòng 2
            writer.write("T002,Amoxicillin 500mg,Hộp,B456,06/2026,50,45000");
            writer.newLine();
            
            // Dữ liệu mẫu dòng 3
            writer.write("T003,Vitamin C 1000mg,Lọ,C789,01/2025,200,25000");
            writer.newLine();
            
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã lưu file mẫu thành công!");
            
            // Mở file ngay sau khi lưu (Chỉ hoạt động trên Desktop hỗ trợ)
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception e) {
                // Bỏ qua nếu không mở được
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

        // Các cột quan trọng: LÔ và HẠN SỬ DỤNG
        String[] columns = {
            "Mã thuốc", "Tên thuốc", "ĐVT", 
            "Lô SX", "Hạn SD", 
            "Số lượng", "Giá nhập", "Thành tiền"
        };
        
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Cho phép sửa: Lô(3), HSD(4), SL(5), Giá(6)
                return column == 3 || column == 4 || column == 5 || column == 6; 
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true; gridColor:#e0e0e0");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        
        // Căn phải cho các cột số
        table.getColumnModel().getColumn(5).setCellRenderer(new RightAlignRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new RightAlignRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new RightAlignRenderer());

        // Sự kiện tính tiền khi sửa SL hoặc Giá
        model.addTableModelListener(e -> {
            if (!isUpdating) tinhTienHang();
        });

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
        
        panel.add(btnLuu, "span 3, growx, h 50!"); // Nút to nằm dưới cùng

        return panel;
    }
    
    // Renderer căn phải
    private class RightAlignRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }

    // --- LOGIC XỬ LÝ ---

    private void actionImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel nhập hàng");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            readExcelSimulated(file); // Gọi hàm đọc file
        }
    }

    /**
     * MÔ PHỎNG ĐỌC FILE EXCEL
     * Trong thực tế, bạn sẽ dùng Apache POI ở đây.
     * Vì chưa có thư viện, mình sẽ giả lập việc đọc dữ liệu và đổ vào bảng.
     */
    private void readExcelSimulated(File file) {
        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Đang đọc file: " + file.getName());
        
        // Giả lập delay đọc file
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Giả vờ đang load
                
                SwingUtilities.invokeLater(() -> {
                    // Xóa dữ liệu cũ
                    model.setRowCount(0);
                    isUpdating = true; // Chặn sự kiện tính tiền để load cho nhanh
                    
                    // Dữ liệu giả lập (như thể đọc từ Excel ra)
                    // Cấu trúc: Mã, Tên, ĐVT, Lô, HSD, SL, Giá
                    Object[][] mockData = {
                        {"T005", "Panadol Extra", "Vỉ", "A101", "12/2025", "100", "12000"},
                        {"T008", "Berberin", "Lọ", "B202", "06/2026", "50", "8500"},
                        {"T012", "Vitamin C 500mg", "Hộp", "C303", "01/2025", "200", "25000"},
                        {"T025", "Khẩu trang Y tế", "Hộp", "KT01", "12/2028", "50", "35000"}
                    };

                    for (Object[] row : mockData) {
                        double sl = Double.parseDouble(row[5].toString());
                        double gia = Double.parseDouble(row[6].toString());
                        double thanhTien = sl * gia;
                        
                        model.addRow(new Object[]{
                            row[0], row[1], row[2], row[3], row[4], 
                            row[5], // SL
                            formatMoney(gia), // Giá
                            formatMoney(thanhTien) // Thành tiền
                        });
                    }
                    
                    isUpdating = false; // Mở lại cờ
                    tinhTienHang(); // Tính tổng lại
                    
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã import dữ liệu thành công!");
                });
                
            } catch (InterruptedException e) {}
        }).start();
    }

    private void tinhTienHang() {
        isUpdating = true;
        double tongCong = 0;
        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    // Lấy SL và Giá từ bảng (xử lý chuỗi tiền tệ)
                    String slStr = model.getValueAt(i, 5).toString();
                    String giaStr = model.getValueAt(i, 6).toString().replace(".", "").replace(",", "").replace(" ₫", "");
                    
                    double sl = Double.parseDouble(slStr);
                    double gia = Double.parseDouble(giaStr);
                    double thanhTien = sl * gia;
                    
                    // Cập nhật lại cột Thành tiền
                    model.setValueAt(formatMoney(thanhTien), i, 7);
                    
                    tongCong += thanhTien;
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi
                }
            }
            lbTongTien.setText(formatMoney(tongCong));
        } finally {
            isUpdating = false;
        }
    }

    private void actionLuuPhieu() {
        if (model.getRowCount() == 0) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Danh sách nhập đang trống!");
            return;
        }
        
        // Check dữ liệu lô và hạn sử dụng
        for(int i=0; i<model.getRowCount(); i++){
            String lo = model.getValueAt(i, 3).toString();
            String hsd = model.getValueAt(i, 4).toString();
            if(lo.trim().isEmpty() || hsd.trim().isEmpty()){
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 
                        "Dòng " + (i+1) + " thiếu Lô SX hoặc Hạn SD!");
                table.setRowSelectionInterval(i, i);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận nhập kho với tổng tiền: " + lbTongTien.getText() + "?\nHệ thống sẽ cập nhật tồn kho theo từng Lô.", 
                "Lưu Phiếu Nhập", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Lưu vào Database (Bảng PhieuNhap, PhieuNhapChiTiet, LoThuoc)
            
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Nhập kho thành công!");
            model.setRowCount(0);
            lbTongTien.setText("0 ₫");
            txtGhiChu.setText("");
        }
    }
    
    private String formatMoney(double money) {
        return new DecimalFormat("#,##0 ₫").format(money);
    }
    
    // --- Phần này để bạn tham khảo cách dùng Apache POI thật ---
    /*
    private void readExcelReal(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
             
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header
                // Đọc cell...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
