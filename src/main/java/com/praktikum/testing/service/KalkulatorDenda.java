package com.praktikum.testing.service;

import com.praktikum.testing.model.Peminjaman;
import com.praktikum.testing.model.Anggota;

public class KalkulatorDenda {

    // tarif denda harian per tipe (dalam Rupiah)
    private static final double TARIF_DENDA_MAHASISWA = 1000.0;
    private static final double TARIF_DENDA_DOSEN = 2000.0;
    private static final double TARIF_DENDA_UMUM = 1500.0;

    // batas maksimal denda per tipe (dalam Rupiah)
    private static final double DENDA_MAX_MAHASISWA = 50000.0;
    private static final double DENDA_MAX_DOSEN = 100000.0;
    private static final double DENDA_MAX_UMUM = 75000.0;

    public double hitungDenda(Peminjaman peminjaman, Anggota anggota) {
        if (peminjaman == null || anggota == null) {
            throw new IllegalArgumentException("Peminjaman dan Anggota tidak boleh null.");
        }

        if (!peminjaman.isTerlambat()) {
            return 0.0;
        }

        long hariTerlambat = peminjaman.getHariTerlambat();
        if (hariTerlambat <= 0) {
            return 0.0;
        }

        double tarifHarian = getTarifDendaHarian(anggota.getTipeAnggota());
        double totalDenda = tarifHarian * hariTerlambat;
        double dendaMax = getDendaMaksimal(anggota.getTipeAnggota());

        return Math.min(totalDenda, dendaMax);
    }

    public double getTarifDendaHarian(Anggota.TipeAnggota tipeAnggota) {
        if (tipeAnggota == null) {
            throw new IllegalArgumentException("Tipe anggota tidak boleh null!");
        }

        switch (tipeAnggota) {
            case MAHASISWA:
                return TARIF_DENDA_MAHASISWA;
            case DOSEN:
                return TARIF_DENDA_DOSEN;
            case UMUM:
                return TARIF_DENDA_UMUM;
            default:
                throw new IllegalArgumentException("Tipe anggota tidak dikenal: " + tipeAnggota);
        }
    }

    public double getDendaMaksimal(Anggota.TipeAnggota tipeAnggota) {
        if (tipeAnggota == null) {
            throw new IllegalArgumentException("Tipe anggota tidak boleh null!");
        }

        switch (tipeAnggota) {
            case MAHASISWA:
                return DENDA_MAX_MAHASISWA;
            case DOSEN:
                return DENDA_MAX_DOSEN;
            case UMUM:
                return DENDA_MAX_UMUM;
            default:
                throw new IllegalArgumentException("Tipe anggota tidak dikenal: " + tipeAnggota);
        }
    }

    // ganti/replace method adaDenda(...) kalau belum seperti ini:
    public boolean adaDenda(Peminjaman peminjaman) {
        if (peminjaman == null) {
            return false;
        }
        // Pastikan kita menganggap ada denda kalau ada keterlambatan hari > 0
        // (gunakan isTerlambat() jika tersedia; combine untuk safety)
        return peminjaman.isTerlambat() || peminjaman.getHariTerlambat() > 0;
    }

    // ganti/replace method getDeskripsiDenda(...) dengan ini:
    public String getDeskripsiDenda(double denda) {
        if (denda <= 0) {
            return "Tidak ada denda";
        } else if (denda <= 10000) {
            return "Denda ringan";
        } else if (denda <= 50000) {
            return "Denda sedang";
        } else {
            return "Denda berat";
        }
    }

}