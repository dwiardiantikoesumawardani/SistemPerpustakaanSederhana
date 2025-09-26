package com.praktikum.testing.service;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.repository.RepositoryBuku;
import com.praktikum.testing.util.ValidationUtils;

import java.util.List;
import java.util.Optional;

public class ServicePerpustakaan {

    private final RepositoryBuku repositoryBuku;
    private final KalkulatorDenda kalkulatorDenda;

    public ServicePerpustakaan(RepositoryBuku repositoryBuku, KalkulatorDenda kalkulatorDenda) {
        this.repositoryBuku = repositoryBuku;
        this.kalkulatorDenda = kalkulatorDenda;
    }

    public boolean tambahBuku(Buku buku) {
        if (!ValidationUtils.isValidBuku(buku)) {
            return false;
        }

        Optional<Buku> bukuExisting = repositoryBuku.caribyIsbn(buku.getIsbn());
        if (bukuExisting.isPresent()) {
            return false; // ISBN sudah ada
        }

        return repositoryBuku.simpan(buku);
    }

    public boolean hapusBuku(String isbn) {
        if (!ValidationUtils.isValidISBN(isbn)) {
            return false;
        }

        Optional<Buku> buku = repositoryBuku.caribyIsbn(isbn);
        if (buku.isEmpty()) {
            return false;
        }

        // Cek apakah ada salinan yang sedang dipinjam
        if (buku.get().getJumlahTotal() > buku.get().getJumlahTersedia()) {
            return false;
        }

        return repositoryBuku.hapus(isbn);
    }

    public Optional<Buku> cariBukuByIsbn(String isbn) {
        if (!ValidationUtils.isValidISBN(isbn)) {
            return Optional.empty();
        }
        return repositoryBuku.caribyIsbn(isbn);
    }

    public List<Buku> cariBukuByJudul(String judul) {
        return ValidationUtils.isValidString(judul) ? repositoryBuku.caribyJudul(judul) : List.of();
    }

    public List<Buku> cariBukuByPengarang(String pengarang) {
        return ValidationUtils.isValidString(pengarang) ? repositoryBuku.caribyPengarang(pengarang) : List.of();
    }

    public boolean bukuTersedia(String isbn) {
        Optional<Buku> buku = repositoryBuku.caribyIsbn(isbn);
        return buku.isPresent() && buku.get().isTersedia();
    }

    public int jumlahBukuTersedia(String isbn) {
        return repositoryBuku.caribyIsbn(isbn)
                .map(Buku::getJumlahTersedia)
                .orElse(0);
    }

    public boolean pinjamBuku(String isbn, Anggota anggota) {
        if (!ValidationUtils.isValidAnggota(anggota) || !anggota.isAktif()) {
            return false;
        }

        // FIX: kalau sudah mencapai batas pinjaman → return false
        if (!anggota.isBolehPinjamLagi()) {
            return false;
        }

        Optional<Buku> bukuOpt = repositoryBuku.caribyIsbn(isbn);
        if (bukuOpt.isEmpty() || !bukuOpt.get().isTersedia()) {
            return false;
        }

        Buku buku = bukuOpt.get();

        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, buku.getJumlahTersedia() - 1);
        if (updateBerhasil) {
            anggota.tambahBukuDipinjam(isbn);
            return true;
        }

        return false;
    }

    public boolean kembalikanBuku(String isbn, Anggota anggota) {
        if (!ValidationUtils.isValidISBN(isbn) || anggota == null) {
            return false;
        }

        if (!anggota.getBukuDipinjam().contains(isbn)) {
            return false;
        }

        Optional<Buku> bukuOpt = repositoryBuku.caribyIsbn(isbn);
        if (bukuOpt.isEmpty()) {
            return false;
        }

        Buku buku = bukuOpt.get();
        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, buku.getJumlahTersedia() + 1);
        if (!updateBerhasil) {
            return false;
        }

        anggota.hapusBukuDipinjam(isbn);
        return true;
    }

    // FIX: implementasi method agar tidak selalu return 0
    public int getJumlahTersedia(String isbn) {
        if (!ValidationUtils.isValidISBN(isbn)) {
            return 0;
        }
        return repositoryBuku.caribyIsbn(isbn)
                .map(Buku::getJumlahTersedia)
                .orElse(0);
    }
}
