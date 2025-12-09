/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Color;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;
public class FormDatThuoc extends javax.swing.JPanel {

   private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel model;
    public FormDatThuoc() {
        initComponents();
        init();
    }
    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow]"));
        
        // 1. Header
        add(createHeaderPanel(), "wrap 20");
        
        // 2. Toolbar
        add(createToolBarPanel(), "wrap 10");
        
        // 3. Table
        add(createTablePanel(), "grow");
        
        loadData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Đơn Khách Đặt Giữ (Qua điện thoại)");
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
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm SĐT hoặc tên khách...");
        
        
        JButton btnTim = new JButton("Tìm kiếm");
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang giữ hàng", "Đã lấy hàng", "Đã hủy"});

        JButton btnTaoPhieu = new JButton("Tạo phiếu giữ");
        btnTaoPhieu.putClientProperty(FlatClientProperties.STYLE, "background:#2196F3; foreground:#fff; font:bold");
        btnTaoPhieu.addActionListener(e -> actionTaoPhieu());
        
        JButton btnKhachDen = new JButton("Khách đến lấy hàng");
        btnKhachDen.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
        btnKhachDen.addActionListener(e -> actionKhachDenLay());

        panel.add(txtTimKiem, "w 250");
        panel.add(btnTim);
        panel.add(new JLabel("Lọc trạng thái:"));
        panel.add(cbTrangThai);
        panel.add(btnTaoPhieu);
        panel.add(btnKhachDen);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Cột "Giờ hẹn" rất quan trọng để biết khách sắp đến chưa
        String[] columns = {"Mã Phiếu", "Khách Hàng", "SĐT", "Giờ Hẹn Lấy", "Tổng Tiền", "Ghi Chú", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        
        // Tô màu trạng thái
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());

        panel.add(new JScrollPane(table));
        return panel;
    }

    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";
            if (status.equals("Đang giữ hàng")) {
                setForeground(new Color(255, 152, 0)); // Màu cam (Cảnh báo đang giữ kho)
                setFont(getFont().deriveFont(java.awt.Font.BOLD));
            } else if (status.equals("Đã lấy hàng")) {
                setForeground(new Color(76, 175, 80)); // Xanh lá
            } else {
                setForeground(Color.GRAY);
            }
            if (isSelected) setForeground(Color.WHITE);
            return com;
        }
    }

    private void loadData() {
        // Mock data
        model.addRow(new Object[]{"G001", "Chị Lan", "0909123456", "10:30 Hôm nay", "150.000", "Lấy gấp", "Đang giữ hàng"});
        model.addRow(new Object[]{"G002", "Anh Nam", "0912333444", "14:00 Hôm nay", "520.000", "", "Đang giữ hàng"});
        model.addRow(new Object[]{"G003", "Cô Ba", "0988777666", "09:00 Hôm qua", "80.000", "Khách quên", "Đã hủy"});
    }

    private void actionTaoPhieu() {
        DialogDatThuoc dialog = new DialogDatThuoc(this);
        dialog.setVisible(true);
        if (dialog.isSave()) {
            model.addRow(new Object[]{
                "G" + (model.getRowCount() + 1),
                dialog.getTenKhach(),
                dialog.getSDT(),
                dialog.getGioHen(),
                dialog.getTongTien(),
                "Vừa tạo",
                "Đang giữ hàng"
            });
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã tạo phiếu và trừ tồn kho tạm thời!");
        }
    }

    private void actionKhachDenLay() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Chọn phiếu giữ hàng khách muốn lấy!");
            return;
        }
        
        String status = model.getValueAt(row, 6).toString();
        if (!status.equals("Đang giữ hàng")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Phiếu này đã hoàn tất hoặc đã hủy!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận khách đã đến và thanh toán?", "Hoàn tất đơn hàng", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            model.setValueAt("Đã lấy hàng", row, 6);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã xuất hóa đơn và thu tiền!");
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
