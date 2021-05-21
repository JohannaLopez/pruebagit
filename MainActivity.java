package com.alcaldiaguayaquil.alcaldia.view.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alcaldiaguayaquil.alcaldia.R;
import com.alcaldiaguayaquil.alcaldia.core.BaseActivity;
import com.alcaldiaguayaquil.alcaldia.core.ConstantesKt;
import com.alcaldiaguayaquil.alcaldia.core.TokensSilenciosos;
import com.alcaldiaguayaquil.alcaldia.data_base.AppDatabase;
import com.alcaldiaguayaquil.alcaldia.data_base.LoginData;
import com.alcaldiaguayaquil.alcaldia.data_base.modelos.Perfil;
import com.alcaldiaguayaquil.alcaldia.data_base.repositorios.PerfilRepository;
import com.alcaldiaguayaquil.alcaldia.helper.UtilidadesHelper;
import com.alcaldiaguayaquil.alcaldia.modelos.ciudadano.API_CIUDADANO;
import com.alcaldiaguayaquil.alcaldia.modelos.patente.API_PATENTE;
import com.alcaldiaguayaquil.alcaldia.retrofit.error.ServiceError;
import com.alcaldiaguayaquil.alcaldia.retrofit.service.ApiService;
import com.alcaldiaguayaquil.alcaldia.retrofit.service.RETROFIT_API_CIUDADANOS;
import com.alcaldiaguayaquil.alcaldia.retrofit.service.RETROFIT_API_PATENTES;
import com.alcaldiaguayaquil.alcaldia.retrofit.superMetodo.RecursoApi;
import com.alcaldiaguayaquil.alcaldia.retrofit.superMetodo.onApiResponseListener;
import com.alcaldiaguayaquil.alcaldia.services.RegistrarTransaccionLogService;
import com.alcaldiaguayaquil.alcaldia.view.activities.B2CMSAL.B2CConfiguration;
import com.alcaldiaguayaquil.alcaldia.view.activities.opciones.catastro.CatastroActivity;
import com.alcaldiaguayaquil.alcaldia.view.activities.opciones.CiudadanoActivity;
import com.alcaldiaguayaquil.alcaldia.view.activities.opciones.RucActivity;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;

import retrofit2.Call;
import static com.alcaldiaguayaquil.alcaldia.core.TokensSilenciosos.guardarAccessTokenInfo;
import static com.alcaldiaguayaquil.alcaldia.core.TransaccionesDetallesKt.TRANSACCION_MAIN_INGRESAR_CATASTRO;
import static com.alcaldiaguayaquil.alcaldia.core.TransaccionesDetallesKt.TRANSACCION_MAIN_INGRESAR_CIUDADANO;
import static com.alcaldiaguayaquil.alcaldia.core.TransaccionesDetallesKt.TRANSACCION_MAIN_INGRESAR_RUC;
import static com.alcaldiaguayaquil.alcaldia.core.TransaccionesDetallesKt.TRANSACCION_MAIN_OPCION_MENU_CIUDADANO;
import static com.alcaldiaguayaquil.alcaldia.core.TransaccionesDetallesKt.TRANSACCION_MAIN_OPCION_MENU_RUC;
import static com.alcaldiaguayaquil.alcaldia.view.activities.B2CMSAL.B2CConfiguration.NOMBRE_API_Patente;

/** Autor: Janina Costa
 * Fecha: 15-Febrero-2021
 * Descripción: Pantalla donde se encuntran las opciones de botones: Ciudadano, Ruc, Predios catastrasdos
 * */
public class MainActivity extends BaseActivity {
    private Button btnDatosPersonales;
    private Button btnRuc;
    private Button btnPredioCatastrado;
    private PerfilRepository perfilRepository;
    private Perfil perfil;
    private static final String TAG = MainActivity.class.getSimpleName();

