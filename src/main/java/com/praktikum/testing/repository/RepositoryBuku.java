package com.praktikum.testing.repository;

import com.praktikum.testing.model.Buku;
import java.util.List;
import java.util.Optional;

public interface RepositoryBuku {
    boolean simpan(Buku buku);
    Optional<Buku> caribyIsbn(String isbn);
    List<Buku> caribyJudul(String judul);
    List<Buku> caribyPengarang(String pengarang);
    boolean hapus(String isbn);
    boolean updateJumlahTersedia(String isbn, int jumlahTersediaBaru);
    List<Buku> cariSemua();
}
