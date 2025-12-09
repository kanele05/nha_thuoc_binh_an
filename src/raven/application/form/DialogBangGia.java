package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class DialogBangGia extends JDialog {

    private final Component parent;
    private boolean isSave = false;
    private final boolean isEdit;

    private JTextField txtMaBG, txtTenBG, txtNgayHL;
    private JTable table;
    private DefaultTableModel model;

    public DialogBangGia(Component parent, Object[] data) {
        super(SwingUtilities.windowForComponent(parent), "Chi Tiết Bảng Giá", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.isEdit = (data != null);
        initComponents();
        if(isEdit) {
            txtMaBG.setText(data[0].toString());
            txtTenBG.setText(data[1].toString());
            txtNgayHL.setText(data[2].toString());
            loadMockDetails();
        } else {
            loadEmptyList();
        }
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap,fillx,insets 20, width 700", "[fill]", "[]15[]10[grow]10[]"));
        
        JLabel lbTitle = new JLabel(isEdit ? "CẬP NHẬT BẢNG GIÁ" : "TẠO BẢNG GIÁ MỚI");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +4; foreground:$Accent.color");
        add(lbTitle, "center");

        // Info Panel
        JPanel pInfo = new JPanel(new MigLayout("insets 0, fillx", "[][grow][][grow]", "[]"));
        txtMaBG = new JTextField(isEdit ? "" : "AUTO");
        txtMaBG.setEditable(false);
        txtTenBG = new JTextField();
        txtTenBG.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: Giá khuyến mãi 20/11");
        txtNgayHL = new JTextField();
        txtNgayHL.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");

        pInfo.add(new JLabel("Mã BG:"));
        pInfo.add(txtMaBG);
        pInfo.add(new JLabel("Tên Bảng Giá:"));
        pInfo.add(txtTenBG, "wrap");
        pInfo.add(new JLabel("Hiệu lực từ:"));
        pInfo.add(txtNgayHL);
        
        add(pInfo);

        // Table Panel (Danh sách thuốc và giá)
        add(createTablePanel(), "grow, h 300!");

        // Footer
        JPanel pFooter = new JPanel(new MigLayout("insets 0", "push[][]"));
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        JButton btnLuu = new JButton("Lưu lại");
        btnLuu.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
        btnLuu.addActionListener(e -> actionSave());
        
        pFooter.add(btnHuy);
        pFooter.add(btnLuu);
        add(pFooter);

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 5));
        
        // Toolbar nhỏ để thao tác nhanh
        JPanel tool = new JPanel(new MigLayout("insets 0", "[]push[]"));
        tool.add(new JLabel("Danh sách thuốc & Giá áp dụng:"));
        
        JButton btnTangGia = new JButton("Tăng tất cả 10%");
        btnTangGia.addActionListener(e -> actionTangGia());
        tool.add(btnTangGia);

        String[] cols = {"Mã Thuốc", "Tên Thuốc", "ĐVT", "Giá Nhập (Tham khảo)", "Giá Bán Cũ", "GIÁ MỚI (Sửa)"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5; } // Chỉ sửa cột Giá Mới
        };
        
        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:25; showHorizontalLines:true");
        
        panel.add(tool, java.awt.BorderLayout.NORTH);
        panel.add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
        return panel;
    }
    
    private void loadEmptyList() {
        // Load danh sách thuốc từ kho lên để thiết lập giá
        model.addRow(new Object[]{"T001", "Paracetamol", "Vỉ", "3.000", "5.000", "5000"});
        model.addRow(new Object[]{"T002", "Vitamin C", "Lọ", "20.000", "28.000", "28000"});
        model.addRow(new Object[]{"T003", "Khẩu trang", "Hộp", "25.000", "35.000", "35000"});
    }
    
    private void loadMockDetails() {
         // Load chi tiết của bảng giá đang sửa
        model.addRow(new Object[]{"T001", "Paracetamol", "Vỉ", "3.000", "5.000", "4500"}); // Giá mới khác
        model.addRow(new Object[]{"T002", "Vitamin C", "Lọ", "20.000", "28.000", "25000"});
    }

    private void actionTangGia() {
        // Demo chức năng tính toán hàng loạt
        for(int i=0; i<model.getRowCount(); i++) {
            try {
                double giaNhap = Double.parseDouble(model.getValueAt(i, 3).toString().replace(".", ""));
                double giaMoi = giaNhap * 1.1; // Lời 10% so với giá nhập
                model.setValueAt(new DecimalFormat("#").format(giaMoi), i, 5);
            } catch(Exception e){}
        }
    }

    private void actionSave() {
        if(txtTenBG.getText().isEmpty()){
             Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Nhập tên bảng giá!");
             return;
        }
        isSave = true;
        dispose();
    }

    public boolean isSave() { return isSave; }
    public String getTenBangGia() { return txtTenBG.getText(); }
    public String getNgayHieuLuc() { return txtNgayHL.getText(); }
}