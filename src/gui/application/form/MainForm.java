package gui.application.form;

import gui.application.form.PhieuNhap.FormDanhSachPhieuNhap;
import gui.application.form.PhieuNhap.FormNhapHang;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import gui.application.Application;
import gui.application.form.other.FormDashboard;
import gui.menu.Menu;
import gui.menu.MenuAction;
import raven.application.form.FormNhanVien;

/**
 *
 * @author Raven
 */
public class MainForm extends JLayeredPane {

    public MainForm() {
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
//        initMenuArrowIcon();
//        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
//                + "background:$Menu.button.background;"
//                + "arc:999;"
//                + "focusWidth:0;"
//                + "borderWidth:0");
//        menuButton.addActionListener((ActionEvent e) -> {
//            setMenuFull(!menu.isMenuFull());
//        });
        initMenuEvent();
//        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
//        add(menuButton);
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
//        initMenuArrowIcon();
    }

//    private void initMenuArrowIcon() {
//        if (menuButton == null) {
//            menuButton = new JButton();
//        }
//        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
//        menuButton.setIcon(new FlatSVGIcon("raven/icon/svg/" + icon, 0.8f));
//    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            // Application.mainForm.showForm(new DefaultForm("Form : " + index + " " + subIndex));
            if (index == 0) {
                Application.showForm(new FormDashboard());
            } else if (index == 1) {
                if (subIndex == 1) {
                    Application.showForm(new FormBanHang());
                } else if (subIndex == 2) {
                    Application.showForm(new FormDatThuoc());
                } else if (subIndex == 3) {
                    Application.showForm(new FormTraHang());
                } else if (subIndex == 4) {
                    Application.showForm(new FormDoiHang());
                }else {
                    action.cancel();
                }
            }else if (index == 2) {
                if (subIndex == 1) {
                    Application.showForm(new FormQuanLyThuoc());
                }else if (subIndex == 2) {
                    FormQuanLyThuoc form = new FormQuanLyThuoc();
                    Application.showForm(form);
                    form.openThemMoi();
                }
            } else if (index == 3) { 
                if (subIndex == 1) {
                    Application.showForm(new FormDanhSachPhieuNhap());
                }else if (subIndex == 2) {
                    Application.showForm(new FormNhapHang());
                }
            }else if (index == 4) { 
                Application.showForm(new FormQuanLyLoThuoc());
            }else if (index == 5) { 
               if (subIndex == 1) {
                    Application.showForm(new FormBangGia());
                }else if (subIndex == 2) {
                    FormBangGia form = new FormBangGia();
                    Application.showForm(form);
                    form.openThemMoi();
                }
            }else if (index == 6) { 
                if (subIndex == 1) {
                    Application.showForm(new FormNhanVien());
                } else if (subIndex == 2) { 
                    FormNhanVien form = new FormNhanVien();
                    Application.showForm(form);
                    form.openThemMoi(); 
                }
            }else if (index == 7) { 
                if (subIndex == 1) { 
                    Application.showForm(new FormKhachHang());
                } else if (subIndex == 2) { 
                    FormKhachHang form = new FormKhachHang();
                    Application.showForm(form);
                    form.openThemMoi(); 
                }
            }else if (index == 8) { // Nhà cung cấp
                Application.showForm(new FormNhaCungCap());
            }else if (index == 9) { // Báo cáo
                if (subIndex == 1) { // Báo cáo doanh thu
                    Application.showForm(new FormBaoCaoDoanhThu());
                } else if (subIndex == 2) {
                    Application.showForm(new FormBaoCaoTonKho());
                }
            }else if (index == 10) { // Cảnh báo
                if (subIndex == 1) { // Sắp hết hạn
                    Application.showForm(new FormCanhBaoHetHan());
                } else if (subIndex == 2) { // Tồn kho thấp
                    Application.showForm(new FormCanhBaoTonKho());
                }
            }else if (index == 11) { // Cảnh báo
                if (subIndex == 1) { // Sắp hết hạn
                    Application.showForm(new FormHoSo());
                } else if (subIndex == 2) { // Tồn kho thấp
                    Application.logout();
                }
            }
        });
    }

//    private void setMenuFull(boolean full) {
//        String icon;
//        if (getComponentOrientation().isLeftToRight()) {
//            icon = (full) ? "menu_left.svg" : "menu_right.svg";
//        } else {
//            icon = (full) ? "menu_right.svg" : "menu_left.svg";
//        }
//        menuButton.setIcon(new FlatSVGIcon("raven/icon/svg/" + icon, 0.8f));
//        menu.setMenuFull(full);
//        revalidate();
//    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
//                int menuButtonWidth = menuButton.getPreferredSize().width;
//                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
//                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
//                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
//                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
