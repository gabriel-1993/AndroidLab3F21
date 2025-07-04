package com.gabrielt.f21.request;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import com.gabrielt.f21.model.CancelarReservaView;
import com.gabrielt.f21.model.ConfirmarNuevaReservaView;
import com.gabrielt.f21.model.CuotaActualView;
import com.gabrielt.f21.model.MisCreditosView;
import com.gabrielt.f21.model.MisReservasView;
import com.gabrielt.f21.model.NovedadView;
import com.gabrielt.f21.model.NuevaReservaView;
import com.gabrielt.f21.model.ProximaReservaView;
import com.gabrielt.f21.model.Usuario;
import com.gabrielt.f21.model.UsuarioPerfilView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public class ApiClient {

    // JD:
    //public static final String URLBASE = "http://192.168.0.11:5001/api/";
    //Ger:
    //public static final String URLBASE = "http://192.168.0.2:5001/api/";
    //LP
    public static final String URLBASE = "http://192.168.1.3:5001/api/";

    private static SharedPreferences sp;

    public static F21Service getApiF21() {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(F21Service.class);
    }

    private static SharedPreferences getSharedPreference(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("usuario", 0);
        }
        return sp;
    }

    public static void guardar(Context context, String token) {
        SharedPreferences sp = getSharedPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String leer(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        return sp.getString("token", null);
    }



    public interface F21Service {

        //USUARIO
        @FormUrlEncoded
        @POST("usuarios/registrarse")
        Call<String> registrarse(
                @Field("FechaNacimiento")String fechaNacimiento,
                @Field("Nombre") String nombre,
                @Field("Apellido") String apellido,
                @Field("Email") String email,
                @Field("Telefono") String telefono,
                @Field("Pass") String password
        );

        @FormUrlEncoded
        @POST("usuarios/login")
        Call<String> login(
                @Field("Email") String email,
                @Field("Pass") String pass
        );

        @FormUrlEncoded
        @POST("usuarios/restablecer-clave")
        Call<String> restablecerClave(@Field("email") String email);

        @GET("usuarios/get-perfil")
        Call<Usuario> getPerfilUsuario(@Header("Authorization") String token);

        @FormUrlEncoded
        @PUT("usuarios/modificar-perfil")
        Call<String> modificarPerfil(
                @Header("Authorization") String token,
                @Field("FechaNacimiento") String fechaNacimientoISO8601,
                @Field("Nombre") String nombre,
                @Field("Apellido") String apellido,
                @Field("Telefono") String telefono,
                @Field("Email") String email
        );


        @Multipart
        @PUT("usuarios/modificar-avatar")
        Call<ResponseBody> modificarAvatar(
                @Header("Authorization") String token,
                @Part MultipartBody.Part avatarFile
        );

        @FormUrlEncoded
        @PUT("usuarios/modificar-password")
        Call<String> modificarPassword(
                @Header("Authorization") String token,
                @Field("PassActual") String passActual,
                @Field("PassNueva") String passNueva,
                @Field("ConfirmarPass") String confirmarPass
        );


        //Mis creditos/cuota
        //consultar todas las cuotas con los creditos y reservas que no esten canceladas, para calcular creditos usados.
        @GET("cuotas/obtener-cuotas")
        Call<MisCreditosView> misCreditos(@Header("Authorization") String token);


        //Al iniciar app: verificar si tiene cuota en curso o esta vencida.
        //En curso, se muestra en inicio creditos disponibles y vencimiento.
        //Vencida, setea creditos disponibles en 0 y en inicio se avisa que no hay creditos.
        @GET("cuotas/obtener-cuota-actual")
        Call<CuotaActualView> obtenerCuotaActual(@Header("Authorization") String token);


        //Mis reservas
        //Mis reservas : obtener todas las reservas del usuario logeado
        @GET("reservas/obtener-reservas")
        Call<MisReservasView> obtenerReservas(@Header("Authorization") String token);

        //proxima reserva para mostrar en inicio
        @GET("reservas/proxima-reserva")
        Call<ProximaReservaView> obtenerProximaReserva(@Header("Authorization") String token);

        @GET("reservas/obtener-lista-nueva-reserva")
        Call<NuevaReservaView> obtenerListaNuevaReserva(@Header("Authorization") String token);

        //crear reserva
        @FormUrlEncoded
        @POST("reservas/nueva-reserva")
        Call<ConfirmarNuevaReservaView> nuevaReserva(
                @Header("Authorization") String token,
                @Field("ClaseHorarioId") int claseHorarioId
        );

        @POST("reservas/cancelar-reserva")
        @FormUrlEncoded
        Call<CancelarReservaView> cancelarReserva(
                @Header("Authorization") String authorization,
                @Field("ReservaId") int reservaId
        );

        @GET("novedades/get-novedades")
        Call<List<NovedadView>> getNovedades();

    }
}
