package com.praktikum.testing.service;

import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.model.Buku;
import com.praktikum.testing.repository.RepositoryBuku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Service Perpustakaan")
public class ServicePerpustakaanTest {

    @Mock
    private RepositoryBuku mockRepositoryBuku;

    // jangan di-mock
    private KalkulatorDenda kalkulatorDenda;

    private ServicePerpustakaan servicePerpustakaan;
    private Buku bukuTest;
    private Anggota anggotaTest;

    @BeforeEach
    void setUp() {
        kalkulatorDenda = new KalkulatorDenda(); // pakai instance asli
        servicePerpustakaan = new ServicePerpustakaan(mockRepositoryBuku, kalkulatorDenda);

        bukuTest = new Buku("1234567890", "Pemrograman Java", "John Doe", 5, 150000.0);
        anggotaTest = new Anggota("A001", "John Student", "john@student.ac.id",
                "081234567890", Anggota.TipeAnggota.MAHASISWA);
    }

    @Test
    @DisplayName("Tambah buku berhasil ketika data valid dan buku belum ada")
    void testTambahBukuBerhasil() {
        when(mockRepositoryBuku.caribyIsbn("1234567890")).thenReturn(Optional.empty());
        when(mockRepositoryBuku.simpan(bukuTest)).thenReturn(true);

        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        assertTrue(hasil, "Harus berhasil menambah buku");
        verify(mockRepositoryBuku).caribyIsbn("1234567890");
        verify(mockRepositoryBuku).simpan(bukuTest);
    }

    @Test
    @DisplayName("Tambah buku gagal ketika buku sudah ada")
    void testTambahBukuGagalBukuSudahAda() {
        when(mockRepositoryBuku.caribyIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        assertFalse(hasil, "Tidak boleh menambah buku yang sudah ada");
        verify(mockRepositoryBuku).caribyIsbn("1234567890");
        verify(mockRepositoryBuku, never()).simpan(any(Buku.class));
    }

    @Test
    @DisplayName("Tambah buku gagal ketika data tidak valid")
    void testTambahBukuGagalDataTidakValid() {
        Buku bukuTidakValid = new Buku("123", "", "", 0, -100.0);

        boolean hasil = servicePerpustakaan.tambahBuku(bukuTidakValid);

        assertFalse(hasil, "Tidak boleh menambah buku dengan data tidak valid");
        verifyNoInteractions(mockRepositoryBuku);
    }

    @Test
    @DisplayName("Hapus buku berhasil ketika tidak ada yang dipinjam")
    void testHapusBukuBerhasil() {
        bukuTest.setJumlahTersedia(5);
        when(mockRepositoryBuku.caribyIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        when(mockRepositoryBuku.hapus("1234567890")).thenReturn(true);

        boolean hasil = servicePerpustakaan.hapusBuku("1234567890");

        assertTrue(hasil, "Harus berhasil menghapus buku");
        verify(mockRepositoryBuku).caribyIsbn("1234567890");
        verify(mockRepositoryBuku).hapus("1234567890");
    }

    @Test
    @DisplayName("Hapus buku gagal ketika ada yang dipinjam")
    void testHapusBukuGagalAdaYangDipinjam() {
        bukuTest.setJumlahTersedia(3);
        when(mockRepositoryBuku.caribyIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        boolean hasil = servicePerpustakaan.hapusBuku("1234567890");

        assertFalse(hasil, "Tidak boleh menghapus buku yang sedang dipinjam");
        verify(mockRepositoryBuku).caribyIsbn("1234567890");
        verify(mockRepositoryBuku, never()).hapus(anyString());
    }

    @Test
    @DisplayName("Cari buku by ISBN berhasil")
    void testCariBukuByIsbnBerhasil() {
        when(mockRepositoryBuku.caribyIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        Optional<Buku> hasil = servicePerpustakaan.cariBukuByIsbn("1234567890");

        assertTrue(hasil.isPresent(), "Harus menemukan buku");
        assertEquals("Pemrograman Java", hasil.get().getJudul());
        verify(mockRepositoryBuku).caribyIsbn("1234567890");
    }

    @Test
    @DisplayName("Cari buku by judul berhasil")
    void testCariBukuByJudul() {
        List<Buku> daftarBuku = Arrays.asList(bukuTest);
        when(mockRepositoryBuku.caribyJudul("Java")).thenReturn(daftarBuku);

        List<Buku> hasil = servicePerpustakaan.cariBukuByJudul("Java");

        assertEquals(1, hasil.size());
        assertEquals("Pemrograman Java", hasil.get(0).getJudul());
        verify(mockRepositoryBuku).caribyJudul("Java");
    }

    // ... (lanjutkan test lainnya, pola sama tinggal ganti caribyIsbn -> cariByIsbn)
}
