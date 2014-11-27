package com.example.kronos.editordetexto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;


public class Principal extends Activity {


    private EditText etTexto;
    private String textoOriginal;
    private String ruta;
    private AlertDialog alerta;
    private boolean dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        etTexto = (EditText)findViewById(R.id.etSalida);
        textoOriginal = "";
        dialogo = false;
        leerarchivo();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dialogo = savedInstanceState.getBoolean(getString(R.string.dialogo));
        if(dialogo) {
            guardararchivo(null);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.dialogo), dialogo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(alerta != null){
            alerta.dismiss();
        }
    }

    public void guardararchivo(View v){
        String textoFinal = etTexto.getText().toString();
        if(!textoFinal.equals(textoOriginal)){
            confirmacion();
        }else{
            Toast.makeText(this, getString(R.string.SinCambiar), Toast.LENGTH_SHORT).show();
        }
    }


    private void confirmacion(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getString(R.string.tconf));
        dialogo.setMessage(getString(R.string.confirmacion));
        dialogo.setCancelable(true);
        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                FileWriter fichero;
                PrintWriter pw;
                try {
                    fichero = new FileWriter(ruta);
                    pw = new PrintWriter(fichero);
                    pw.println(Principal.this.etTexto.getText().toString());
                    pw.close();
                    fichero.close();
                    Principal.this.dialogo = false;
                } catch (IOException e) {
                    Toast.makeText(Principal.this, getString(R.string.errorEscritura), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                Principal.this.dialogo = false;
                dialogo.dismiss();
            }
        });
        alerta = dialogo.create();
        this.dialogo = true;
        alerta.show();
    }

    private void escribirContenido(URI datos){
        ruta = datos.getPath();
        try {
            FileInputStream fIn = new FileInputStream(ruta);
            InputStreamReader archivo = new InputStreamReader(fIn, getString(R.string.cod));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            StringBuilder todo = new StringBuilder("");
            while (linea != null) {
                todo.append(linea + "\n");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            etTexto.setText(todo);
            textoOriginal = todo.toString();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.errorLectura), Toast.LENGTH_SHORT).show();
        }
    }
    public void larchivo(View view){
        leerarchivo();
    }
    private void leerarchivo(){
        etTexto.setText("");
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data!=null) {
            URI datos = null;
            try {
                datos = new URI(data.toString());
            } catch (URISyntaxException e) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
            if (datos != null) {
                escribirContenido(datos);
            }
        }
    }

}
