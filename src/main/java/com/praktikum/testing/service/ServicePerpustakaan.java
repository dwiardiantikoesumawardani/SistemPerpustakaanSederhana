package com.praktikum.testing.service;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.repository.RepositoryBuku;
import com.praktikum.testing.service.KalkulatorDenda;
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

        // Cek apakah buku dengan ISBN yang sama sudah ada
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
        if (!buku.isPresent()) {
            return false; // Buku tidak ditemukan
        }

        // Cek apakah ada salinan yang sedang dipinjam
        if (buku.get().getJumlahTotal() > buku.get().getJumlahTersedia()) {
            return false; // Tidak bisa dihapus karena ada yang dipinjam
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
        Optional<Buku> buku = repositoryBuku.caribyIsbn(isbn);
        return buku.map(Buku::getJumlahTersedia).orElse(0);
    }

    public boolean pinjamBuku(String isbn, Anggota anggota) {
        // Validasi anggota
        if (!ValidationUtils.isValidAnggota(anggota) || !anggota.isAktif()) {
            return false;
        }

        // Cek apakah anggota masih bisa pinjam
        if (anggota.isBolehPinjamLagi()) {
            return false;
        }

        // Cek ketersediaan buku
        Optional<Buku> bukuOpt = repositoryBuku.caribyIsbn(isbn);
        if (!bukuOpt.isPresent() || !bukuOpt.get().isTersedia()) {
            return false;
        }

        Buku buku = bukuOpt.get();

        // Update jumlah tersedia
        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, buku.getJumlahTersedia() - 1);
        if (updateBerhasil) {
            anggota.tambahBukuDipinjam(isbn);
            return true;
        }

        return false;
    }

    public boolean kembalikanBuku(String isbn, Anggota anggota) {
        // Validasi
        if (!ValidationUtils.isValidISBN(isbn) || anggota == null) {
            return false;
        }

        // Cek apakah anggota meminjam buku ini
        if (!anggota.getBukuDipinjam().contains(isbn)) {
            return false;
        }

        Optional<Buku> bukuOpt = repositoryBuku.caribyIsbn(isbn);
        if (!bukuOpt.isPresent()) {
            return false;
        }

        Buku buku = bukuOpt.get();

        // Update jumlah tersedia
        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, buku.getJumlahTersedia() + 1);
        if (updateBerhasil) {
            anggota.hapusBukuDipinjam(isbn);
            return true;
        }

        return false;
    }

    public int getJumlahTersedia(String number) {
        return 0;
    }
}