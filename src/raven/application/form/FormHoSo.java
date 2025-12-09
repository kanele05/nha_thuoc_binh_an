package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Font;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.datetime.component.date.DatePicker;
import raven.toast.Notifications;

public class FormHoSo extends JPanel {

    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JFormattedTextField txtNgaySinh;
    private JComboBox<String> cbGioiTinh;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JTextField txtVaiTro;
    private JTextArea txtDiaChi;
    
    private DatePicker datePicker;

    public FormHoSo() {
        initComponents();
        init();
    }

    private void init() {
        // Layout chính
        setLayout(new MigLayout("wrap,fill,insets 20", "[center]", "[]20[]"));

        // 1. Header
        add(createHeaderPanel(), "wrap");

        // 2. Form thông tin (Đặt trong 1 panel con căn giữa để đẹp hơn)
        add(createProfilePanel(), "w 800!"); 

        // Load dữ liệu giả lập (thực tế lấy từ Session đăng nhập)
        loadDataMock();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Hồ Sơ Nhân Viên");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[][grow,fill]", "[]15[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%);");

        // --- Định nghĩa Components ---
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false); // Không cho sửa mã
        txtMaNV.putClientProperty(FlatClientProperties.STYLE, "font:bold");

        txtHoTen = new JTextField();
        txtHoTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập họ tên");

        txtNgaySinh = new JFormattedTextField();
        datePicker = new DatePicker();
        datePicker.setEditor(txtNgaySinh);
        datePicker.setDateFormat("dd/MM/yyyy");
        datePicker.setCloseAfterSelected(true);
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);

        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});

        txtSDT = new JTextField();
        txtEmail = new JTextField();
        
        txtVaiTro = new JTextField();
        txtVaiTro.setEditable(false); // Không cho tự sửa quyền

        txtDiaChi = new JTextArea(3, 0);
        JScrollPane scrollDiaChi = new JScrollPane(txtDiaChi);

        // --- Add vào Panel ---
        // Nhóm 1: Thông tin cơ bản
        panel.add(createSectionTitle("Thông tin cơ bản"), "span 2, wrap");
        
        panel.add(new JLabel("Mã nhân viên:"));
        panel.add(txtMaNV);

        panel.add(new JLabel("Họ và tên:"));
        panel.add(txtHoTen);

        panel.add(new JLabel("Ngày sinh:"));
        panel.add(txtNgaySinh);

        panel.add(new JLabel("Giới tính:"));
        panel.add(cbGioiTinh);

        // Nhóm 2: Liên hệ
        panel.add(createSectionTitle("Thông tin liên hệ"), "span 2, wrap, gaptop 10");

        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtSDT);

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        panel.add(new JLabel("Địa chỉ:"));
        panel.add(scrollDiaChi);
        
        // Nhóm 3: Hệ thống
        panel.add(createSectionTitle("Tài khoản hệ thống"), "span 2, wrap, gaptop 10");
        
        panel.add(new JLabel("Vai trò:"));
        panel.add(txtVaiTro);
        
        panel.add(new JLabel("Mật khẩu:"));
        JButton btnDoiMatKhau = new JButton("Đổi mật khẩu");
        btnDoiMatKhau.addActionListener(e -> actionDoiMatKhau());
        panel.add(btnDoiMatKhau, "w 150!, left"); // Nút nhỏ, căn trái

        // --- Nút Lưu ---
        JPanel pActions = new JPanel(new MigLayout("insets 20 0 0 0", "push[]push")); // Căn giữa nút
        pActions.setOpaque(false);
        
        JButton btnLuu = new JButton("Lưu Thay Đổi");
        btnLuu.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#2196F3;"
                + "foreground:#ffffff;"
                + "font:bold +2;"
                + "margin:10,30,10,30");
        btnLuu.addActionListener(e -> actionSave());
        
        pActions.add(btnLuu);
        panel.add(pActions, "span 2");

        return panel;
    }
    
    private JLabel createSectionTitle(String title) {
        JLabel lb = new JLabel(title);
        lb.putClientProperty(FlatClientProperties.STYLE, "font:bold; foreground:$Accent.color");
        return lb;
    }

    private void loadDataMock() {
        // Giả lập dữ liệu user đang đăng nhập
        txtMaNV.setText("NV001");
        txtHoTen.setText("Phạm Văn Admin");
        txtNgaySinh.setText("15/08/1995");
        cbGioiTinh.setSelectedItem("Nam");
        txtSDT.setText("0909123456");
        txtEmail.setText("admin@nhathuoc.com");
        txtDiaChi.setText("123 Đường 3/2, Quận 10, TP.HCM");
        txtVaiTro.setText("Quản Lý (Admin)");
    }

    private void actionSave() {
        if(txtHoTen.getText().isEmpty()) {
             Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Họ tên không được để trống!");
             return;
        }
        
        // TODO: Update thông tin vào Database (UPDATE NhanVien SET ...)
        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Cập nhật hồ sơ thành công!");
    }
    
    private void actionDoiMatKhau() {
        DialogDoiMatKhau dialog = new DialogDoiMatKhau(this);
        dialog.setVisible(true);
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