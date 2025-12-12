package gui.application.form.PhieuNhap;

import gui.application.form.NhaCungCap.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.time.LocalDate;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.datetime.component.date.DatePicker;


import raven.toast.Notifications;

public class DialogAddHang extends JDialog {

    private final Component parent;
    private boolean isSave = false;
    private final boolean isEdit;

    // Components
    private JTextField txtMaThuoc, txtTenThuoc, txtDonViTinh, txtDonGia, txtSoLuongTong, txtSoLo, txtSoLuongLo, txtThanhTien;
    private JTextArea txtDiaChi;
    private DatePicker dateHSD;
    private JFormattedTextField txtHSD;
    public DialogAddHang(Component parent, Object[] data) {
        super(SwingUtilities.windowForComponent(parent), "Thông Tin Hàng Hóa", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.isEdit = (data != null);
        initComponents();
        if(isEdit) fillData(data);
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap,fillx,insets 20, width 500", "[label, 100]10[grow,fill]", "[]15[]"));
        
        JLabel lbTitle = new JLabel(isEdit ? "SỬA HÀNG HÓA" : "THÊM HÀNG HÓA");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +4; foreground:$Accent.color");
        add(lbTitle, "span 2, center, wrap 20");

        txtMaThuoc = new JTextField(isEdit ? "" : "");
        txtMaThuoc.setEditable(false);
        add(new JLabel("Mã thuốc"));
        add(txtMaThuoc);

        txtTenThuoc = new JTextField();
        txtTenThuoc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "");
        add(new JLabel("Tên thuốc:"));
        add(txtTenThuoc);

        txtDonViTinh = new JTextField();
        txtDonGia = new JTextField();
        
        add(new JLabel("Đơn vị tính"));
        add(txtDonViTinh);
        
        add(new JLabel("Đơn giá"));
        add(txtDonGia);

        txtDiaChi = new JTextArea();
        JScrollPane scrollAddr = new JScrollPane(txtDiaChi);
        add(new JLabel("Số lượng tổng"));
        add(scrollAddr);

        txtSoLo = new JTextField();
        txtSoLo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "");
        add(new JLabel("Số lô"));
        add(txtSoLo);

        txtHSD = new JFormattedTextField();
        dateHSD = new DatePicker();
        add(new JLabel("Hạn sử dụng:"));
        txtHSD.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        txtHSD.putClientProperty(FlatClientProperties.STYLE, "focusedBackground:#e8f5e9;");       
        dateHSD.setEditor(txtHSD);
        dateHSD.setDateFormat("dd/MM/yyyy");
        dateHSD.setCloseAfterSelected(true);
        dateHSD.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);

        add(txtHSD, "growx, height 42::");

        txtHSD = new JFormattedTextField();
        txtThanhTien = new JTextField();
        txtThanhTien.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "");
        add(new JLabel("Thành tiền"));
        add(txtThanhTien);
        
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
        txtMaThuoc.setText(data[0].toString());
        txtTenThuoc.setText(data[1].toString());
        txtDonViTinh.setText(data[2].toString());
        txtDonGia.setText(data[3].toString());
        txtDiaChi.setText(data[4].toString());
    }

    private void actionSave() {
        if(txtTenThuoc.getText().trim().isEmpty() || txtDonViTinh.getText().trim().isEmpty()){
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng nhập tên và số điện thoại!");
            return;
        }
        isSave = true;
        dispose();
    }

    public boolean isSave() { return isSave; }
    
    public Object[] getData() {
        return new Object[]{
            txtMaThuoc.getText().equals("AUTO") ? "NCC" + System.currentTimeMillis() % 1000 : txtMaThuoc.getText(),
            txtTenThuoc.getText(),
            txtDonViTinh.getText(),
            txtDonGia.getText(),
            txtDiaChi.getText()
        };
    }
}