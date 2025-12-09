package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

/**
 * Form popup để Thêm mới hoặc Cập nhật thuốc
 */
public class DialogThuoc extends JDialog {

    private final Component parent;
    private final boolean isEdit;
    private boolean isSave = false; // Biến kiểm tra xem người dùng có bấm Lưu không

    // Input components
    private JTextField txtMaThuoc;
    private JTextField txtTenThuoc;
    private JComboBox<String> cbNhomThuoc;
    private JTextField txtHoatChat;
    private JComboBox<String> cbDVT;
    private JSpinner spinGiaNhap;
    private JSpinner spinGiaBan;
    private JSpinner spinTonKho;
    private JTextArea txtGhiChu;
    
    // Data (dùng khi edit)
    private Object[] currentData;

    public DialogThuoc(Component parent, Object[] data) {
        super(SwingUtilities.windowForComponent(parent), "Thuốc", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.currentData = data;
        this.isEdit = (data != null);
        
        initComponents();
        if (isEdit) {
            fillData();
        }
    }

    private void initComponents() {
        // Cấu hình Dialog
        setTitle(isEdit ? "Cập Nhật Thông Tin Thuốc" : "Thêm Thuốc Mới");
        setLayout(new MigLayout("wrap,fillx,insets 20, width 500", "[label, 100]10[grow,fill]", "[]15[]"));
        
        // --- Header ---
        JLabel lbTitle = new JLabel(isEdit ? "SỬA THÔNG TIN" : "THÊM THUỐC MỚI");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +4; foreground:$Accent.color");
        add(lbTitle, "span 2, center, wrap 20");

        // --- Các trường nhập liệu ---
        
        // 1. Mã thuốc & Tên thuốc
        add(new JLabel("Mã thuốc:"));
        txtMaThuoc = new JTextField();
        txtMaThuoc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tự động tạo nếu để trống");
        if(isEdit) txtMaThuoc.setEditable(false); // Không sửa mã khi edit
        add(txtMaThuoc);

        add(new JLabel("Tên thuốc:"));
        txtTenThuoc = new JTextField();
        txtTenThuoc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: Panadol Extra");
        add(txtTenThuoc);

        // 2. Nhóm & Hoạt chất
        add(new JLabel("Nhóm thuốc:"));
        cbNhomThuoc = new JComboBox<>(new String[]{"Kháng sinh", "Giảm đau", "Vitamin", "Thực phẩm chức năng", "Dạ dày", "Tim mạch"});
        add(cbNhomThuoc);

        add(new JLabel("Hoạt chất:"));
        txtHoatChat = new JTextField();
        txtHoatChat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: Paracetamol");
        add(txtHoatChat);

        // 3. Đơn vị tính & Tồn kho
        add(new JLabel("Đơn vị tính:"));
        cbDVT = new JComboBox<>(new String[]{"Viên", "Vỉ", "Hộp", "Lọ", "Tuýp", "Gói"});
        add(cbDVT, "split 3, width 30%"); // Chia dòng này làm 3 phần
        
        add(new JLabel("Tồn đầu:"), "gapleft 10");
        spinTonKho = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        add(spinTonKho, "grow");

        // 4. Giá nhập & Giá bán (Dùng JSpinner cho số)
        add(new JLabel("Giá nhập:"));
        spinGiaNhap = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000000.0, 1000.0));
        spinGiaNhap.setEditor(new JSpinner.NumberEditor(spinGiaNhap, "#,##0 ₫"));
        add(spinGiaNhap);

        add(new JLabel("Giá bán:"));
        spinGiaBan = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000000.0, 1000.0));
        spinGiaBan.setEditor(new JSpinner.NumberEditor(spinGiaBan, "#,##0 ₫"));
        add(spinGiaBan);

        // 5. Ghi chú
        add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextArea(3, 0);
        txtGhiChu.putClientProperty(FlatClientProperties.STYLE, "font:90%");
        JScrollPane scrollNote = new JScrollPane(txtGhiChu);
        add(scrollNote);

        // --- Buttons Actions ---
        JPanel panelActions = new JPanel(new MigLayout("insets 20 0 0 0", "push[][]"));
        panelActions.setOpaque(false);

        JButton btnHuy = new JButton("Hủy bỏ");
        btnHuy.addActionListener(e -> closeDialog());

        JButton btnLuu = new JButton("Lưu thông tin");
        btnLuu.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#4CAF50;"
                + "foreground:#ffffff;"
                + "font:bold");
        btnLuu.addActionListener(e -> actionSave());

        panelActions.add(btnHuy);
        panelActions.add(btnLuu);
        add(panelActions, "span 2, growx");

        // Settings Dialog
        pack();
        setLocationRelativeTo(parent);
    }

    private void fillData() {
        // Thứ tự cột trong Table: 
        // 0:Mã, 1:Tên, 2:Nhóm, 3:Hoạt chất, 4:ĐVT, 5:Giá Nhập, 6:Giá Bán, 7:Tồn
        txtMaThuoc.setText(currentData[0].toString());
        txtTenThuoc.setText(currentData[1].toString());
        cbNhomThuoc.setSelectedItem(currentData[2].toString());
        txtHoatChat.setText(currentData[3].toString());
        cbDVT.setSelectedItem(currentData[4].toString());
        
        // Parse giá tiền từ chuỗi (vd: "5.000") về số để đưa vào Spinner
        try {
            spinGiaNhap.setValue(parseCurrency(currentData[5].toString()));
            spinGiaBan.setValue(parseCurrency(currentData[6].toString()));
            spinTonKho.setValue(parseCurrency(currentData[7].toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double parseCurrency(String text) {
        // Loại bỏ dấu chấm và chữ ₫ để parse về double
        return Double.parseDouble(text.replace(".", "").replace(",", "").trim());
    }

    private void actionSave() {
        // Validate đơn giản
        if (txtTenThuoc.getText().trim().isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Tên thuốc không được để trống!");
            txtTenThuoc.requestFocus();
            return;
        }

        // TODO: Ở đây bạn sẽ viết code lưu vào database
        
        isSave = true;
        closeDialog();
    }

    private void closeDialog() {
        dispose();
    }

    // Hàm public để FormQuanLyThuoc gọi lấy dữ liệu sau khi dialog đóng
    public boolean isSave() {
        return isSave;
    }

    public Object[] getData() {
        DecimalFormat df = new DecimalFormat("#,##0");
        return new Object[]{
            txtMaThuoc.getText().isEmpty() ? "AUTO" : txtMaThuoc.getText(),
            txtTenThuoc.getText(),
            cbNhomThuoc.getSelectedItem(),
            txtHoatChat.getText(),
            cbDVT.getSelectedItem(),
            df.format(spinGiaNhap.getValue()),
            df.format(spinGiaBan.getValue()),
            spinTonKho.getValue()
        };
    }
}