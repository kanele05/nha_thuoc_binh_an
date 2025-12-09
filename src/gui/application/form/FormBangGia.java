package gui.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class FormBangGia extends JPanel {

    private JTextField txtTimKiem;
    private JTable table;
    private DefaultTableModel model;

    public FormBangGia() {
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
        JLabel lbTitle = new JLabel("Quản Lý Bảng Giá Bán");
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
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm tên bảng giá...");
        
        JButton btnTim = new JButton("Tìm kiếm");
        
        JButton btnThem = new JButton("Tạo bảng giá mới");
        btnThem.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
        btnThem.addActionListener(e -> actionTaoMoi());

        JButton btnApDung = new JButton("Áp dụng bảng giá này");
        btnApDung.putClientProperty(FlatClientProperties.STYLE, "background:#2196F3; foreground:#fff; font:bold");
        btnApDung.addActionListener(e -> actionApDung());
        
        JButton btnSua = new JButton("Chi tiết / Sửa");
        btnSua.addActionListener(e -> actionSua());

        panel.add(txtTimKiem, "w 250");
        panel.add(btnTim);
        
        panel.add(btnApDung);
        panel.add(btnSua);
        panel.add(btnThem);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = {"Mã Bảng Giá", "Tên Bảng Giá", "Hiệu Lực Từ", "Hiệu Lực Đến", "Ghi Chú", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        
        // Render trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        panel.add(new JScrollPane(table));
        return panel;
    }

    public void openThemMoi() {
        actionTaoMoi();
    }

    // --- RENDERER ---
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";
            
            if (status.equals("Đang áp dụng")) {
                com.setForeground(new Color(56, 142, 60)); // Xanh lá
                com.setFont(com.getFont().deriveFont(java.awt.Font.BOLD));
            } else {
                com.setForeground(Color.GRAY);
                com.setFont(com.getFont().deriveFont(java.awt.Font.PLAIN));
            }
            
            if (isSelected) com.setForeground(Color.WHITE);
            return com;
        }
    }

    // --- LOGIC ---

    private void loadData() {
        model.addRow(new Object[]{"BG01", "Giá Bán Lẻ Mặc Định", "01/01/2023", "---", "Giá chuẩn", "Đang áp dụng"});
        model.addRow(new Object[]{"BG02", "Giá Bán Sỉ (Đại lý)", "01/01/2023", "---", "Áp dụng cho khách sỉ", "Ngừng áp dụng"});
        model.addRow(new Object[]{"BG03", "Khuyến Mãi Tết 2024", "01/02/2024", "15/02/2024", "Giảm 10% nhóm Vitamin", "Chưa hiệu lực"});
    }

    private void actionTaoMoi() {
        DialogBangGia dialog = new DialogBangGia(this, null);
        dialog.setVisible(true);
        if(dialog.isSave()){
            model.addRow(new Object[]{
                "BG" + (model.getRowCount() + 1),
                dialog.getTenBangGia(),
                dialog.getNgayHieuLuc(),
                "---",
                "",
                "Chưa hiệu lực"
            });
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Tạo bảng giá mới thành công!");
        }
    }
    
    private void actionSua() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        
        // Mock data truyền vào dialog
        Object[] data = {
            model.getValueAt(row, 0),
            model.getValueAt(row, 1),
            model.getValueAt(row, 2)
        };
        
        DialogBangGia dialog = new DialogBangGia(this, data);
        dialog.setVisible(true);
    }
    
    private void actionApDung() {
        int row = table.getSelectedRow();
        if (row == -1) {
             Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Chọn bảng giá muốn áp dụng!");
             return;
        }
        
        String tenBG = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Hệ thống sẽ cập nhật giá bán của toàn bộ thuốc theo bảng giá:\n" + tenBG + "\nTiếp tục?", 
                "Xác nhận thay đổi giá", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Update logic: Set row selected to "Đang áp dụng", others to "Ngừng áp dụng"
            for(int i=0; i<model.getRowCount(); i++) {
                model.setValueAt("Ngừng áp dụng", i, 5);
            }
            model.setValueAt("Đang áp dụng", row, 5);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã áp dụng bảng giá mới!");
        }
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
}