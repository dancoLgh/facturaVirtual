package com.geotechpy.facturavirtual;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String CONTRIBUYENTEPREF = "Contribuyente";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (shouldAskPermissions()) {
            askPermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        String tesakaId = sharedPreferences.getString("TESAKA_INSTALL_ID","");
        if (tesakaId.equals("")){
            SharedPreferences.Editor spEditor = sharedPreferences.edit();
            String uniqueID = UUID.randomUUID().toString();
            spEditor.putString("TESAKA_INSTALL_ID", uniqueID);
            spEditor.apply();
        }


        String ruc = sharedPreferences.getString("etRuc","");
        String licencia = sharedPreferences.getString("etFchVencimientoLicencia","");
        SimpleDateFormat format =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fchActual, fchVencimiento;
        try {
            fchVencimiento = format.parse(licencia);
            fchActual = new Date();
        } catch (Exception e){
            e.printStackTrace();
            fchVencimiento = yesterday();
            fchActual = new Date();
        }
        Intent intent = null;
        if (ruc.equals("")) {
            Toast.makeText(getApplicationContext(), "Debe registrarse para utilizar la aplicacion", Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, Personalizacion.class);
            startActivity(intent);
        } else if (fchActual.after(fchVencimiento)) {
            Toast.makeText(getApplicationContext(), "La licencia ha expirado. Debe actualizar la licencia", Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, Personalizacion.class);
            startActivity(intent);
        } else {
            String nombre = sharedPreferences.getString("etNombre","");
            Toast.makeText(getApplicationContext(), "Bienvenido "+nombre, Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, PantallaPrincipal.class);
        }
        startActivity(intent);
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }


}
