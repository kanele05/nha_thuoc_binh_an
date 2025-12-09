package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class DialogNhanVien extends JDialog {

    private final Component parent;
    private boolean isSave = false;
    private final boolean isEdit;

    // Components
    private JTextField txtMaNV, txtHoTen, txtSDT, txtEmail, txtNgaySinh;
    private JComboBox<String> cbGioiTinh, cbVaiTro, cbTrangThai;
    private JTextArea txtDiaChi;

    public DialogNhanVien(Component parent, Object[] data) {
        super(SwingUtilities.windowForComponent(parent), "Thông Tin Nhân Viên", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.isEdit = (data != null);
        initComponents();
        if(isEdit) fillData(data);
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap,fillx,insets 20, width 500", "[label, 100]10[grow,fill]", "[]15[]"));
        
        JLabel lbTitle = new JLabel(isEdit ? "SỬA THÔNG TIN NHÂN VIÊN" : "THÊM NHÂN VIÊN MỚI");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +4; foreground:$Accent.color");
        add(lbTitle, "span 2, center, wrap 20");

        // Mã NV (Auto)
        txtMaNV = new JTextField(isEdit ? "" : "AUTO");
        txtMaNV.setEditable(false);
        add(new JLabel("Mã NV:"));
        add(txtMaNV);

        // Họ tên
        txtHoTen = new JTextField();
        txtHoTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập họ và tên");
        add(new JLabel("Họ tên:"));
        add(txtHoTen);

        // Giới tính & Ngày sinh
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        txtNgaySinh = new JTextField();
        txtNgaySinh.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        
        add(new JLabel("Giới tính:"));
        add(cbGioiTinh, "split 3, w 100!");
        add(new JLabel("Ngày sinh:"), "gapleft 10");
        add(txtNgaySinh, "grow");

        // SĐT & Email
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        
        add(new JLabel("Số ĐT:"));
        add(txtSDT);
        
        add(new JLabel("Email:"));
        add(txtEmail);
        
        // Vai trò & Trạng thái
        cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Nhân viên bán hàng", "Thủ kho"});
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Đã nghỉ"});
        
        add(new JLabel("Vai trò:"));
        add(cbVaiTro, "split 3, grow");
        add(new JLabel("Trạng thái:"), "gapleft 10");
        add(cbTrangThai, "grow");

        // Địa chỉ
        txtDiaChi = new JTextArea(3, 0);
        JScrollPane scrollAddr = new JScrollPane(txtDiaChi);
        add(new JLabel("Địa chỉ:"));
        add(scrollAddr);

        // Actions
        JPanel pFooter = new JPanel(new MigLayout("insets 20 0 0 0", "push[][]"));
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        
        JButton btnLuu = new JButton("Lưu");
        btnLuu.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
        btnLuu.addActionListener(e -> actionSave());
        
        pFooter.add(btnHuy);
        pFooter.add(btnLuu);
        add(pFooter, "span 2");

        pack();
        setLocationRelativeTo(parent);
    }

    private void fillData(Object[] data) {
        // Cấu trúc bảng: 0:Mã, 1:Tên, 2:Giới, 3:SĐT, 4:Email, 5:VaiTro, 6:TrangThai
        // Lưu ý: Cần điều chỉnh lại index fill cho đúng nếu có thêm trường Địa chỉ/Ngày sinh ẩn trong Object data thực tế
        
        // Ở form danh sách, model chưa hiện Ngày sinh & Địa chỉ, nên ở đây mình giả lập fill tạm
        // Trong thực tế bạn sẽ query DB lấy full info theo Mã NV (data[0])
        
        txtMaNV.setText(data[0].toString());
        txtHoTen.setText(data[1].toString());
        cbGioiTinh.setSelectedItem(data[2].toString());
        txtSDT.setText(data[3].toString());
        txtEmail.setText(data[4].toString());
        cbVaiTro.setSelectedItem(data[5].toString());
        cbTrangThai.setSelectedItem(data[6].toString());
        
        // Giả lập dữ liệu ẩn
        txtNgaySinh.setText("01/01/1990"); 
        txtDiaChi.setText("TP.HCM");
    }

    private void actionSave() {
        if(txtHoTen.getText().isEmpty() || txtSDT.getText().isEmpty()){
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng nhập họ tên và số điện thoại!");
            return;
        }
        isSave = true;
        dispose();
    }

    public boolean isSave() { return isSave; }
    
    public Object[] getData() {
        return new Object[]{
            txtMaNV.getText().equals("AUTO") ? "NV" + System.currentTimeMillis() % 1000 : txtMaNV.getText(),
            txtHoTen.getText(),
            cbGioiTinh.getSelectedItem(),
            txtSDT.getText(),
            txtEmail.getText(),
            cbVaiTro.getSelectedItem(),
            cbTrangThai.getSelectedItem()
        };
    }
}