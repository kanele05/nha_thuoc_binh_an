package gui.application.form.PhieuNhap;

import com.formdev.flatlaf.FlatClientProperties;

import dao.NhaCungCapDAO;
import dao.PhieuNhapDAO;
import entities.NhaCungCap;
import entities.NhanVien;
import entities.PhieuNhap;

import java.awt.Color;
import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;
public class FormDanhSachPhieuNhap extends javax.swing.JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbThoiGian;
    private JTable table;
    private DefaultTableModel model;
    private PhieuNhapDAO pnDao = new PhieuNhapDAO();
    private NhaCungCapDAO nccDao = new NhaCungCapDAO();
    public FormDanhSachPhieuNhap() {
        initComponents();
        init();
    }
    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow]"));

        add(createHeaderPanel(), "wrap 20");
        add(createToolBarPanel(), "wrap 10");
        add(createTablePanel(), "grow");
        
        loadData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panel.setOpaque(false);
        JLabel lbTitle = new JLabel("Danh Sách Phiếu Nhập Hàng");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
        panel.add(lbTitle);
        return panel;
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 10", "[]10[]10[]push[][]", "[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:darken(@background,3%)");

        txtTimKiem = new JTextField();
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo mã phiếu, NCC...");
        
        cbThoiGian = new JComboBox<>(new String[]{"Tháng này", "Tháng trước", "Tất cả"});
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang chờ nhập hàng", "Đã nhập hàng", "Đã hủy"});

        JButton btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.addActionListener(e -> actionXemChiTiet());

        JButton btnXacNhan = new JButton("Xác nhận nhập kho");
        btnXacNhan.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:#4CAF50;"
                + "foreground:#ffffff;"
                + "font:bold");
        btnXacNhan.addActionListener(e -> actionXacNhan());

        panel.add(txtTimKiem, "w 250");
        panel.add(cbThoiGian);
        panel.add(cbTrangThai);
        panel.add(btnXemChiTiet);
        panel.add(btnXacNhan);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = {"Mã Phiếu", "Ngày Tạo", "Tổng Tiền", "Người Nhập", "Nhà Cung Cấp","Trạng Thái" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
        
        // Căn phải cột tiền
        table.getColumnModel().getColumn(4).setCellRenderer(new RightAlignRenderer());
        
        // Tô màu trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        panel.add(new JScrollPane(table));
        return panel;
    }

    private class RightAlignRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }
    
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value.toString();
            
            if (status.equals("Đang chờ nhập hàng")) {
                com.setForeground(new Color(255, 152, 0)); // Cam
                com.setFont(com.getFont().deriveFont(java.awt.Font.BOLD));
            } else if (status.equals("Đã nhập hàng")) {
                com.setForeground(new Color(56, 142, 60)); // Xanh lá
            } else if (status.equals("Đã hủy")) {
                com.setForeground(new Color(211, 47, 47)); // Đỏ
            } else {
                com.setForeground(Color.GRAY);
            }
            
            if (isSelected) com.setForeground(Color.WHITE);
            return com;
        }
    }
    private void loadData() {
        model.setRowCount(0);
        
        ArrayList<PhieuNhap> list = pnDao.getAllPhieuNhap();
        for(PhieuNhap pn : list) {
        	String maNcc = pn.getNcc().getMaNCC();
        	String tenNv = new String("Admin");
        	String tenNcc = nccDao.getTenNCCByMa(maNcc);
        	if(tenNcc == null) tenNcc = maNcc;
        	String ngayTao = pn.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        	String tongTien = String.format("%,.0f đ", pn.getTongTien());
        
        	model.addRow(new Object[] {
        		pn.getMaPN(),
        		ngayTao,
        		tongTien,
        		tenNv,
        		tenNcc,
                        pn.getTrangThai(),
        	});
        
        }
    }

    private void actionXacNhan() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Vui lòng chọn phiếu cần xác nhận!");
            return;
        }
        
        String currentStatus = model.getValueAt(row, 5).toString();
        String maPhieu = model.getValueAt(row, 0).toString();

        if (currentStatus.equals("Đã nhập hàng")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Phiếu này đã nhập kho rồi!");
            return;
        }
        if (currentStatus.equals("Đã hủy")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Không thể nhập kho phiếu đã hủy!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận hàng mã phiếu " + maPhieu + " đã về kho đầy đủ?\n" +
                "Hệ thống sẽ cập nhật số lượng tồn kho.", 
                "Xác nhận nhập kho", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Update Database
            // 1. Update PhieuNhap SET TrangThai = 'Đã nhập hàng' WHERE MaPN = ...
            // 2. Trigger/Code: Cộng số lượng trong bảng ChiTietPhieuNhap vào bảng LoThuoc (Kho)
            
            model.setValueAt("Đã nhập hàng", row, 5);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đã nhập kho thành công!");
        }
    }
    
    private void actionXemChiTiet() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String maPhieu = model.getValueAt(row, 0).toString();
        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Xem chi tiết phiếu: " + maPhieu);
        // Mở Dialog hiển thị bảng Chi tiết phiếu nhập tại đây
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
