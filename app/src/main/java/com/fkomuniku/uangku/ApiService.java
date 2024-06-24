package com.fkomuniku.uangku;

import com.fkomuniku.uangku.model.ApiResponse;
import com.fkomuniku.uangku.model.DaftarResponse;
import com.fkomuniku.uangku.model.KategoriResponse;
import com.fkomuniku.uangku.model.KeuanganInfoResponse;
import com.fkomuniku.uangku.model.KeuanganResponse;
import com.fkomuniku.uangku.model.LoginRequest;
import com.fkomuniku.uangku.model.LoginResponse;
import com.fkomuniku.uangku.model.DaftarRequest;
import com.fkomuniku.uangku.model.RekeningResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("daftar")
    Call<DaftarResponse> registerUser(@Body DaftarRequest registerRequest);

    @GET("rekening")
    Call<RekeningResponse> getRekening();

    @FormUrlEncoded
    @POST("rekening/add")
    Call<Void> addRekening(@Field("rekening") String rekening);

    @FormUrlEncoded
    @POST("rekening/update")
    Call<Void> updateRekening(@Field("id") String id, @Field("rekening") String rekening);

    @DELETE("rekening/{id}")
    Call<Void> deleteRekening(@Path("id") String id);

    @GET("kategori")
    Call<KategoriResponse> getKategori();

    @FormUrlEncoded
    @POST("kategori/add")
    Call<ResponseBody> addKategori(@Field("kategori") String kategori, @Field("jenis") String jenis);

    @FormUrlEncoded
    @POST("kategori/update")
    Call<ResponseBody> updateKategori(@Field("id") String id, @Field("kategori") String kategori, @Field("jenis") String jenis);

    @DELETE("kategori/{id}")
    Call<ResponseBody> deleteKategori(@Path("id") String id);

    @GET("keuangan/info")
    Call<KeuanganInfoResponse> getKeuanganInfo();

    @GET("keuangan")
    Call<KeuanganResponse> getKeuangan();

    @GET("keuangan/kategori/{jenis}")
    Call<KategoriResponse> getKeuanganKategori(@Path("jenis") String jenis);

    @FormUrlEncoded
    @POST("keuangan/add")
    Call<KeuanganResponse> addKeuangan(@Field("kategori") String kategori, @Field("jenis") String jenis, @Field("nominal") int nominal, @Field("tanggal") String tanggal, @Field("keterangan") String keterangan, @Field("rekening") String rekening);

    @FormUrlEncoded
    @POST("keuangan/update")
    Call<ApiResponse> updateKeuangan(@Field("id") String id, @Field("jenis") String jenis, @Field("kategori") String kategori, @Field("old_nominal") int old_nominal, @Field("nominal") String nominal, @Field("keterangan") String keterangan, @Field("tanggal") String tanggal, @Field("rekening") String rekening);

    @DELETE("keuangan/{id}")
    Call<ApiResponse> deleteKeuangan(@Path("id") String id);

}
