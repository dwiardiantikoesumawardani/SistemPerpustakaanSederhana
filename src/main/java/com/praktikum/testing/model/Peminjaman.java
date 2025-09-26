package com.praktikum.testing.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Peminjaman {
    private String idPeminjaman;
    private String idAnggota;
    private String isbnBuku;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private boolean sudahDikembalikan;

    public Peminjaman() {}

    public Peminjaman(String idPeminjaman, String idAnggota, String isbnBuku,
                      LocalDate tanggalPinjam, LocalDate tanggalJatuhTempo) {
        this.idPeminjaman = idPeminjaman;
        this.idAnggota = idAnggota;
        this.isbnBuku = isbnBuku;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.sudahDikembalikan = false;
    }

    // Getters & Setters
    public String getIdPeminjaman() { return idPeminjaman; }
    public void setIdPeminjaman(String idPeminjaman) { this.idPeminjaman = idPeminjaman; }

    public String getIdAnggota() { return idAnggota; }
    public void setIdAnggota(String idAnggota) { this.idAnggota = idAnggota; }

    public String getIsbnBuku() { return isbnBuku; }
    public void setIsbnBuku(String isbnBuku) { this.isbnBuku = isbnBuku; }

    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public void setTanggalPinjam(LocalDate tanggalPinjam) { this.tanggalPinjam = tanggalPinjam; }

    public LocalDate getTanggalJatuhTempo() { return tanggalJatuhTempo; }
    public void setTanggalJatuhTempo(LocalDate tanggalJatuhTempo) { this.tanggalJatuhTempo = tanggalJatuhTempo; }

    public LocalDate getTanggalKembali() { return tanggalKembali; }
    public void setTanggalKembali(LocalDate tanggalKembali) { this.tanggalKembali = tanggalKembali; }

    public boolean isSudahDikembalikan() { return sudahDikembalikan; }
    public void setSudahDikembalikan(boolean sudahDikembalikan) { this.sudahDikembalikan = sudahDikembalikan; }

    public boolean isTerlambat() {
        if (sudahDikembalikan && tanggalKembali != null) {
            return tanggalKembali.isAfter(tanggalJatuhTempo);
        }
        return LocalDate.now().isAfter(tanggalJatuhTempo);
    }

    public long getHariTerlambat() {
        LocalDate tanggalAkhir = sudahDikembalikan && tanggalKembali != null ? tanggalKembali : LocalDate.now();
        if (!tanggalAkhir.isAfter(tanggalJatuhTempo)) return 0;
        return ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalAkhir);
    }

    public long getDurasiPeminjaman() {
        LocalDate tanggalAkhir = sudahDikembalikan && tanggalKembali != null ? tanggalKembali : LocalDate.now();
        return ChronoUnit.DAYS.between(tanggalPinjam, tanggalAkhir);
    }
}