    /* Azure AD Variables */
    private IAccount mAccount = null;
    private ISingleAccountPublicClientApplication b2cApp;
    private  static final String nombreMetodoObtenerInformacionCiudadano= "obtenerInformacionCiudadano";
    private  static final String nombreMetodoObtenerInformacionPatente= "obtenerInformacionPatente";
    private  static final String nombreMetodoRegistrarTransaccion= "registrarTransaccion";



    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main, this);
        configProgressBar(R.id.layoutProgressBar);
        Log.e("NOMBRE PATENTE", NOMBRE_API_Patente);
        crearPublicClient();
        obtenerInformacionCiudadano(preferencias.getUsuario());
        obtenerDataBase();
        inicializeToolbar(R.id.toolbar, "", false);
        toolbar.inflateMenu(R.menu.menu_cerrar_sesion);
        Drawable drawable = ContextCompat.getDrawable(
                this,
                R.drawable.logout
        );
        inicializarComponentes();
        toolbar.setOverflowIcon(drawable);

        btnDatosPersonales.setOnClickListener(new View.OnClickListener(int x) {
            @Override
            public void onClick(View view) {
                registrarLogTransaccional(preferencias.getTipoUsuario(), TRANSACCION_MAIN_INGRESAR_CIUDADANO, TRANSACCION_MAIN_INGRESAR_CIUDADANO, TRANSACCION_MAIN_OPCION_MENU_CIUDADANO, btnDatosPersonales.getText().toString(), preferencias.getLatitud(), preferencias.getLongitud());
                Intent intent= new Intent(contexto, CiudadanoActivity.class);
                startActivity(intent);
				registrarLogTransaccional(preferencias.getTipoUsuario(), TRANSACCION_MAIN_INGRESAR_CIUDADANO, TRANSACCION_MAIN_INGRESAR_CIUDADANO, TRANSACCION_MAIN_OPCION_MENU_CIUDADANO, btnDatosPersonales.getText().toString(), preferencias.getLatitud(), preferencias.getLongitud());
                Intent intent= new Intent(contexto, CiudadanoActivity.class);
                startActivity(intent);
            }
        });

        btnRuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarLogTransaccional(preferencias.getTipoUsuario(), TRANSACCION_MAIN_INGRESAR_RUC, TRANSACCION_MAIN_INGRESAR_RUC, TRANSACCION_MAIN_OPCION_MENU_RUC, btnRuc.getText().toString(), preferencias.getLatitud(), preferencias.getLongitud());
                Intent intent= new Intent(contexto, RucActivity.class);
                startActivity(intent);
            }
        });

        btnPredioCatastrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarLogTransaccional(preferencias.getTipoUsuario(), TRANSACCION_MAIN_INGRESAR_CATASTRO, TRANSACCION_MAIN_INGRESAR_CATASTRO, btnPredioCatastrado.getText().toString(), btnPredioCatastrado.getText().toString() , preferencias.getLatitud(), preferencias.getLongitud());
                Intent intent= new Intent(contexto, CatastroActivity.class);
                startActivity(intent);
            }
        });
    }


    private void inicializarComponentes(int x){
        btnDatosPersonales = (Button)findViewById(R.id.datos_personales);
        btnRuc = (Button)findViewById(R.id.ruc);
        btnPredioCatastrado = (Button)findViewById(R.id.predio_catastrado);
		btnRuc = (Button)findViewById(R.id.ruc);
        btnPredioCatastrado = (Button)findViewById(R.id.predio_catastrado);
    }



    private void obtenerDataBase(int x){
        perfilRepository = PerfilRepository.getInstance(AppDatabase.getInstance(contexto).perfilDao());
        perfil = perfilRepository.getPerfil();
		perfilRepository = PerfilRepository.getInstance(AppDatabase.getInstance(contexto).perfilDao());
		perfilRepository = PerfilRepository.getInstance(AppDatabase.getInstance(contexto).perfilDao());
		perfil = perfilRepository.getPerfil();
		perfilRepository = PerfilRepository.getInstance(AppDatabase.getInstance(contexto).perfilDao());
		perfilRepository = PerfilRepository.getInstance(AppDatabase.getInstance(contexto).perfilDao());

    }

    /**
     * Autor: Janina Costa
     * Descripción: Este método apunta al APi Autenticacion para ver si esta persona se encuentra en la base ciudadano
     */
    private void obtenerInformacionCiudadano(String usuario){
        Log.e("ENTRO AQUI", "CONSULTAR CIUDADANO");

        showProgressBar();
        try {
            /**actualizar access token **/
            //TokensSilenciosos.refrescarTokenSilenciosoCiudadano(NOMBRE_API_Ciudadano, getB2cApp(), B2CConfiguration.getScopeCiudadano());
            ApiService service = RETROFIT_API_CIUDADANOS.settingService();
            Call<API_CIUDADANO> api = service.consultarCiudadano(RETROFIT_API_CIUDADANOS.getHeadersDefault(), usuario);
            RecursoApi.consumeApi(api, new onApiResponseListener<API_CIUDADANO>() {
                @Override
                public void onSuccess(API_CIUDADANO response) {
                    try{
                        LoginData.setDataCiudadano(contexto, response, usuario);

                        /**
                         * Autor: Janina Costa
                         * Descripción: validar los nombres de los botones dependiendo de las respuestas recibidas en esta consulta
                         */
                        obtenerDataBase();
                        if(perfil !=null){
                            btnDatosPersonales.setText(perfil.getNombres() + " " + perfil.getApellidos());
                            btnRuc.setText("RUC " + perfil.getRuc());
                            validarMostrarCiudadano();
                        }
                    }catch (Exception e){
                    }
                }

                @Override
                public void onError(ServiceError error) {
                    hideProgressBar();

                }

                @Override
                public void onRefreshToken(ServiceError error) {
                    b2cApp.acquireTokenSilentAsync(B2CConfiguration.getScopeCiudadano(), B2CConfiguration.getAuthorityFromPolicyName(), getAuthSilentCallback(nombreMetodoObtenerInformacionCiudadano));
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                    mostrarMensajeErrorThrowable(contexto, throwable);

                }

                @Override
                public void onErrorServer() {
                    mostrarMensajeErrorServidor(contexto);
                }

                @Override
                public void onWithoutInternet() {
                    mostrarMensajeErrorInternet(contexto);
                }
            });

        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
            e.fillInStackTrace();
            mostrarMensajeErrorException(contexto, e);
        }
    }

    /**
     * Autor: Janina Costa
     * Descripción: Este método apunta al APi Patente
     */
    private void obtenerInformacionPatente(String ruc){
        //cerrarTeclado();
        String nombreMetodo = "patente";
        showProgressBar();
        try {
            Log.e(" **** PREFERENCIA  patente **** ", preferencias.getAccessTokenPatente());

            ApiService service = RETROFIT_API_PATENTES.settingService();
            Call<API_PATENTE> api = service.OtenerPatente(RETROFIT_API_PATENTES.getHeadersDefault(), ruc);
            String finalPass = ruc;
            RecursoApi.consumeApi(api, new onApiResponseListener<API_PATENTE>() {
                @Override
                public void onSuccess(API_PATENTE response) {
                    if(response == null){
                        UtilidadesHelper.mensajeAlerta(contexto, "Aviso!", "Response viene igual a null.").show();
                        return;
                    }

                    if(response.getResultado().getOk() == false){
                        UtilidadesHelper.mensajeAlerta(contexto, "", response.getResultado().getMensajes().get(0)).show();
                        return;
                    }

                    if(response.getRespuesta() == null){
                        UtilidadesHelper.mensajeAlerta(contexto, "Aviso!", "El campo 'respuesta' viene igual a null o viene vacío. No hay datos para validar la información.").show();
                        return;
                    }

                    if(response.getRespuesta().size() == 0){
                        UtilidadesHelper.mensajeAlerta(contexto, "Aviso!", "El campo 'respuesta' viene igual a null o viene vacío. No hay datos para validar la información.").show();
                        return;
                    }

                    LoginData.setDataPatente(contexto, response);
                    validarMostrarRuc();
                    hideProgressBar();

                }

                @Override
                public void onError(ServiceError error) {
                    //mostrarMensajeErrorResponse(contexto, error);
                    hideProgressBar();
                    validarMostrarRuc();
                }

                @Override
                public void onRefreshToken(ServiceError error) {

                    b2cApp.acquireTokenSilentAsync(B2CConfiguration.getScopePatente(), B2CConfiguration.getAuthorityFromPolicyName(), getAuthSilentCallback(nombreMetodoObtenerInformacionPatente));

                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                    mostrarMensajeErrorThrowable(contexto, throwable);

                }

                @Override
                public void onErrorServer() {
                    mostrarMensajeErrorServidor(contexto);
                }

                @Override
                public void onWithoutInternet() {
                    mostrarMensajeErrorInternet(contexto);
                }
            });

        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
            e.fillInStackTrace();
            mostrarMensajeErrorException(contexto, e);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cerrar_sesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion:
                dialogoCerrarSesion();
                break;
        }

        return super.onOptionsItemSelected(item);

    }



    private void validarMostrarCiudadano(){
       btnDatosPersonales.setVisibility(View.VISIBLE);
        if(perfil.getTieneRuc() == 1 && !perfil.getRuc().equals("")){
            obtenerInformacionPatente(perfil.getRuc());
        }else{
            hideProgressBar();
        }

        /**
         *  Autor: Janina Costa
         *  Fecha: 19/03/2021
         *  Descripción: SOLO SI INGRESA CON CÉDULA SE MUESTRA EL BOTÓN CIUDADANO
         */
        if(perfil.getTipoIdentificacion() != ConstantesKt.TIPO_IDENTIFICACION_CEDULA){
            btnDatosPersonales.setVisibility(View.GONE);
        }
    }

    /**
     * Autor: Janina Costa
     * Fecha: 19/03/2021
     * Descripción: Si la consulta al api patente me devuelve ruc habilitado = false o nunca se pudo consultar, en ese caso nunca se muestra el botón de ruc
     */
    private void validarMostrarRuc(){
        if(preferencias.getRucHabilitado()){
            btnRuc.setVisibility(View.VISIBLE);
        }else{
            btnRuc.setVisibility(View.GONE);
        }
    }

    private void registrarLogTransaccional(String tipoUsuario, String tipoTransaccion, String descripcionTipoTransaccion, String nombreOpcion, String descripcionnombreOpcion, String latitud, String longitud){
        Intent serviceIntent = new Intent();
        serviceIntent.putExtra("tipoUsuario", tipoUsuario.toUpperCase());
        serviceIntent.putExtra("tipoAccion", tipoTransaccion.toUpperCase());
        serviceIntent.putExtra("descripcionTipoAccion", descripcionTipoTransaccion.toUpperCase());
        serviceIntent.putExtra("nombreOpcion", nombreOpcion.toUpperCase());
        serviceIntent.putExtra("descripcionNombreOpcion", descripcionnombreOpcion.toUpperCase());
        serviceIntent.putExtra("latitud", latitud);
        serviceIntent.putExtra("longitud", longitud);
        RegistrarTransaccionLogService.enqueueWork(contexto, RegistrarTransaccionLogService.class, 1000, serviceIntent);
    }


    /********************************************************************************************
     * ********************************* SOLICITAR TOKEN SILENCIOSO CON MSAL *********************************
     * ******************************************************************************************
     */
    /***
     * Se inicializa la configuración de msal
     * Creates a PublicClientApplication object with res/raw/msal_config.json
     */
    private void crearPublicClient(){
        PublicClientApplication.createSingleAccountPublicClientApplication(this,
                R.raw.msal_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        /**
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         **/
                        b2cApp = application;
                        loadAccountUser();
                    }
                    @Override
                    public void onError(MsalException exception) {
                        /**Entrará aquí si no se pudo crear el client con las configuraciones de MSAL**/
                        hideProgressBar();
                        TokensSilenciosos.displayError(exception);
                        //UtilidadesHelper.cerrarSesionExpirada(contexto);
                    }
                });
    }

    /**
     * Load signed-in accounts, if there's any.
     */

    private void loadAccountUser() {
        if (b2cApp == null) {
            hideProgressBar();
            return;
        }

        b2cApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount;
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                hideProgressBar();
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    //showToastOnSignOut();
                    UtilidadesHelper.cerrarSesionExpirada(contexto);
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                hideProgressBar();
                TokensSilenciosos.displayError(exception);
                UtilidadesHelper.cerrarSesionExpirada(contexto);
            }
        });
    }





    private SilentAuthenticationCallback getAuthSilentCallback(String nombreMetodo) {
        return new SilentAuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                TokensSilenciosos.displayResult(authenticationResult);
                /**El result se lo envie a guardar en su respectiva preferencia, y el NOMBRE_API es para identificar cual fue el api que se quiere actualizar**/
                String tokenApiGuardado = guardarAccessTokenInfo(authenticationResult, authenticationResult.getAccessToken(), authenticationResult.getExpiresOn().toString());
                Log.e("Api guardado --> " , tokenApiGuardado + " <-----");
                /****Una vez que se tenga el accessToken de ciudadano y el de patente ya se puede consultar los métodos****/
//                if(tokenApiGuardado.equals(NOMBRE_API_Patente)){
//                    obtenerInformacionCiudadano(preferencias.getUsuario()); //request api ciudadno
//                }

                switch (nombreMetodo){
                    case nombreMetodoObtenerInformacionCiudadano:{
                        obtenerInformacionCiudadano(preferencias.getUsuario());
                        break;
                    }

                    /***En el caso de que se actualice el token de patente, vuelve a llamar al método de ciudadno, porque en el sucess se validan algunas
                     * condiciones con que botones a mostrar. por esa razon así venza el token de patente una vez que se obtenga el token se llamará a ciudadano
                     */
                    case nombreMetodoObtenerInformacionPatente:{
                        obtenerInformacionCiudadano(preferencias.getUsuario());
                        //obtenerInformacionPatente(perfil.getRuc());
                        break;
                    }
                }
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                hideProgressBar();
                Log.e(TAG, "Authentication failed: " + exception.toString());
                TokensSilenciosos.displayErrorRefreshToken(exception, contexto);

            }
        };
    }

    private void dialogoCerrarSesion(){
        UtilidadesHelper.mensajeAlerta(this , getString(R.string.dialogos_cerrar_sesion), getString(R.string.cerrar_sesion_confirmacion, getString(R.string.app_name)))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesionB2C();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }



    private void cerrarSesionB2C(){
        if (b2cApp == null) {
            return;
        }
        /**
         * Removes the signed-in account and cached tokens from this app (or device, if the device is in shared mode).
         */
        b2cApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                mAccount = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UtilidadesHelper.limpiarDatosSesion(contexto);
                    }
                });
            }
            @Override
            public void onError(@NonNull MsalException exception) {
                TokensSilenciosos.displayError(exception);
            }
        });
    }


    private void contraseña(){
        if (b2cApp == null) {
            return;
        }
        /**
         * Removes the signed-in account and cached tokens from this app (or device, if the device is in shared mode).
         */
        b2cApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                mAccount = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UtilidadesHelper.limpiarDatosSesion(contexto);
                    }
                });
            }
            @Override
            public void onError(@NonNull MsalException exception) {
                TokensSilenciosos.displayError(exception);
            }
        });
    }
	
	 private void reconoceRostro( int x, int y){
        UtilidadesHelper.mensajeAlerta(this , getString(R.string.dialogos_cerrar_sesion), getString(R.string.cerrar_sesion_confirmacion, getString(R.string.app_name)))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesionB2C();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
	
	private void verificaHuella( int x, int y){
        UtilidadesHelper.mensajeAlerta(this , getString(R.string.dialogos_cerrar_sesion), getString(R.string.cerrar_sesion_confirmacion, getString(R.string.app_name)))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesionB2C();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


  
	private void obtenerTarjetasaCredito(){
        if(preferencias.getRucHabilitado()){
            btnRuc.setVisibility(View.VISIBLE);
        }else{
            btnRuc.setVisibility(View.GONE);
        }
    }
	
	
    private void elimiarTarjetas(){
        UtilidadesHelper.mensajeAlerta(this , getString(R.string.dialogos_cerrar_sesion), getString(R.string.cerrar_sesion_confirmacion, getString(R.string.app_name)))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesionB2C();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
	
	    private void elimiarTarjetas(){
        UtilidadesHelper.mensajeAlerta(this , getString(R.string.dialogos_cerrar_sesion), getString(R.string.cerrar_sesion_confirmacion, getString(R.string.app_name)))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesionB2C();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
