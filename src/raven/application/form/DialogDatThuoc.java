package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class DialogDatThuoc extends JDialog {

    private final Component parent;
    private boolean isSave = false;

    // Output data getters
    private String tenKhach = "";
    private String sdt = "";
    private String gioHen = "";
    private String tongTienStr = "";

    // Components
    private JTextField txtSDT, txtTenKH, txtGioHen;
    private JTable tableItem;
    private DefaultTableModel modelItem;
    private JLabel lbTongTien;
    private boolean isUpdating = false;
    public DialogDatThuoc(Component parent) {
        super(SwingUtilities.windowForComponent(parent), "Tạo Phiếu Giữ Hàng", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap,fillx,insets 20, width 600", "[fill]", "[]15[]10[grow]10[]"));

        // Title
        JLabel lbTitle = new JLabel("KHÁCH GỌI ĐẶT THUỐC");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +6; foreground:$Accent.color");
        add(lbTitle, "center");

        // Info Panel
        add(createCustomerPanel());

        // Items Panel
        add(createItemsPanel(), "grow, h 200!");

        // Footer
        add(createFooterPanel());

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 10, fillx", "[][grow][][grow]", "[]10[]"));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin người gọi"));

        txtSDT = new JTextField();
        txtSDT.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập SĐT khách...");
        JButton btnTim = new JButton("🔍"); // Mock tìm khách cũ
        btnTim.addActionListener(e -> {
            txtTenKH.setText("Nguyễn Văn A (Khách Quen)"); // Demo
            Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Đã tìm thấy khách hàng!");
        });

        txtTenKH = new JTextField();
        
        // Mặc định giờ hẹn là 30p sau
        String timeDefault = LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm dd/MM"));
        txtGioHen = new JTextField(timeDefault);
        txtGioHen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: 10:30");

        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtSDT, "split 2");
        panel.add(btnTim);
        
        panel.add(new JLabel("Tên khách:"));
        panel.add(txtTenKH, "wrap");
        
        panel.add(new JLabel("Hẹn lấy lúc:"));
        panel.add(txtGioHen, "span 3");

        return panel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 5));
        
        JPanel toolbar = new JPanel(new MigLayout("insets 0", "push[]"));
        JButton btnThem = new JButton("Thêm thuốc (F2)");
        btnThem.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff");
        btnThem.addActionListener(e -> themThuocDemo());
        
        JButton btnXoa = new JButton("Xóa dòng");
        btnXoa.addActionListener(e -> {
            if(tableItem.getSelectedRow() != -1) {
                modelItem.removeRow(tableItem.getSelectedRow());
                tinhTongTien();
            }
        });
        
        toolbar.add(btnThem);
        toolbar.add(btnXoa);

        String[] cols = {"Tên thuốc", "ĐVT", "SL Giữ", "Đơn giá", "Thành tiền"};
        modelItem = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 2; } // Chỉ sửa số lượng
        };
        
        tableItem = new JTable(modelItem);
        tableItem.putClientProperty(FlatClientProperties.STYLE, "rowHeight:25; showHorizontalLines:true");
        modelItem.addTableModelListener(e -> {
            if (!isUpdating) {
                tinhTongTien();
            }
        });

        panel.add(new JScrollPane(tableItem), java.awt.BorderLayout.CENTER);
        panel.add(toolbar, java.awt.BorderLayout.NORTH);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow]push[]"));
        
        lbTongTien = new JLabel("Tổng cộng: 0 ₫");
        lbTongTien.putClientProperty(FlatClientProperties.STYLE, "font:bold +6; foreground:#D32F2F");
        
        JButton btnLuu = new JButton("LƯU & GIỮ HÀNG");
        btnLuu.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold +2; margin:10,20,10,20");
        btnLuu.addActionListener(e -> actionSave());

        panel.add(lbTongTien);
        panel.add(btnLuu);
        return panel;
    }

    private void themThuocDemo() {
        modelItem.addRow(new Object[]{"Panadol Extra", "Vỉ", 2, "15.000", "30.000"});
        modelItem.addRow(new Object[]{"Vitamin C 500mg", "Lọ", 1, "50.000", "50.000"});
        // Xóa dòng này đi vì addRow đã tự kích hoạt listener rồi
        // tinhTongTien(); 
    }

    private void tinhTongTien() {
        // Bật cờ để chặn Listener gọi lại hàm này
        isUpdating = true; 
        
        try {
            double total = 0;
            for(int i=0; i<modelItem.getRowCount(); i++) {
                try {
                    // Lấy số lượng và đơn giá
                    int sl = Integer.parseInt(modelItem.getValueAt(i, 2).toString());
                    double gia = Double.parseDouble(modelItem.getValueAt(i, 3).toString().replace(".", "").replace(",", ""));
                    
                    double thanhTien = sl * gia;
                    
                    // Cập nhật lại cột Thành tiền (Cột 4)
                    // Vì isUpdating = true nên dòng này sẽ KHÔNG kích hoạt lại Listener
                    modelItem.setValueAt(formatMoney(thanhTien).replace(" ₫",""), i, 4); 
                    
                    total += thanhTien;
                } catch(Exception e) {
                    // Bỏ qua lỗi parse nếu dòng dữ liệu chưa đủ
                }
            }
            lbTongTien.setText("Tổng cộng: " + formatMoney(total));
            tongTienStr = formatMoney(total);
            
        } finally {
            // Luôn tắt cờ dù có lỗi hay không để người dùng còn thao tác tiếp được
            isUpdating = false;
        }
    }
    
    private String formatMoney(double amount) {
        return new DecimalFormat("#,##0 ₫").format(amount);
    }

    private void actionSave() {
        if (txtTenKH.getText().isEmpty() || modelItem.getRowCount() == 0) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Thiếu thông tin khách hoặc chưa chọn thuốc!");
            return;
        }
        
        this.tenKhach = txtTenKH.getText();
        this.sdt = txtSDT.getText();
        this.gioHen = txtGioHen.getText();
        this.isSave = true;
        dispose();
    }

    // Getters for Parent Form
    public boolean isSave() { return isSave; }
    public String getTenKhach() { return tenKhach; }
    public String getSDT() { return sdt; }
    public String getGioHen() { return gioHen; }
    public String getTongTien() { return tongTienStr; }
}