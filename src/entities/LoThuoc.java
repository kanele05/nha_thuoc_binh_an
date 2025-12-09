/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.time.LocalDate;

/**
 *
 * @author khang
 */
public class LoThuoc {
    private String maLo;
    private Thuoc thuoc;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private int soLuongTon;
    private boolean trangThai;

    public LoThuoc(String maLo, Thuoc thuoc, LocalDate ngayNhap, LocalDate hanSuDung, int soLuongTon, boolean trangThai) {
        this.maLo = maLo;
        this.thuoc = thuoc;
        this.ngayNhap = ngayNhap;
        this.hanSuDung = hanSuDung;
        this.soLuongTon = soLuongTon;
        this.trangThai = trangThai;
    }

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo;
    }

    public Thuoc getThuoc() {
        return thuoc;
    }

    public void setThuoc(Thuoc thuoc) {
        this.thuoc = thuoc;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "LoThuoc{" + "maLo=" + maLo + ", thuoc=" + thuoc + ", ngayNhap=" + ngayNhap + ", hanSuDung=" + hanSuDung + ", soLuongTon=" + soLuongTon + ", trangThai=" + trangThai + '}';
    }
    
}
