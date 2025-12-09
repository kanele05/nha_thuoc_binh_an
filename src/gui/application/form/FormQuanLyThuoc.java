/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.application.form;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;
/**
 *
 * @author khang
 */
public class FormQuanLyThuoc extends javax.swing.JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbNhomThuoc;
    private JTable table;
    private DefaultTableModel model;
    
    // Buttons
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnXuatExcel;
    public FormQuanLyThuoc() {
        initComponents();
        init();
    }
    private void init() {
        // Layout chính: Wrap, fill chiều ngang, insets (lề) 20px
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow]"));
        
        // 1. Header (Tiêu đề)
        add(createHeaderPanel(), "wrap 20");
        
        // 2. Toolbar (Tìm kiếm, Lọc, Các nút chức năng)
        add(createToolBarPanel(), "wrap 10");
        
        // 3. Table (Danh sách)
        add(createTablePanel(), "grow");
        
        // Load dữ liệu mẫu
        loadData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        
        JLabel lbTitle = new JLabel("Danh Sách Thuốc");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 10", "[]10[]push[][]", "[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");

        // --- Phần tìm kiếm ---
        txtTimKiem = new JTextField();
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo tên, mã thuốc, hoạt chất...");
        // Icon tìm kiếm (dùng svg hoặc unicode tạm)
        // txtTimKiem.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon(...));
        
        JButton btnTim = new JButton("Tìm kiếm");
        
        // --- Phần lọc ---
        cbNhomThuoc = new JComboBox<>(new String[]{"Tất cả nhóm", "Kháng sinh", "Giảm đau", "Vitamin", "Thực phẩm chức năng"});

        // --- Phần nút chức năng ---
        btnThem = createButton("Thêm mới", "#4CAF50"); // Xanh lá
        btnSua = createButton("Sửa", "#2196F3");       // Xanh dương
        btnXoa = createButton("Xóa", "#F44336");       // Đỏ
        btnXuatExcel = createButton("Xuất Excel", "#009688"); // Teal

        // Thêm action
        btnThem.addActionListener(e -> actionThem());
        btnSua.addActionListener(e -> actionSua());
        btnXoa.addActionListener(e -> actionXoa());

        panel.add(txtTimKiem, "w 250");
        panel.add(btnTim);
        panel.add(new JLabel("Lọc:"));
        panel.add(cbNhomThuoc);
        
        // Add buttons to the right (push handles the gap)
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnXuatExcel);

        return panel;
    }

    private JButton createButton(String text, String colorHex) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:" + colorHex + ";"
                + "foreground:#FFFFFF;"
                + "font:bold;"
                + "borderWidth:0;"
                + "focusWidth:0");
        return btn;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Cấu hình Table
        String[] columns = {"Mã Thuốc", "Tên Thuốc", "Nhóm", "Hoạt Chất", "ĐVT", "Giá Nhập", "Giá Bán", "Tồn Kho"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };

        table = new JTable(model);
        
        // Style cho Table
        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:30;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;");
//                + "selectionBackground:darken(@background, 10%)");
        
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:35;"
                + "font:bold;"
                + "separatorColor:transparent");

        // Custom renderer để căn lề số liệu (Giá, Tồn kho)
        table.getColumnModel().getColumn(5).setCellRenderer(new RightAlignRenderer()); // Giá nhập
        table.getColumnModel().getColumn(6).setCellRenderer(new RightAlignRenderer()); // Giá bán
        table.getColumnModel().getColumn(7).setCellRenderer(new RightAlignRenderer()); // Tồn kho

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public void openThemMoi() {
        actionThem();
    }

    // Renderer căn phải cho cột số
    private class RightAlignRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }

    private void loadData() {
        // Dữ liệu giả lập
        model.addRow(new Object[]{"T001", "Paracetamol 500mg", "Giảm đau", "Paracetamol", "Vỉ", "3.000", "5.000", "500"});
        model.addRow(new Object[]{"T002", "Amoxicillin 500mg", "Kháng sinh", "Amoxicillin", "Hộp", "45.000", "55.000", "120"});
        model.addRow(new Object[]{"T003", "Vitamin C 1000mg", "Vitamin", "Acid Ascorbic", "Lọ", "20.000", "28.000", "85"});
        model.addRow(new Object[]{"T004", "Panadol Extra", "Giảm đau", "Paracetamol, Caffeine", "Vỉ", "12.000", "15.000", "200"});
        model.addRow(new Object[]{"T005", "Berberin", "Tiêu hóa", "Berberin", "Lọ", "8.000", "12.000", "300"});
    }

    // --- Actions ---

    private void actionThem() {
        // Tạo dialog, tham số thứ 2 là null vì là thêm mới
        DialogThuoc dialog = new DialogThuoc(this, null);
        dialog.setVisible(true); // Dialog sẽ dừng code tại đây cho đến khi đóng

        if (dialog.isSave()) {
            // Nếu người dùng bấm Lưu, lấy dữ liệu và thêm vào bảng
            model.addRow(dialog.getData());
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thêm thuốc mới thành công!");
            
            // Scroll xuống dòng cuối
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        }
    }

    private void actionSua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng chọn thuốc để sửa!");
            return;
        }

        // Lấy dữ liệu dòng hiện tại để truyền vào Dialog
        Object[] oldData = new Object[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            oldData[i] = model.getValueAt(row, i);
        }

        DialogThuoc dialog = new DialogThuoc(this, oldData);
        dialog.setVisible(true);

        if (dialog.isSave()) {
            // Nếu bấm Lưu, cập nhật lại dòng hiện tại
            Object[] newData = dialog.getData();
            for (int i = 0; i < model.getColumnCount(); i++) {
                model.setValueAt(newData[i], row, i);
            }
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Cập nhật thành công!");
        }
    }

    private void actionXoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng chọn thuốc để xóa!");
            return;
        }

        String tenThuoc = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa thuốc \"" + tenThuoc + "\" không?\nHành động này không thể hoàn tác.", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã xóa thuốc thành công!");
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
