package raven.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class FormBanHang extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private PanelDanhSachHoaDon panelDanhSach;
    private PanelBanHangGiaoDien panelBanHang;
    private boolean isUpdating = false;
    public FormBanHang() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        panelDanhSach = new PanelDanhSachHoaDon();
        panelBanHang = new PanelBanHangGiaoDien();
        
        mainPanel.add(panelDanhSach, "LIST");
        mainPanel.add(panelBanHang, "POS");
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public void showBanHang() {
        panelBanHang.resetForm();
        cardLayout.show(mainPanel, "POS");
    }
    
    public void showDanhSach() {
        panelDanhSach.loadData();
        cardLayout.show(mainPanel, "LIST");
    }

    // =========================================================================
    // 1. PANEL DANH SÁCH HÓA ĐƠN
    // =========================================================================
    private class PanelDanhSachHoaDon extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        
        public PanelDanhSachHoaDon() {
            setLayout(new MigLayout("wrap,fill,insets 20", "[fill]", "[][][grow]"));
            JLabel lbTitle = new JLabel("Lịch Sử Bán Hàng");
            lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
            add(lbTitle, "wrap 20");
            add(createToolBar(), "wrap 10");
            add(createTable(), "grow");
            loadData();
        }
        
        private JPanel createToolBar() {
            JPanel panel = new JPanel(new MigLayout("insets 10", "[]10[]push[]", "[]"));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            JTextField txtSearch = new JTextField();
            txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mã hóa đơn, tên khách...");
            JButton btnTim = new JButton("Tìm kiếm");
            JButton btnBanHang = new JButton("➕ Tạo hóa đơn mới");
            btnBanHang.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
            btnBanHang.addActionListener(e -> showBanHang());
            panel.add(txtSearch, "w 250");
            panel.add(btnTim);
            panel.add(btnBanHang);
            return panel;
        }
        
        private JPanel createTable() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            String[] cols = {"Mã HĐ", "Ngày Bán", "Khách Hàng", "Tổng Tiền", "Thanh Toán", "Người Bán"};
            model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            table = new JTable(model);
            table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30; showHorizontalLines:true");
            table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
            table.getColumnModel().getColumn(3).setCellRenderer(new RightAlignRenderer());
            panel.add(new JScrollPane(table));
            return panel;
        }
        
        public void loadData() {
            model.setRowCount(0);
            model.addRow(new Object[]{"HD001", "08/12/2025 10:30", "Nguyễn Văn A", "150.000 ₫", "Tiền mặt", "Admin"});
        }
    }

    // =========================================================================
    // 2. PANEL BÁN HÀNG (CẬP NHẬT THUẾ CỐ ĐỊNH 5%)
    // =========================================================================
    private class PanelBanHangGiaoDien extends JPanel {
        
        private JTextField txtTimKiem, txtMaKH, txtTenKH, txtSDT;
        private JComboBox<String> cbHinhThucTT;
        private JTable tableThuoc, tableGioHang;
        private DefaultTableModel modelThuoc, modelGioHang;
        
        private JLabel lbTongTien, lbThanhTien, lbTienThue;
        private JTextField txtGiamGia, txtThueVAT;
        
        private JButton btnThemVaoGio, btnXoaKhoiGio, btnThanhToan, btnHuyHD;
        
        private double tongTien = 0;
        private double giamGia = 0;
        private double thueVAT = 0;
        private final double DEFAULT_VAT_RATE = 5.0; // CỐ ĐỊNH 5%

        public PanelBanHangGiaoDien() {
            initPOS();
        }

        private void initPOS() {
            setLayout(new MigLayout("wrap,fillx,insets 20", "[70%][30%]", "[][][grow][]"));
            
            JPanel header = new JPanel(new MigLayout("insets 0", "[]10[]push[]"));
            header.setOpaque(false);
            JButton btnBack = new JButton(" Quay lại");
            btnBack.addActionListener(e -> showDanhSach());
            JLabel lbTitle = new JLabel("Bán hàng / Tạo hóa đơn");
            lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +8");
            header.add(btnBack);
            header.add(lbTitle);
            add(header, "span 2, growx, wrap 15");
            
            add(createSearchPanel(), "grow");
            add(createCustomerPanel(), "grow,wrap");
            add(createMedicineTablePanel(), "grow");
            add(createCartPanel(), "grow,wrap");
            
            add(createPaymentPanel(), "span 2,grow");
        }
        
        public void resetForm() {
            modelGioHang.setRowCount(0);
            txtMaKH.setText("");
            txtTenKH.setText("");
            txtSDT.setText("");
            txtGiamGia.setText("0");
            
            // Reset về mặc định 5%
            txtThueVAT.setText(String.valueOf((int)DEFAULT_VAT_RATE)); 
            
            tinhTongTien();
        }
        
        private JPanel createSearchPanel() {
            JPanel panel = new JPanel(new MigLayout("insets 15,fillx", "[]10[]push[]", "[]"));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            JLabel lbSearch = new JLabel("Tìm kiếm thuốc:");
            lbSearch.putClientProperty(FlatClientProperties.STYLE, "font:bold");
            txtTimKiem = new JTextField();
            txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên thuốc, hoạt chất...");
            JButton btnTimKiem = new JButton("Tìm kiếm");
            btnTimKiem.putClientProperty(FlatClientProperties.STYLE, "background:#2196F3; foreground:#fff");
            panel.add(lbSearch, "split 2");
            panel.add(txtTimKiem, "w 300");
            panel.add(btnTimKiem);
            return panel;
        }
        
        private JPanel createCustomerPanel() {
            JPanel panel = new JPanel(new MigLayout("insets 15,wrap 2", "[][grow,fill]", ""));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            JLabel lbTitle = new JLabel("Thông tin khách hàng");
            lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
            panel.add(lbTitle, "span 2,wrap 10");
            txtSDT = new JTextField();
            txtSDT.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập SĐT");
            JButton btnTimKH = new JButton("🔍");
            panel.add(new JLabel("SĐT:"));
            panel.add(txtSDT, "split 2");
            panel.add(btnTimKH, "wrap");
            txtMaKH = new JTextField();
            txtMaKH.setEnabled(false);
            panel.add(new JLabel("Mã KH:"));
            panel.add(txtMaKH, "wrap");
            txtTenKH = new JTextField();
            txtTenKH.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên khách hàng");
            panel.add(new JLabel("Tên KH:"));
            panel.add(txtTenKH, "wrap");
            return panel;
        }
        
        private JPanel createMedicineTablePanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel title = new JLabel("Danh sách thuốc trong kho");
            title.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
            String[] columns = {"Mã", "Tên thuốc", "Lô", "HSD", "Tồn", "Giá bán"};
            modelThuoc = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            tableThuoc = new JTable(modelThuoc);
            tableThuoc.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines:true; rowHeight:30");
            tableThuoc.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
            modelThuoc.addRow(new Object[]{"T001", "Paracetamol 500mg", "A123", "12/2025", "500", "5.000"});
            modelThuoc.addRow(new Object[]{"T002", "Amoxicillin 500mg", "B456", "03/2026", "200", "15.000"});
            modelThuoc.addRow(new Object[]{"T003", "Vitamin C 1000mg", "C789", "08/2025", "350", "8.500"});
            JScrollPane scroll = new JScrollPane(tableThuoc);
            scroll.setBorder(null);
            btnThemVaoGio = new JButton("Thêm vào giỏ →");
            btnThemVaoGio.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#fff; font:bold");
            btnThemVaoGio.addActionListener(e -> themVaoGioHang());
            panel.add(title, BorderLayout.NORTH);
            panel.add(scroll, BorderLayout.CENTER);
            panel.add(btnThemVaoGio, BorderLayout.SOUTH);
            return panel;
        }
        
        private JPanel createCartPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:darken(@background,3%)");
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel title = new JLabel("Giỏ hàng");
            title.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
            String[] columns = {"Tên thuốc", "SL", "Giá", "Thành tiền"};
            modelGioHang = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return column == 1; }
            };
            tableGioHang = new JTable(modelGioHang);
            tableGioHang.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines:true; rowHeight:30");
            tableGioHang.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:35; font:bold");
            modelGioHang.addTableModelListener(e -> {
                if (!isUpdating) {
                    tinhTongTien();
                }
            });
            JScrollPane scroll = new JScrollPane(tableGioHang);
            scroll.setBorder(null);
            btnXoaKhoiGio = new JButton("Xóa khỏi giỏ");
            btnXoaKhoiGio.putClientProperty(FlatClientProperties.STYLE, "background:#F44336; foreground:#fff");
            btnXoaKhoiGio.addActionListener(e -> xoaKhoiGioHang());
            panel.add(title, BorderLayout.NORTH);
            panel.add(scroll, BorderLayout.CENTER);
            panel.add(btnXoaKhoiGio, BorderLayout.SOUTH);
            return panel;
        }
        
        private JPanel createPaymentPanel() {
            JPanel panel = new JPanel(new MigLayout("insets 15", "[grow][][]", ""));
            panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:lighten(#E8F5E9,5%)");
            
            JPanel infoPanel = new JPanel(new MigLayout("wrap 2", "[][grow,right]", "[]5[]5[]5[]"));
            infoPanel.setOpaque(false);
            
            lbTongTien = new JLabel("0 ₫");
            lbTongTien.putClientProperty(FlatClientProperties.STYLE, "font:bold +4");
            
            lbTienThue = new JLabel("0 ₫");
            
            lbThanhTien = new JLabel("0 ₫");
            lbThanhTien.putClientProperty(FlatClientProperties.STYLE, "font:bold +8;foreground:#4CAF50");
            
            txtGiamGia = new JTextField("0", 10);
            txtGiamGia.setHorizontalAlignment(JTextField.RIGHT);
            txtGiamGia.addActionListener(e -> tinhTongTien()); 
            
            // --- THIẾT LẬP THUẾ CỐ ĐỊNH ---
            txtThueVAT = new JTextField(String.valueOf((int)DEFAULT_VAT_RATE), 5); 
            txtThueVAT.setHorizontalAlignment(JTextField.RIGHT);
            txtThueVAT.setEditable(false); // KHÔNG CHO SỬA
            txtThueVAT.setFocusable(false); // Không focus vào làm gì
            // ------------------------------
            
            infoPanel.add(new JLabel("Tổng tiền hàng:"));
            infoPanel.add(lbTongTien);
            
            infoPanel.add(new JLabel("Giảm giá (VNĐ):"));
            infoPanel.add(txtGiamGia);
            
            JPanel pTax = new JPanel(new MigLayout("insets 0", "[]5[]push"));
            pTax.setOpaque(false);
            pTax.add(new JLabel("Thuế VAT (%):"));
            pTax.add(txtThueVAT);
            
            infoPanel.add(pTax);
            infoPanel.add(lbTienThue);
            
            JSeparator sep = new JSeparator();
            infoPanel.add(sep, "span 2, growx, gapy 5");
            
            infoPanel.add(new JLabel("THÀNH TIỀN:"));
            infoPanel.add(lbThanhTien);
            
            JPanel ttPanel = new JPanel(new MigLayout("", "[][]", ""));
            ttPanel.setOpaque(false);
            ttPanel.add(new JLabel("Hình thức TT:"));
            cbHinhThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Công nợ"});
            ttPanel.add(cbHinhThucTT);
            
            btnThanhToan = new JButton("THANH TOÁN");
            btnThanhToan.putClientProperty(FlatClientProperties.STYLE, "background:#4CAF50; foreground:#FFFFFF; font:bold +4; arc:15");
            btnThanhToan.setPreferredSize(new Dimension(200, 60));
            btnThanhToan.addActionListener(e -> thanhToan());
            
            btnHuyHD = new JButton("Hủy");
            btnHuyHD.putClientProperty(FlatClientProperties.STYLE, "background:#9E9E9E; arc:15; foreground:#fff");
            btnHuyHD.setPreferredSize(new Dimension(100, 60));
            btnHuyHD.addActionListener(e -> huyHoaDon());
            
            panel.add(infoPanel, "grow");
            panel.add(ttPanel, "");
            panel.add(btnHuyHD, "");
            panel.add(btnThanhToan, "");
            
            return panel;
        }
        
        private void tinhTongTien() {
            isUpdating = true;
            try {
                tongTien = 0;
                for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                    try {
                        int sl = Integer.parseInt(modelGioHang.getValueAt(i, 1).toString());
                        String giaStr = modelGioHang.getValueAt(i, 2).toString().replace(" ₫", "").replace(".", "");
                        double gia = Double.parseDouble(giaStr);
                        double tt = sl * gia;
                        
                        modelGioHang.setValueAt(formatCurrency(tt), i, 3);
                        
                        tongTien += tt;
                    } catch(Exception e){}
                }
                
                try {
                    giamGia = Double.parseDouble(txtGiamGia.getText().replace(".", ""));
                } catch (Exception e) { giamGia = 0; }
                
                // Tính thuế
                double taxableAmount = Math.max(0, tongTien - giamGia);
                thueVAT = taxableAmount * (DEFAULT_VAT_RATE / 100);
                double thanhTien = taxableAmount + thueVAT;
                
                lbTongTien.setText(formatCurrency(tongTien));
                lbTienThue.setText(formatCurrency(thueVAT));
                lbThanhTien.setText(formatCurrency(thanhTien));
                
            } finally {
                isUpdating = false; 
            }
        }
        
        private void themVaoGioHang() {
            int selectedRow = tableThuoc.getSelectedRow();
            if (selectedRow == -1) return;
            String tenThuoc = modelThuoc.getValueAt(selectedRow, 1).toString();
            String giaStr = modelThuoc.getValueAt(selectedRow, 5).toString().replace(".", "");
            double gia = Double.parseDouble(giaStr);
            String soLuongStr = JOptionPane.showInputDialog(this, "Nhập số lượng:", "1");
            if (soLuongStr == null) return;
            try {
                int soLuong = Integer.parseInt(soLuongStr);
                modelGioHang.addRow(new Object[]{ tenThuoc, soLuong, formatCurrency(gia), formatCurrency(gia*soLuong) });
                tinhTongTien();
            } catch(Exception e){}
        }
        
        private void xoaKhoiGioHang() {
            int row = tableGioHang.getSelectedRow();
            if (row != -1) {
                modelGioHang.removeRow(row);
                tinhTongTien();
            }
        }
        
        private void thanhToan() {
            if (modelGioHang.getRowCount() == 0) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán " + lbThanhTien.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Thanh toán thành công!");
                showDanhSach();
            }
        }
        
        private void huyHoaDon() {
            showDanhSach();
        }
        
        private String formatCurrency(double amount) {
            return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " ₫";
        }
    }
    
    private class RightAlignRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }
}