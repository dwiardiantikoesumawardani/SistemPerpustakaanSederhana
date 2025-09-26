package com.praktikum.testing.util;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.model.Anggota;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public static boolean isValidNomorTelepon(String telepon) {
        if (telepon == null || telepon.trim().isEmpty()) return false;
        String teleponBersih = telepon.replaceAll("[\\s\\-()]", "");
        return teleponBersih.matches("^(08[0-9]{8,11}|\\+628[0-9]{7,12})$");
    }

    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return false;
        String isbnBersih = isbn.replaceAll("[-\\s]", "");
        return isbnBersih.matches("^[0-9]{10}$") || isbnBersih.matches("^[0-9]{13}$");
    }

    public static boolean isValidBuku(Buku buku) {
        if (buku == null) return false;
        return isValidISBN(buku.getIsbn()) &&
                isValidString(buku.getJudul()) &&
                isValidString(buku.getPengarang()) &&
                buku.getJumlahTotal() > 0 &&
                buku.getJumlahTersedia() >= 0 &&
                buku.getJumlahTersedia() <= buku.getJumlahTotal() &&
                buku.getHarga() > 0;
    }

    public static boolean isValidAnggota(Anggota anggota) {
        if (anggota == null) return false;
        return isValidString(anggota.getIdAnggota()) &&
                isValidString(anggota.getNama()) &&
                isValidEmail(anggota.getEmail()) &&
                isValidNomorTelepon(anggota.getTelepon());
    }

    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isAngkaPositif(double angka) {
        return angka > 0;
    }

    public static boolean isAngkaNonNegatif(double angka) {
        return angka >= 0;
    }
}
