package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class FormNhanVien extends JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbLocVaiTro;
    private JTable table;
    private DefaultTableModel model;

    public FormNhanVien() {
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
        JLabel lbTitle = new JLabel("Quản Lý Nhân Viên");
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
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo tên, mã NV, SĐT...");
        
        JButton btnTim = new JButton("Tìm kiếm");
        
        cbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Quản lý", "Nhân viên bán hàng", "Thủ kho"});

        JButton btnThem = new JButton("Thêm mới");
        btnThem.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
        btnThem.addActionListener(e -> actionThem());

        JButton btnSua = new JButton("Sửa");
        btnSua.putClientProperty(FlatClientProperties.STYLE, "background:#2196F3; foreground:#fff; font:bold");
        btnSua.addActionListener(e -> actionSua());
        
        JButton btnXoa = new JButton("Xóa / Nghỉ việc");
        btnXoa.putClientProperty(FlatClientProperties.STYLE, "background:#F44336; foreground:#fff; font:bold");
        btnXoa.addActionListener(e -> actionXoa());
        
        JButton btnResetPass = new JButton("Reset mật khẩu");
        btnResetPass.setToolTipText("Đặt lại mật khẩu mặc định (123456)");
        btnResetPass.addActionListener(e -> actionResetPass());

        panel.add(txtTimKiem, "w 250");
        panel.add(btnTim);
        panel.add(new JLabel("Lọc:"));
        panel.add(cbLocVaiTro);
        
        panel.add(btnResetPass);
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = {"Mã NV", "Họ Tên", "Giới Tính", "Số ĐT", "Email", "Vai Trò", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        
        // Căn giữa cột Giới tính và Trạng thái
        table.getColumnModel().getColumn(2).setCellRenderer(new CenterAlignRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new CenterAlignRenderer());

        panel.add(new JScrollPane(table));
        return panel;
    }

    public void openThemMoi() {
        actionThem();
    }

    
    private class CenterAlignRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    }

    private void loadData() {
        // Mock data
        model.addRow(new Object[]{"NV001", "Nguyễn Quản Lý", "Nam", "0909111222", "admin@nhathuoc.com", "Quản lý", "Đang làm"});
        model.addRow(new Object[]{"NV002", "Trần Thị Thu Ngân", "Nữ", "0909333444", "thu_ngan@nhathuoc.com", "Nhân viên bán hàng", "Đang làm"});
        model.addRow(new Object[]{"NV003", "Lê Văn Kho", "Nam", "0909555666", "thu_kho@nhathuoc.com", "Thủ kho", "Đang làm"});
        model.addRow(new Object[]{"NV004", "Phạm Thị Nghỉ", "Nữ", "0909777888", "nghi@nhathuoc.com", "Nhân viên bán hàng", "Đã nghỉ"});
    }

    // --- Actions ---

    private void actionThem() {
        DialogNhanVien dialog = new DialogNhanVien(this, null);
        dialog.setVisible(true);
        if(dialog.isSave()){
            model.addRow(dialog.getData());
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm nhân viên mới thành công!");
        }
    }

    private void actionSua() {
        int row = table.getSelectedRow();
        if(row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng chọn nhân viên để sửa!");
            return;
        }
        
        // Lấy data dòng chọn
        Object[] currentData = new Object[model.getColumnCount()];
        for(int i=0; i<model.getColumnCount(); i++){
            currentData[i] = model.getValueAt(row, i);
        }
        
        DialogNhanVien dialog = new DialogNhanVien(this, currentData);
        dialog.setVisible(true);
        
        if(dialog.isSave()){
            Object[] newData = dialog.getData();
            for(int i=0; i<model.getColumnCount(); i++){
                model.setValueAt(newData[i], row, i);
            }
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Cập nhật thông tin thành công!");
        }
    }

    private void actionXoa() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        
        String tenNV = model.getValueAt(row, 1).toString();
        
        int opt = JOptionPane.showConfirmDialog(this, 
                "Bạn muốn cập nhật trạng thái nhân viên '" + tenNV + "' thành ĐÃ NGHỈ VIỆC?\n(Dữ liệu sẽ không bị xóa hoàn toàn để giữ lịch sử bán hàng)", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if(opt == JOptionPane.YES_OPTION){
            // Thay vì removeRow, ta chỉ update cột trạng thái
            model.setValueAt("Đã nghỉ", row, 6);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã cập nhật trạng thái!");
        }
    }
    
    private void actionResetPass() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        String tenNV = model.getValueAt(row, 1).toString();
        
        if(JOptionPane.showConfirmDialog(this, "Reset mật khẩu của '" + tenNV + "' về mặc định (123456)?", "Reset Password", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã reset mật khẩu thành công!");
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