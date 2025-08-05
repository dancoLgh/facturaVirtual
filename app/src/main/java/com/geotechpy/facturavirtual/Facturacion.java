package com.geotechpy.facturavirtual;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

public class Facturacion extends AppCompatActivity {
    private EditText etFacRUC, etFacDV, etFacNombre, etFacDomicilio, etFacCorreo, etFacCantidad, etFacPrecioUnitario, etFacDescripcion;
    private TextInputLayout etFacRUCLayout, etFacDVLayout, etFacNombreLayout, etFacDomicilioLayout, etFacCorreoLayout, etFacCantidadLayout, etFacPrecioUnitarioLayout, etFacDescripcionLayout;
    private Button btnFacAceptar, btnFacCancelar;
    private Switch swCondicionVenta;
    private RadioGroup rgFacTasa;
    private RadioButton rbFacTasa;
    private JSONObject setResponse;
    private Runnable changeMessage;
    private String strCharacters;
    private Boolean isFinished = false;


    ProgressDialog progressDialog;
    public static final String CONTRIBUYENTEPREF = "Contribuyente";
    SharedPreferences sharedPreferences;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturacion);

        if (Build.VERSION.SDK_INT >= 23){
            if (verificarPermisos()){

                swCondicionVenta = findViewById(R.id.swCondicionVenta);
                swCondicionVenta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if ( isChecked ){
                            swCondicionVenta.setText("Credito");
                        } else {
                            swCondicionVenta.setText("Contado");
                        }
                    }
                });

                progressDialog = new ProgressDialog(this);
                changeMessage = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage(strCharacters);
                    }
                };


                etFacRUC = findViewById(R.id.etFacRUC);
                etFacRUC.setMaxLines(1);
                etFacRUC.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacRUCLayout = findViewById(R.id.etFacRUCLayout);
                etFacRUC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if ( !hasFocus) {
                            if (!validarRUC()){
                                return;
                            } else {
                                cargarDatosCliente();
                            }
                        }
                    }
                });



                etFacDV = findViewById(R.id.etFacDV);
                etFacDV.setMaxLines(1);
                etFacDV.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacDVLayout = findViewById(R.id.etFacDVLayout);

                etFacNombre = findViewById(R.id.etFacNombre);
                etFacNombre.setMaxLines(1);
                etFacNombre.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacNombreLayout = findViewById(R.id.etFacNombreLayout);

                etFacDomicilio = findViewById(R.id.etFacDomicilio);
                etFacDomicilio.setMaxLines(1);
                etFacDomicilio.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacDomicilioLayout = findViewById(R.id.etFacDomicilioLayout);

                etFacCorreo = findViewById(R.id.etFacCorreo);
                etFacCorreo.setMaxLines(1);
                etFacCorreo.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacCorreoLayout = findViewById(R.id.etFacCorreoLayout);

                rgFacTasa = findViewById(R.id.rgFacTasa);

                etFacCantidad = findViewById(R.id.etFacCantidad);
                etFacCantidad.setMaxLines(1);
                etFacCantidad.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacCantidadLayout = findViewById(R.id.etFacCantidadLayout);

                etFacPrecioUnitario = findViewById(R.id.etFacPrecioUnitario);
                etFacPrecioUnitario.setMaxLines(1);
                etFacPrecioUnitario.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etFacPrecioUnitario.addTextChangedListener(new NumberTextWatcher(etFacPrecioUnitario));
                etFacPrecioUnitarioLayout = findViewById(R.id.etFacPrecioUnitarioLayout);

                etFacDescripcion = findViewById(R.id.etFacDescripcion);
                etFacDescripcion.setMaxLines(1);
                etFacDescripcionLayout = findViewById(R.id.etFacDescripcionLayout);

                btnFacAceptar = findViewById(R.id.btnFacAceptar);
                btnFacAceptar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        fnValidarFormulario();
                    }
                });

                btnFacCancelar = findViewById(R.id.btnFacCancelar);
                btnFacCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Facturacion.this, MainActivity.class);
                        startActivity(intent);
                    }
                });


            } else {
                solicitarPermisos();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (isFinished){
            Intent intent = new Intent(Facturacion.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permiso otorgados.");
                } else {
                    Log.e("value", "Permiso denegado.");
                }
                break;
        }
    }

    private boolean verificarPermisos(){
        int result = ContextCompat.checkSelfPermission(Facturacion.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void solicitarPermisos(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(Facturacion.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Facturacion.this, "Favor habilitar los permisos para descargar las facturas.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Facturacion.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void cargarDatosCliente(){

        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        final String spUsuario = sharedPreferences.getString("etUsuario","");
        final String spContrasena = sharedPreferences.getString("etContrasena","");
        if (!spUsuario.equals("") && !spContrasena.equals("")) {

            progressDialog.setMessage("Obteniendo informacion");
            RequestQueue requestQueue = Volley.newRequestQueue(Facturacion.this);

            progressDialog.setMessage("Conectandose...");
            progressDialog.show();
            final String url = "https://marangatu.set.gov.py/eset-restful/contribuyentes/consultar?codigoEstablecimiento=1&ruc="+etFacRUC.getText().toString().trim();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.hide();
                            System.out.println("Response"+ response.toString());
                            Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                            try {
                                etFacDV.setText(response.get("dv").toString());
                                etFacNombre.setText(response.get("nombre").toString());
                                etFacDomicilio.setText((response.isNull("direccion")) ? "" : response.get("direccion").toString());
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String credenciales = spUsuario+":"+spContrasena;
                    String auth = "Basic " + Base64.encodeToString(credenciales.getBytes(),Base64.NO_WRAP);
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("User-Agent","Tesaka APP Android");
                    headers.put("authorization",auth);
                    return headers;
                }
            } ;


            requestQueue.add(getRequest);


        } else {
            Intent intent = new Intent(Facturacion.this, Personalizacion.class);
            startActivity(intent);

        }


    }

    private void fnValidarFormulario(){

        if (!validarRUC()){
            return;
        }
        if (!validarDV()){
            return;
        }
        if (!validarNombre()){
            return;
        }
        if (!validarCantidad()){
            return;
        }
        if (!validarPrecioUnitario()){
            return;
        }
        if (!validarDescripcion()){
            return;
        }

        etFacRUCLayout.setErrorEnabled(false);
        etFacDVLayout.setErrorEnabled(false);
        etFacNombreLayout.setErrorEnabled(false);
        etFacCantidadLayout.setErrorEnabled(false);
        etFacPrecioUnitarioLayout.setErrorEnabled(false);
        etFacDescripcionLayout.setErrorEnabled(false);

        //Detalles
        int tasaId = rgFacTasa.getCheckedRadioButtonId();
        rbFacTasa = findViewById(tasaId);
        int tasaAplica = rbFacTasa.getText().toString().trim().equals("Exento") ? 0 : Integer.parseInt(rbFacTasa.getText().toString().trim().substring(0, rbFacTasa.getText().toString().trim().length()-1));
        int cantidad = Integer.parseInt( etFacCantidad.getText().toString().trim());
        int precioUnitario = Integer.parseInt( etFacPrecioUnitario.getText().toString().trim().replaceAll(",","").replaceAll("\\.",""));
        int precioTotal = (cantidad * precioUnitario);
        int impuestoExento = 0;
        int impuestoAl5 = tasaAplica == 5 ? Math.round(precioUnitario/21) : 0 ;
        int impuestoAl10 = tasaAplica == 10 ? Math.round(precioUnitario/11) : 0 ;
        int precioTotalExento = tasaAplica == 0 ? precioTotal : 0 ;
        int precioTotalAl5 = tasaAplica == 5 ? precioTotal : 0 ;
        int precioTotalAl10 = tasaAplica == 10 ? precioTotal : 0 ;
        String descripcion = etFacDescripcion.getText().toString().trim();

        //Totales
        int impuestoTotalExento = 0;
        int impuestoTotalAl5 = impuestoAl5;
        int impuestoTotalAl10 = impuestoAl10;
        int valorTotalExento = precioTotalExento;
        int valorTotalAl5 = precioTotalAl5;
        int valorTotalAl10 = precioTotalAl10;
        int impuestoTotal = (impuestoAl5 + impuestoAl10);
        int valorTotal = (valorTotalExento + valorTotalAl5 + valorTotalAl10);

        //Informado
        String situacion = "registrado";
        String identificacion = null;
        String nombre = etFacNombre.getText().toString().trim();
        String ruc = etFacRUC.getText().toString().trim();
        String dv = etFacDV.getText().toString().trim();
        String domicilio = etFacDomicilio.getText().toString().trim();
        String correoElectronico = etFacCorreo.getText().toString().trim();

        //Informante
        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        String spNombre = sharedPreferences.getString("etNombre","");
        String spNombreFantasia = sharedPreferences.getString("etNombreFantasia","");
        String spRuc = sharedPreferences.getString("etRuc","");
        String spDV = sharedPreferences.getString("etDV","");
        String spDireccion = sharedPreferences.getString("etDireccion","");
        String spTelefono = sharedPreferences.getString("etTelefono","");
        String spEstablecimiento = sharedPreferences.getString("etEstablecimiento","");
        String spPuntoExpedicion = sharedPreferences.getString("etPuntoExpedicion","");
        String spFechaVigencia = sharedPreferences.getString("etVigencia","");
        String spTimbrado = sharedPreferences.getString("etTimbrado","");

        //Transaccion
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        String condicionCompra = swCondicionVenta.isChecked() ? "CREDITO" : "CONTADO";

        //Atributos
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();
        String fechaHora = dateFormat1.format(date1);
        String uuid = UUID.randomUUID().toString();
        String version = "1.1.2";



        JSONObject facturaObj = new JSONObject();
        JSONObject atributosObj = new JSONObject();
        JSONObject informateObj = new JSONObject();
        JSONObject transaccionObj = new JSONObject();
        JSONObject detalleObj = new JSONObject();
        JSONArray detalleArray = new JSONArray();
        JSONObject totalesObj = new JSONObject();
        JSONObject informadoObj = new JSONObject();

        try {
            facturaObj.put("id", 1);

            detalleObj.put("tasaAplica", String.valueOf(tasaAplica));
            detalleObj.put("cantidad", cantidad);
            detalleObj.put("precioUnitario", precioUnitario);
            detalleObj.put("precioTotal", precioTotal);
            detalleObj.put("impuestoExento", impuestoExento);
            detalleObj.put("impuestoAl5", impuestoAl5);
            detalleObj.put("impuestoAl10", impuestoAl10);
            detalleObj.put("precioTotalExento", precioTotalExento);
            detalleObj.put("precioTotalAl5", precioTotalAl5);
            detalleObj.put("precioTotalAl10", precioTotalAl10);
            detalleObj.put("descripcion", descripcion.toUpperCase());
            detalleArray.put(detalleObj);
            facturaObj.put("detalle", detalleArray);

            totalesObj.put("impuestoTotalExento",impuestoTotalExento);
            totalesObj.put("impuestoTotalAl5",impuestoTotalAl5);
            totalesObj.put("impuestoTotalAl10",impuestoTotalAl10);
            totalesObj.put("valorTotalExento",valorTotalExento);
            totalesObj.put("valorTotalAl5",valorTotalAl5);
            totalesObj.put("valorTotalAl10",valorTotalAl10);
            totalesObj.put("impuestoTotal",impuestoTotal);
            totalesObj.put("valorTotal",valorTotal);
            facturaObj.put("totales",totalesObj);

            informadoObj.put("situacion",situacion);
            informadoObj.put("identificacion",identificacion);
            informadoObj.put("nombre",nombre);
            informadoObj.put("ruc",ruc);
            informadoObj.put("dv",dv);
            informadoObj.put("domicilio",domicilio);
            informadoObj.put("correoElectronico",correoElectronico);
            facturaObj.put("informado",informadoObj);

            informateObj.put("ruc",spRuc);
            informateObj.put("dv",spDV);
            informateObj.put("nombre",spNombre);
            informateObj.put("nombreFantasia",spNombreFantasia);
            informateObj.put("domicilioEmision",spDireccion);
            informateObj.put("telefono",spTelefono);
            informateObj.put("codigoEstablecimiento",spEstablecimiento);
            informateObj.put("timbradoFactura",spTimbrado);
            informateObj.put("puntoExpedicionFactura",spPuntoExpedicion);
            informateObj.put("inicioVigenciaFactura",spFechaVigencia);
            informateObj.put("establecimiento",spEstablecimiento);
            facturaObj.put("informante",informateObj);

            transaccionObj.put("fecha",fecha);
            transaccionObj.put("condicionCompra",condicionCompra);
            transaccionObj.put("tipoComprobante","Factura Virtual");
            facturaObj.put("transaccion",transaccionObj);

            atributosObj.put("fechaCreacion",fecha);
            atributosObj.put("fechaHoraCreacion",fechaHora);
            atributosObj.put("uuid",uuid);
            atributosObj.put("version",version);
            facturaObj.put("atributos",atributosObj);

        } catch (JSONException e){
            e.printStackTrace();
        }

        System.out.println(facturaObj.toString());
        fnEnviarFacturaASET(facturaObj);

        //Intent intent = new Intent(Personalizacion.this, MainActivity.class);
        //startActivity(intent);

    }

    private AlertDialog alertDialog(String pMensaje) {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(pMensaje)
                .setIcon(R.drawable.ic_exclamation_triangle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void fnEnviarFacturaASET(final JSONObject pFacturaObj){
        System.out.println("jsonString " + pFacturaObj);
        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        final String spUsuario = sharedPreferences.getString("etUsuario","");
        final String spContrasena = sharedPreferences.getString("etContrasena","");
        if (!spUsuario.equals("") && !spContrasena.equals("")) {

            progressDialog.setMessage("Obteniendo informacion");
            RequestQueue requestQueue = Volley.newRequestQueue(Facturacion.this);

            progressDialog.setMessage("Enviando...");
            progressDialog.show();
            final String url = "https://marangatu.set.gov.py/eset-restful/facturas/guardar";
            //final String url = "http://www.geotechpy.com/falseResponse.php";
            //final String url = "http://www.geotechpy.com/nullResponse.php";
            //final String url = "http://www.geotechpy.com/okResponse.php";
            final String requestBody = pFacturaObj.toString();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.hide();
                            System.out.println("onResponse: "+ response.toString());
                            //Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                            strCharacters = "Inicio de Session Correcto";
                            runOnUiThread(changeMessage);
                            try {
                                if ( response.has("procesamientoCorrecto") ){
                                    if ( response.getBoolean("procesamientoCorrecto")) {
                                        fnGenerarFactura(pFacturaObj, response);
                                        //Toast.makeText(getApplicationContext(), "Facturado exitosamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        AlertDialog diaBox = alertDialog(response.getString("mensajeProcesamiento"));
                                        diaBox.show();
                                    }
                                } else {
                                    //Toast.makeText(getApplicationContext(), "Error al obtener respuesta de la SET", Toast.LENGTH_SHORT).show();
                                    strCharacters = "Error al obtener una respuesta valida de la SET. Verifique su conexion";
                                    runOnUiThread(changeMessage);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            Log.e("onErrorResponse: ", error.toString());
                            Toast.makeText(getApplicationContext(), "Error al obtener una respuesta valida de la SET. Verifique su conexion", Toast.LENGTH_SHORT).show();
                        }
                    } )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String credenciales = spUsuario+":"+spContrasena;
                    String auth = "Basic " + Base64.encodeToString(credenciales.getBytes(),Base64.NO_WRAP);
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("User-Agent","Tesaka APP Android");
                    headers.put("authorization",auth);
                    headers.put("Conteny-type","application/json");
                    return headers;
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }

            } ;


            requestQueue.add(getRequest);


        }





    }

    private void fnGenerarFactura(JSONObject pRequest, JSONObject pResponse) {
        final int PAGE_WIDTH = 650;
        final int PAGE_HEIGHT = 562;
        String nroFactura = "";
        PdfDocument facturaPdf = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();

        PdfDocument.Page page = facturaPdf.startPage(pageInfo);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.pdf_layout, null);

        TextView compNombre = content.findViewById(R.id.tvCompNombre);
        TextView compNombreFantasia = content.findViewById(R.id.tvCompNombreFantasia);
        TextView compDireccion = content.findViewById(R.id.tvCompDireccion);
        TextView compTelefono = content.findViewById(R.id.tvCompTelefono);
        TextView compTimbrado = content.findViewById(R.id.tvCompTimbrado);
        TextView compCodigoControl = content.findViewById(R.id.tvCompCodigoControl);
        TextView compInicioVigencia = content.findViewById(R.id.tvCompInicioVigencia);
        TextView compRUC = content.findViewById(R.id.tvCompRUC);
        TextView compNroFactura = content.findViewById(R.id.tvCompNroFactura);
        TextView compFechaEmision = content.findViewById(R.id.tvCompFechaEmision);
        TextView compCondicionVenta = content.findViewById(R.id.tvCompCondicionVenta);
        TextView compRucCedula = content.findViewById(R.id.tvCompRucCedula);
        TextView compNombreRazonSocial = content.findViewById(R.id.tvCompNombreRazonSocial);
        TextView compDireccionCliente = content.findViewById(R.id.tvCompDireccionCliente);
        TextView compCantidad = content.findViewById(R.id.tvCompCantidad);
        TextView compDescripcion1 = content.findViewById(R.id.tvCompDescripcion1);
        TextView compDescripcion2 = content.findViewById(R.id.tvCompDescripcion2);
        TextView compPrecioUnitario = content.findViewById(R.id.tvCompPrecioUnitario);
        TextView compExentas = content.findViewById(R.id.tvCompExentas);
        TextView compGravada5 = content.findViewById(R.id.tvCompGravada5);
        TextView compGravada10 = content.findViewById(R.id.tvCompGravada10);
        TextView compTotalAPagar = content.findViewById(R.id.tvCompTotalAPagar);
        ImageView ivQR = content.findViewById(R.id.ivQr);


        TextView compTotalExentas = content.findViewById(R.id.tvCompTotalExentas);
        TextView compTotalGravada5 = content.findViewById(R.id.tvCompTotalGravada5);
        TextView compTotalGravada10 = content.findViewById(R.id.tvCompTotalGravada10);
        TextView compTotalIva5 = content.findViewById(R.id.tvCompTotalIva5);
        TextView compTotalIva10 = content.findViewById(R.id.tvCompTotalIva10);
        TextView compTotalIva = content.findViewById(R.id.tvCompTotalIva);

        try {
            compNombre.setText(pRequest.getJSONObject("informante").getString("nombre"));
            compNombreFantasia.setText(pRequest.getJSONObject("informante").getString("nombreFantasia"));
            compDireccion.setText(pRequest.getJSONObject("informante").getString("domicilioEmision"));
            compTelefono.setText(pRequest.getJSONObject("informante").getString("telefono"));
            compTimbrado.setText("TIMBRADO NRO. "+pRequest.getJSONObject("informante").getString("timbradoFactura"));
            compCodigoControl.setText("CODIGO CONTROL: "+pResponse.getString("numeroControl"));
            compInicioVigencia.setText("INICIO DE VIGENCIA "+pRequest.getJSONObject("informante").getString("inicioVigenciaFactura"));
            compRUC.setText("RUC "+pRequest.getJSONObject("informante").getString("ruc") +"-"+pRequest.getJSONObject("informante").getString("dv"));
            compNroFactura.setText(pResponse.getString("numeroComprobante"));
            nroFactura = pResponse.getString("numeroComprobante");
            compFechaEmision.setText(pResponse.getString("fechaEmision"));
            if ( pRequest.getJSONObject("transaccion").getString("condicionCompra").equals("CONTADO")) {
                compCondicionVenta.setText(" CONTADO:X CREDITO:");
            } else {
                compCondicionVenta.setText(" CONTADO: CREDITO: X");
            }
            compRucCedula.setText(pRequest.getJSONObject("informado").getString("ruc")+"-"+pRequest.getJSONObject("informado").getString("dv"));
            compNombreRazonSocial.setText(pRequest.getJSONObject("informado").getString("nombre"));
            compDireccionCliente.setText(pRequest.getJSONObject("informado").getString("domicilio"));

            JSONArray detalle = pRequest.getJSONArray("detalle");
            JSONObject d = (JSONObject) detalle.get(0);
            compCantidad.setText(d.getString("cantidad"));
            if ( d.getString("descripcion").length() > 26 ){
                compDescripcion1.setText(d.getString("descripcion").substring(0,25));
                compDescripcion2.setText(d.getString("descripcion").substring(26));
            } else {
                compDescripcion1.setText(d.getString("descripcion"));
                compDescripcion2.setText("");
            }
            compPrecioUnitario.setText( String.format("%1$,.0f", Float.parseFloat( d.getString("precioUnitario"))) );
            compExentas.setText(String.format("%1$,.0f", Float.parseFloat(d.getString("precioTotalExento"))));
            compGravada5.setText(String.format("%1$,.0f", Float.parseFloat(d.getString("precioTotalAl5"))));
            compGravada10.setText(String.format("%1$,.0f", Float.parseFloat(d.getString("precioTotalAl10"))));

            compTotalExentas.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("valorTotalExento"))));
            compTotalGravada5.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("valorTotalAl5"))));
            compTotalGravada10.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("valorTotalAl10"))));
            compTotalIva5.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("impuestoTotalAl5"))));
            compTotalIva10.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("impuestoTotalAl10"))));
            compTotalIva.setText(String.format("%1$,.0f", Float.parseFloat(pRequest.getJSONObject("totales").getString("impuestoTotal"))));

            int totalAPagar = Integer.parseInt(pRequest.getJSONObject("totales").getString("valorTotalExento")) +
                    Integer.parseInt(pRequest.getJSONObject("totales").getString("valorTotalAl5")) +
                    Integer.parseInt(pRequest.getJSONObject("totales").getString("valorTotalAl10")) ;
            compTotalAPagar.setText(String.format("%1$,.0f", Float.parseFloat(String.valueOf(totalAPagar))));



            Bitmap bm = getQRImage(pResponse.getString("cadenaControl"),152);
            if (bm != null){
                ivQR.setImageBitmap(bm);
            }

            int measureWidth = View.MeasureSpec.makeMeasureSpec(PAGE_WIDTH, View.MeasureSpec.EXACTLY);
            int measuredHeight = View.MeasureSpec.makeMeasureSpec(PAGE_HEIGHT, View.MeasureSpec.EXACTLY);

            content.measure(measureWidth, measuredHeight);
            content.layout(0, 0, PAGE_WIDTH, PAGE_HEIGHT);


            content.draw(page.getCanvas());
            facturaPdf.finishPage(page);

            //String directory_path =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(); //getFilesDir().getPath();
            //File facturaFile = new File(directory_path+"/"+nroFactura+".pdf");
            File facturaFile = createImageFile(nroFactura);

            try {
                facturaPdf.writeTo(new FileOutputStream(facturaFile));
                try {
                    shareFile(facturaFile);
                    Toast.makeText(getApplicationContext(), "Impresion de factura exitosa", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println("Error: "+e.toString());
                    Toast.makeText(getApplicationContext(), "Error al compartir la factura. Verifique en su carpeta de descarga", Toast.LENGTH_SHORT).show();
                    isFinished = true;
                }
            } catch (Exception e) {
                System.out.println("Error: "+e.toString());
                Toast.makeText(getApplicationContext(), "Error al imprimir la factura. Verifique en el Marangatu", Toast.LENGTH_SHORT).show();
                isFinished = true;
            }



        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "La factura ha sido generada en la SET pero no se pudo imprimir: "+e.toString(), Toast.LENGTH_SHORT).show();
            System.out.println("Error en: "+e.toString());
            isFinished = true;
        }





    }

    private File createImageFile(String pNombre) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = pNombre+"_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        File file = File.createTempFile(
                fileName,  /* prefix */
                ".pdf",         /* suffix */
                storageDir      /* directory */
        );

        return file;
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        //intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));
        Uri fileURI = FileProvider.getUriForFile(getApplicationContext(),
                                       getApplicationContext().getPackageName() + ".provider", file);
        intentShareFile.putExtra(Intent.EXTRA_STREAM,fileURI);
        startActivity(Intent.createChooser(intentShareFile, "Compartir Factura"));
        isFinished = true;

    }

    private boolean validarRUC(){
        if (etFacRUC.getText().toString().trim().isEmpty()) {
            etFacRUCLayout.setErrorEnabled(true);
            etFacRUCLayout.setError("Ingresar RUC");
            //etRUC.setError("Obligatorio");
            return false;
        }
        etFacRUCLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarDV(){
        if (etFacDV.getText().toString().trim().isEmpty()) {
            etFacDVLayout.setErrorEnabled(true);
            etFacDVLayout.setError("Ingresar DV");
            //etDV.setError("Obligatorio");
            return false;
        }
        etFacDVLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarNombre(){
        if (etFacNombre.getText().toString().trim().isEmpty()) {
            etFacNombreLayout.setErrorEnabled(true);
            etFacNombreLayout.setError("Ingresar Nombre");
            //etDV.setError("Obligatorio");
            return false;
        }
        etFacNombreLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarCantidad(){
        if (etFacCantidad.getText().toString().trim().isEmpty()) {
            etFacCantidadLayout.setErrorEnabled(true);
            etFacCantidadLayout.setError("Ingresar Cantidad");
            //etDV.setError("Obligatorio");
            return false;
        }
        etFacCantidadLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarPrecioUnitario(){
        if (etFacPrecioUnitario.getText().toString().trim().isEmpty()) {
            etFacPrecioUnitarioLayout.setErrorEnabled(true);
            etFacPrecioUnitarioLayout.setError("Ingresar Precio Unitario");
            //etDV.setError("Obligatorio");
            return false;
        }
        etFacPrecioUnitarioLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validarDescripcion(){
        if (etFacDescripcion.getText().toString().trim().isEmpty()) {
            etFacDescripcionLayout.setErrorEnabled(true);
            etFacDescripcionLayout.setError("Ingresar Descripcion");
            //etDV.setError("Obligatorio");
            return false;
        }
        etFacDescripcionLayout.setErrorEnabled(false);
        return true;
    }

    public static Bitmap getQRImage(String codigoControl, int size) {
        Bitmap mBitmap;
        BitMatrix bitMatrix;
        @SuppressWarnings("unused")
        QRCode qr = new QRCode();
        QRCodeWriter writer = new QRCodeWriter();
        mBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        try {
            bitMatrix = writer.encode(codigoControl, BarcodeFormat.QR_CODE, size, size);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    mBitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

}
