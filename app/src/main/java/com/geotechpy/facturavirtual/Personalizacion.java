package com.geotechpy.facturavirtual;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Personalizacion extends AppCompatActivity {

    private EditText etRUC, etDV, etNombre, etNombreFantasia, etEstablecimiento, etDireccion, etTelefono, etTimbrado, etPuntoExpedicion, etVigencia, etLicencia, etUsuario, etContrasena, etFchVencimientoLicencia;
    private TextInputLayout etRUCLayout, etDVLayout, etEstablecimientoLayout, etNombreLayout, etTimbradoLayout, etPuntoExpedicionLayout, etVigenciaLayout, etLicenciaLayout, etUsuarioLayout, etContrasenaLayout;
    private Button btnObtener, btnAceptar, btnCancelar, btnEliminar, btnObtenerSerial;
    private Switch swRecordar;
    ProgressDialog progressDialog;
    public static final String CONTRIBUYENTEPREF = "Contribuyente";
    SharedPreferences sharedPreferences;
    private String android_id = "";
    android.content.ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizacion);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        progressDialog = new ProgressDialog(this);

        etRUC = findViewById(R.id.etRUC);
        etRUC.setMaxLines(1);
        etRUC.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etRUCLayout = findViewById(R.id.etRUCLayout);

        etDV = findViewById(R.id.etDV);
        etDV.setMaxLines(1);
        etDV.setEnabled(false);
        etDVLayout = findViewById(R.id.etDVLayout);

        etEstablecimiento = findViewById(R.id.etEstablecimiento);
        etEstablecimiento.setMaxLines(1);
        etEstablecimientoLayout = findViewById(R.id.etEstablecimientoLayout);


        etNombre = findViewById(R.id.etNombre);
        etNombre.setEnabled(false);
        etNombreLayout = findViewById(R.id.etNombreLayout);

        etNombreFantasia = findViewById(R.id.etNombreFantasia);
        etNombreFantasia.setEnabled(false);

        etDireccion = findViewById(R.id.etDireccion);
        etDireccion.setEnabled(false);

        etTelefono = findViewById(R.id.etTelefono);
        etTelefono.setEnabled(false);

        etTimbrado = findViewById(R.id.etTimbrado);
        etTimbrado.setMaxLines(1);
        etTimbrado.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etTimbradoLayout = findViewById(R.id.etTimbradoLayout);

        etPuntoExpedicion = findViewById(R.id.etPuntoExpedicion);
        etPuntoExpedicion.setMaxLines(1);
        etPuntoExpedicion.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etPuntoExpedicionLayout = findViewById(R.id.etPuntoExpedicionLayout);

        etVigencia = findViewById(R.id.etVigencia);
        etVigencia.setMaxLines(1);
        etVigenciaLayout = findViewById(R.id.etVigenciaLayout);

        etLicencia = findViewById(R.id.etLicencia);
        etLicencia.setMaxLines(1);
        etLicenciaLayout = findViewById(R.id.etLicenciaLayout);

        etFchVencimientoLicencia = findViewById(R.id.etFchVencimientoLicencia);

        btnObtenerSerial = findViewById(R.id.btnObtenerSerial);
        btnObtenerSerial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
                android_id = sharedPreferences.getString("TESAKA_INSTALL_ID","");

                ClipData clipData= ClipData.newPlainText("ANDROID_ID",android_id);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Numero de Serie copiado al portapepeles", Toast.LENGTH_SHORT).show();


            }

        });

        btnObtener = findViewById(R.id.btnObtener);
        btnObtener.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openLoginDialog();
            }
        });

        btnAceptar = findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fnValidarFormulario();
            }
        });

        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personalizacion.this, MainActivity.class);
                startActivity(intent);
            }
        });


        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        String ruc = sharedPreferences.getString("etRuc","");

        if (!ruc.equals("")) {
            String nombre = sharedPreferences.getString("etNombre", "");
            String nombreFantasia = sharedPreferences.getString("etNombreFantasia", "");
            String dv = sharedPreferences.getString("etDV", "");
            String direccion = sharedPreferences.getString("etDireccion", "");
            String telefono = sharedPreferences.getString("etTelefono", "");
            String establecimiento = sharedPreferences.getString("etEstablecimiento", "");
            String timbrado = sharedPreferences.getString("etTimbrado", "");
            String puntoExpedicion = sharedPreferences.getString("etPuntoExpedicion", "");
            String fechaVigencia = sharedPreferences.getString("etVigencia", "");
            String licencia = sharedPreferences.getString("etLicencia", "");

            etRUC.setText(ruc);
            etEstablecimiento.setText(establecimiento);
            etDV.setText(dv);
            etNombre.setText(nombre);
            etNombreFantasia.setText(nombreFantasia);
            etDireccion.setText(direccion);
            etTelefono.setText(telefono);
            etTimbrado.setText(timbrado);
            etPuntoExpedicion.setText(puntoExpedicion);
            etVigencia.setText(fechaVigencia);
            etLicencia.setText(licencia);

            btnEliminar = findViewById(R.id.btnEliminar);
            btnEliminar.setVisibility(View.VISIBLE);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                }
            });



        }

        //Toast.makeText(getApplicationContext(), "On Create", Toast.LENGTH_SHORT).show();
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Eliminar")
                .setMessage("Estas segura(o) que desea eliminar?")
                .setIcon(R.drawable.ic_trash_alt)

                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        SharedPreferences.Editor spEditor = sharedPreferences.edit();
                        spEditor.putString("etRuc", "");
                        spEditor.putString("etDV", "");
                        spEditor.putString("etEstablecimiento", "");
                        spEditor.putString("etNombre", "");
                        spEditor.putString("etNombreFantasia", "");
                        spEditor.putString("etDireccion", "");
                        spEditor.putString("etTelefono", "");
                        spEditor.putString("etTimbrado", "");
                        spEditor.putString("etPuntoExpedicion", "");
                        spEditor.putString("etVigencia", "");
                        spEditor.putString("etLicencia", "");
                        spEditor.putString("etFchVencimientoLicencia", "");
                        spEditor.apply();

                        Toast.makeText(getApplicationContext(), "Eliminado exitosamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Personalizacion.this, MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Contribuyente eliminado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    public void openLoginDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Personalizacion.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        etUsuario = mView.findViewById(R.id.etUsuario);
        etContrasena = mView.findViewById(R.id.etContrasena);
        etUsuarioLayout = mView.findViewById(R.id.etUsuarioLayout);
        etContrasenaLayout = mView.findViewById(R.id.etContrasenaLayout);
        swRecordar = mView.findViewById(R.id.swRecordar);

        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        Boolean spRecordar = sharedPreferences.getBoolean("swRecordar",false);
        if (spRecordar){
            String spUsuario = sharedPreferences.getString("etUsuario","");
            String spContrasena = sharedPreferences.getString("etContrasena","");
            etUsuario.setText(spUsuario);
            etContrasena.setText(spContrasena);
            swRecordar.setChecked(true);
        }

        Button mBtnLogin = mView.findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fnValidarLogin(dialog);
            }
        });

        Button mBtnCancelar = mView.findViewById(R.id.btnCancelarLogin);
        mBtnCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialog.hide();
            }
        });

    }

    public void fnValidarLogin(final AlertDialog loginDialog) {
        if (!validarUsuario()){
            return;
        }
        if (!validarContrasena()){
            return;
        }
        etUsuarioLayout.setErrorEnabled(false);
        etContrasenaLayout.setErrorEnabled(false);

        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        if (swRecordar.isChecked()){
            spEditor.putString("etUsuario", etUsuario.getText().toString().trim());
            spEditor.putString("etContrasena", etContrasena.getText().toString().trim());
            spEditor.putBoolean("swRecordar", swRecordar.isChecked());
        } else {
            spEditor.putString("etUsuario", "");
            spEditor.putString("etContrasena", "");
            spEditor.putBoolean("swRecordar", false);
        }

        spEditor.apply();

        progressDialog.setMessage("Obteniendo informacion");
        RequestQueue requestQueue = Volley.newRequestQueue(Personalizacion.this);

        progressDialog.setMessage("Conectandose...");
        progressDialog.show();
        final String url = "https://marangatu.set.gov.py/eset-restful/contribuyentes/consultar?codigoEstablecimiento=1&ruc="+etRUC.getText().toString().trim();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        loginDialog.hide();
                        System.out.println("Response"+ response.toString());
                        Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                        try {
                            etDV.setText(response.get("dv").toString());
                            etNombre.setText(response.get("nombre").toString());
                            etNombreFantasia.setText( (response.isNull("nombreFantasia")) ? "" : response.get("nombreFantasia").toString() );
                            etDireccion.setText((response.isNull("direccion")) ? "" : response.get("direccion").toString());
                            etTelefono.setText((response.isNull("telefono")) ? "" : response.get("telefono").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        System.out.println("Response: " + error.getStackTrace().toString());
                        Toast.makeText(getApplicationContext(), "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    }
                } )
        {
          @Override
          public Map<String, String> getHeaders() throws AuthFailureError{
              String credenciales = etUsuario.getText().toString().trim()+":"+etContrasena.getText().toString().trim();
              //System.out.println("Credenciales: "+credenciales);
              String auth = "Basic " + Base64.encodeToString(credenciales.getBytes(),Base64.NO_WRAP);
              //System.out.println("Hash: "+auth);
              HashMap<String, String> headers = new HashMap<String, String>();
              headers.put("User-Agent","Tesaka APP Android");
              headers.put("authorization",auth);
              return headers;
          }
        } ;


        requestQueue.add(getRequest);

        //Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
    }

    private boolean validarUsuario(){
        if (etUsuario.getText().toString().trim().isEmpty()) {
            etUsuarioLayout.setErrorEnabled(true);
            etUsuarioLayout.setError("Ingresar Usuario");
            //etDV.setError("Obligatorio");
            return false;
        }
        etUsuarioLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarContrasena(){
        if (etContrasena.getText().toString().trim().isEmpty()) {
            etContrasenaLayout.setErrorEnabled(true);
            etContrasenaLayout.setError("Ingresar Password");
            //etDV.setError("Obligatorio");
            return false;
        }
        etContrasenaLayout.setErrorEnabled(false);
        return true;
    }

    private void fnValidarFormulario(){

        if (!validarRUC()){
            return;
        }
        if (!validarDV()){
            return;
        }
        if (!validarEstablecimiento()){
            return;
        }
        if (!validarNombre()){
            return;
        }
        if (!validarTimbrado()){
            return;
        }
        if (!validarPuntoExpedicion()){
            return;
        }
        if (!validarFechaVigencia()){
            return;
        }
        if (!validarLicencia()){
            return;
        }

        etRUCLayout.setErrorEnabled(false);
        etDVLayout.setErrorEnabled(false);
        etEstablecimientoLayout.setErrorEnabled(false);
        etNombreLayout.setErrorEnabled(false);
        etTimbradoLayout.setErrorEnabled(false);
        etPuntoExpedicionLayout.setErrorEnabled(false);
        etVigenciaLayout.setErrorEnabled(false);
        etLicenciaLayout.setErrorEnabled(false);

        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString("etRuc", etRUC.getText().toString().trim());
        spEditor.putString("etDV", etDV.getText().toString().trim());
        spEditor.putString("etEstablecimiento", etEstablecimiento.getText().toString().trim());
        spEditor.putString("etNombre", etNombre.getText().toString().trim());
        spEditor.putString("etNombreFantasia", etNombreFantasia.getText().toString().trim());
        spEditor.putString("etDireccion", etDireccion.getText().toString().trim());
        spEditor.putString("etTelefono", etTelefono.getText().toString().trim());
        spEditor.putString("etTimbrado", etTimbrado.getText().toString().trim());
        spEditor.putString("etPuntoExpedicion", etPuntoExpedicion.getText().toString().trim());
        spEditor.putString("etVigencia", etVigencia.getText().toString().trim());
        spEditor.putString("etLicencia", etLicencia.getText().toString().trim());
        spEditor.putString("etFchVencimientoLicencia", etFchVencimientoLicencia.getText().toString().trim());
        spEditor.apply();

        Toast.makeText(getApplicationContext(), "Registrado exitosamente", Toast.LENGTH_SHORT).show();

        ClipData clipData= ClipData.newPlainText("","");
        clipboardManager.setPrimaryClip(clipData);

        Intent intent = new Intent(Personalizacion.this, MainActivity.class);
        startActivity(intent);

    }

    private boolean validarRUC(){
        if (etRUC.getText().toString().trim().isEmpty()) {
            etRUCLayout.setErrorEnabled(true);
            etRUCLayout.setError("Ingresar RUC");
            //etRUC.setError("Obligatorio");
            return false;
        }
        etRUCLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarDV(){
        if (etDV.getText().toString().trim().isEmpty()) {
            etDVLayout.setErrorEnabled(true);
            etDVLayout.setError("Ingresar DV");
            //etDV.setError("Obligatorio");
            return false;
        }
        etDVLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarEstablecimiento(){
        if (etEstablecimiento.getText().toString().trim().isEmpty()) {
            etEstablecimientoLayout.setErrorEnabled(true);
            etEstablecimientoLayout.setError("Requerido");
            //etEstablecimiento.setError("Obligatorio");
            return false;
        }
        etEstablecimientoLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarNombre(){
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombreLayout.setErrorEnabled(true);
            etNombreLayout.setError("Requerido");
            //etNombre.setError("Obligatorio");
            return false;
        }
        etNombreLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarTimbrado(){
        if (etTimbrado.getText().toString().trim().isEmpty()) {
            etTimbradoLayout.setErrorEnabled(true);
            etTimbradoLayout.setError("Requerido");
            //etTimbrado.setError("Obligatorio");
            return false;
        }
        etTimbradoLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarPuntoExpedicion(){
        if (etPuntoExpedicion.getText().toString().trim().isEmpty()) {
            etPuntoExpedicionLayout.setErrorEnabled(true);
            etPuntoExpedicionLayout.setError("Requerido");
            //etPuntoExpedicion.setError("Obligatorio");
            return false;
        }
        etPuntoExpedicionLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarFechaVigencia(){

        if (etVigencia.getText().toString().trim().isEmpty()) {
            etVigenciaLayout.setErrorEnabled(true);
            etVigenciaLayout.setError("Requerido");
            //etVigencia.setError("Obligatorio");
            return false;
        }


        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse( etVigencia.getText().toString());
        } catch (Exception ex){
            etVigenciaLayout.setErrorEnabled(true);
            etVigenciaLayout.setError("No es valido");
            return false;
        }


        etVigenciaLayout.setErrorEnabled(false);
        return true;

    }

    private boolean validarLicencia(){
        if (etLicencia.getText().toString().trim().isEmpty()) {
            etLicenciaLayout.setErrorEnabled(true);
            etLicenciaLayout.setError("Requerido");
            //etLicencia.setError("Obligatorio");
            return false;
        }

        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        android_id = sharedPreferences.getString("TESAKA_INSTALL_ID","");
        try {
            String licencia = decrypt(etLicencia.getText().toString().trim(),android_id);
            System.out.println("Limite: "+licencia);
            long dv = Long.valueOf(licencia)*1000;
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.setTime( new java.util.Date(dv));
            try {
                Toast.makeText(getApplicationContext(), "Licencia valida hasta: " + cal.getTime(), Toast.LENGTH_LONG).show();
                SimpleDateFormat formatter =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                etFchVencimientoLicencia.setText(formatter.format(dv));
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Licencia Invalida ", Toast.LENGTH_SHORT).show();
                System.out.println("Licencia invalida");
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Licencia Invalida ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }


        etLicenciaLayout.setErrorEnabled(false);
        return true;
    }

    public String decrypt(String encryptedText, String salt) throws Exception {
        System.out.println("decrypting... "+encryptedText+"-"+salt);
        String seed = salt.substring(0,16);
        byte[] clearText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES/ECB/NoPadding");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
