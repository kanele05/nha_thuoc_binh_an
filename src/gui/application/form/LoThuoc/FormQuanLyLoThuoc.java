/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.application.form.LoThuoc;

import com.formdev.flatlaf.FlatClientProperties;
import dao.LoThuocDAO;
import entities.LoThuoc;

import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;
public class FormQuanLyLoThuoc extends javax.swing.JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel model;
    private LoThuocDAO ltDao = new LoThuocDAO();
    // Formatter ngày tháng
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public FormQuanLyLoThuoc() throws SQLException {
        initComponents();
        init();
    }
    private void init() throws SQLException {
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow]"));

        // 1. Header
        add(createHeaderPanel(), "wrap 20");

        // 2. Toolbar (Tìm kiếm & Lọc hạn dùng)
        add(createToolBarPanel(), "wrap 10");

        // 3. Table (Danh sách lô)
        add(createTablePanel(), "grow");
        
        loadData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Quản Lý Lô Thuốc & Hạn Sử Dụng");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 10", "[]10[]push[][]", "[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");

        txtTimKiem = new JTextField();
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo mã lô, tên thuốc...");
        
        JButton btnTim = new JButton("Tìm kiếm");
        
        // Combo lọc trạng thái Date
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "✅ Còn hạn", "⚠️ Sắp hết hạn (< 3 tháng)", "⛔ Đã hết hạn"});
        cbTrangThai.addActionListener(e -> filterData()); // Giả lập sự kiện lọc

        JButton btnHuyLo = new JButton("Hủy lô hết hạn");
        btnHuyLo.putClientProperty(FlatClientProperties.STYLE, "background:#F44336; foreground:#fff; font:bold");
        btnHuyLo.addActionListener(e -> actionHuyLo());
        
        JButton btnSua = new JButton("Sửa thông tin");
        btnSua.addActionListener(e -> actionSua());

        panel.add(txtTimKiem, "w 250"); // Set width bằng MigLayout
        panel.add(btnTim);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cbTrangThai);
        
        panel.add(btnSua);
        panel.add(btnHuyLo);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = {"Mã Lô", "Tên Thuốc", "Ngày Nhập", "Hạn Sử Dụng", "Tồn Kho", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");

        // Áp dụng Renderer tô màu cho cột Hạn Sử Dụng (Cột 3) và Trạng Thái (Cột 5)
        ExpiryDateRenderer dateRenderer = new ExpiryDateRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(dateRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll);

        return panel;
    }
    
    // --- RENDERER TÔ MÀU (Logic quan trọng nhất) ---
    private class ExpiryDateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Lấy chuỗi Hạn sử dụng từ cột 3 (index 3)
            String hsdStr = table.getValueAt(row, 3).toString();
            
            try {
                LocalDate hsd = LocalDate.parse(hsdStr, dateFormatter);
                LocalDate homNay = LocalDate.now();
                
                long daysBetween = ChronoUnit.DAYS.between(homNay, hsd);
                
                if (daysBetween < 0) {
                    // Đã hết hạn -> Màu Đỏ
                    if (!isSelected) {
                        com.setForeground(new Color(211, 47, 47)); 
                        com.setFont(com.getFont().deriveFont(java.awt.Font.BOLD));
                    }
                } else if (daysBetween <= 90) { // Dưới 3 tháng (90 ngày)
                    // Sắp hết hạn -> Màu Cam
                    if (!isSelected) {
                        com.setForeground(new Color(230, 81, 0));
                    }
                } else {
                    // Còn hạn xa -> Màu Xanh lá (hoặc đen mặc định)
                    if (!isSelected) {
                        com.setForeground(new Color(56, 142, 60)); 
                    }
                }
                
            } catch (Exception e) {
                // Nếu lỗi parse ngày thì để mặc định
            }
            
            return com;
        }
    }

    private void loadData() throws SQLException {
        // Mock data với các trường hợp Date khác nhau
        // Giả sử hôm nay là ngày hiện tại, bạn hãy tự cộng trừ để thấy màu
        
        LocalDate now = LocalDate.now();
        String expired = now.minusDays(10).format(dateFormatter); // Hết hạn 10 ngày trước
        String near = now.plusDays(45).format(dateFormatter);     // Còn 45 ngày (Sắp hết)
        String good = now.plusYears(1).format(dateFormatter);     // Còn 1 năm (Tốt)
        
        model.setRowCount(0);

        ArrayList<LoThuoc> list = ltDao.getAllLoThuoc();
        for(LoThuoc lt : list ) {
        	String maLo  = lt.getMaLo();
        	String tenThuoc = lt.getThuoc().getTenThuoc();
        	String ngayNhap = lt.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        	String hanSuDung = lt.getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        	int tonKho = lt.getSoLuongTon();
        	
        	long checkHSD = ChronoUnit.DAYS.between(now, lt.getHanSuDung());
        	String trangThai;
        	if(checkHSD < 0) {
        		trangThai = "Đã hết hạn";
        	} else if(checkHSD <= 60) {
        		trangThai = "Sắp hết hạn";
        	}
        	else {
        		trangThai = "Còn hạn";
        	}
        	  model.addRow(new Object[] {
              		maLo,
              		tenThuoc,
              		ngayNhap,
              		hanSuDung,
              		tonKho,
              		trangThai
              });
        }
      
    }
    
    private void filterData() {
        // TODO: Thực tế bạn sẽ query lại DB với điều kiện WHERE
        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, 
                "Đang lọc: " + cbTrangThai.getSelectedItem());
    }

    private void actionHuyLo() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Chọn lô cần hủy!");
            return;
        }
        
        String trangThai = model.getValueAt(row, 5).toString();
        if (!trangThai.equals("Đã hết hạn")) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Lô này chưa hết hạn (" + trangThai + "). Bạn có chắc chắn muốn hủy không?", 
                    "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        } else {
             int confirm = JOptionPane.showConfirmDialog(this, 
                    "Xác nhận tiêu hủy lô thuốc đã hết hạn này?", 
                    "Tiêu hủy", JOptionPane.YES_NO_OPTION);
             if (confirm != JOptionPane.YES_OPTION) return;
        }

        // TODO: Cập nhật DB (Set Active = 0 hoặc Update SoLuong = 0)
        model.removeRow(row);
        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã hủy lô thuốc thành công!");
    }

    private void actionSua() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        // Demo sửa số lượng tồn (Kiểm kê kho phát hiện lệch)
        String currentStock = model.getValueAt(row, 4).toString();
        String newStock = JOptionPane.showInputDialog(this, "Điều chỉnh tồn kho thực tế:", currentStock);
        
        if (newStock != null && !newStock.isEmpty()) {
            try {
                Integer.parseInt(newStock); // Validate số
                model.setValueAt(newStock, row, 4);
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã cập nhật tồn kho!");
            } catch (NumberFormatException e) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Số lượng không hợp lệ!");
            }
        }
    }
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
