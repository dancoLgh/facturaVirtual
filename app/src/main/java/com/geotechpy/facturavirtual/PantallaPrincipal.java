package com.geotechpy.facturavirtual;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends AppCompatActivity {
    public static final String CONTRIBUYENTEPREF = "Contribuyente";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        sharedPreferences = getSharedPreferences(CONTRIBUYENTEPREF, Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("etNombre","");
        String ruc = sharedPreferences.getString("etRuc","");
        String dv = sharedPreferences.getString("etDV","");
        String direccion = sharedPreferences.getString("etDireccion","");
        String establecimiento = sharedPreferences.getString("etEstablecimiento","");
        String timbrado = sharedPreferences.getString("etTimbrado","");
        String licencia = sharedPreferences.getString("etFchVencimientoLicencia","");

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvRuc = findViewById(R.id.tvRUC);
        TextView tvDV = findViewById(R.id.tvDV);
        TextView tvEstablecimiento = findViewById(R.id.tvEstablecimiento);
        TextView tvDireccion = findViewById(R.id.tvDireccion);
        TextView tvTimbrado = findViewById(R.id.tvTimbrado);
        TextView tvLicencia = findViewById(R.id.tvLicencia);

        tvNombre.setText(nombre);
        tvRuc.setText(ruc);
        tvDV.setText(dv);
        tvDireccion.setText(direccion);
        tvEstablecimiento.setText(establecimiento);
        tvTimbrado.setText(timbrado);
        tvLicencia.setText(licencia);

        Button btnEditar = findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaPrincipal.this, Personalizacion.class);
                startActivity(intent);
            }
        });

        Button btnFacturas = findViewById(R.id.btnFacturas);
        btnFacturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaPrincipal.this, Facturacion.class);
                startActivity(intent);
            }
        });
    }
}
