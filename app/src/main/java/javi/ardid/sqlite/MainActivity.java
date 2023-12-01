package javi.ardid.sqlite;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.dao.Dao;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;

import javi.ardid.sqlite.Adapters.ProductAdapter;
import javi.ardid.sqlite.configuraciones.Configuracion;
import javi.ardid.sqlite.databinding.ActivityMainBinding;
import javi.ardid.sqlite.helpers.ProductosHelper;
import javi.ardid.sqlite.modelos.Producto;


public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private ArrayList<Producto> listaProductos;

    private ProductAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private ProductosHelper helper;

    private Dao<Producto, Integer> daoProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaProductos = new ArrayList<>();



        adapter = new ProductAdapter(MainActivity.this, listaProductos, R.layout.product_view_holder);
        layoutManager = new LinearLayoutManager(this);

        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);




        helper = new ProductosHelper(this, Configuracion.BD_NAME, null, Configuracion.BD_VERSION);

        if(helper != null){
            try {
                daoProductos = helper.getDaoProductos();
                listaProductos.addAll(daoProductos.queryForAll());
                adapter.notifyItemRangeChanged(0, listaProductos.size());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private AlertDialog crearProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("CREAR PRODUCTO");
        builder.setCancelable(false);
        View productView = LayoutInflater.from(this).inflate(R.layout.product_view_model, null);
        EditText txtNombre = productView.findViewById(R.id.txtNombreProductoViewModel);
        EditText txtCantidad = productView.findViewById(R.id.txtCantidadViewModal);
        EditText txtPrecio = productView.findViewById(R.id.txtPrecioViewModal);
        builder.setView(productView);

        builder.setNegativeButton("CANCELAR",null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(txtNombre.getText().toString().isEmpty()||
                txtCantidad.getText().toString().isEmpty() ||
                        txtPrecio.getText().toString().isEmpty()
                ){
                    Toast.makeText(MainActivity.this, "FALTAN DATOS :(", Toast.LENGTH_SHORT).show();
                }else{
                    Producto producto = new Producto(
                            txtNombre.getText().toString(),
                            Integer.parseInt(txtCantidad.getText().toString()),
                            Float.parseFloat(txtPrecio.getText().toString())
                    );
                    listaProductos.add(producto);
                    adapter.notifyItemInserted(listaProductos.size()-1);
                    //GUARDAR EN LA BASE DE DATOS
                    try {
                        daoProductos.create(producto);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

        return builder.create();
    }


}