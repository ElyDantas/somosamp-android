package br.org.pibfortaleza.somosamp.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.model.Imagem;

/**
 * Created by elydantas on 05/08/15.
 */
public class ImagemDAO extends SQLiteOpenHelper {

    private static final String TAG_L = "LISTAR_IMAGEM";
    private static final String TAG_U = "UPDATE_IMAGEM";
    private static final String TAG_G = "GET_IMAGEM";
    private static final String TAG_I = "INSERT_IMAGEM";
    private static final String TAG_D = "DELETE_IMAGEM";
    private static final String TAG_E = "ERROR";

    // Criao de constantes para auxiliar no controle de verses do banco de dados.
    private static final int VERSAO = 1;
    private static final String TABELA = "IMAGEM";
    private static final String DATABASE = "IMAGEM.db";
    private static final String COLUMN_ID = "_ID";
    private static final String COLUMN_BYTE_ARRAY = "BYTE_ARRAY";
    private static ImagemDAO mImagemDaoInstance;

    // Construtor gerado por meio da superclasse
    public ImagemDAO(Context context) {
        //		super(context, name, factory, version);
        super(context, DATABASE, null, VERSAO);
        // TODO Auto-generated constructor stub
    }

    public static ImagemDAO getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mImagemDaoInstance == null) {
            mImagemDaoInstance = new ImagemDAO(ctx.getApplicationContext());
        }
        return mImagemDaoInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BYTE_ARRAY + " BLOB NOT NULL"
                + ")";
        db.execSQL(sql);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int salvarImagem(Imagem obj) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();
        long id = 0;

        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(COLUMN_BYTE_ARRAY, obj.getImagem());

            id = db.insert(TABELA, null, values);
            Log.v(TAG_I, "[Imagem added]: " + obj);
        } catch (SQLException sqle) {
            Log.e(TAG_E, sqle.getMessage());
        } finally {
            db.close();
        }

        return (int) id;
    }

    public ArrayList<Imagem> listarImagens() {

        ArrayList<Imagem> listaImagem = new ArrayList<>();

        String sql = "SELECT * FROM " + TABELA ;

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {

                Imagem obj = new Imagem();

                obj.setId(cursor.getInt(0));
                obj.setImagem(cursor.getBlob(1));

                listaImagem.add(obj);
            }
        } catch (SQLException sqle) {
            Log.e(TAG_L, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return listaImagem;
    }

    public void delImagem(Imagem obj) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Query para deletar o item recebido como param
            db.delete(TABELA, COLUMN_ID + " = '" + obj.getId() + "'", null);
            Log.v(TAG_D, "Imagem deleted: " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            db.close();
        }

    }

    public void updateImagem(Imagem obj) {

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        try {

            values.put(COLUMN_ID, obj.getId());
            values.put(COLUMN_BYTE_ARRAY, obj.getImagem());

            // Utiliza o mtodo insert da base de dados para inserir os
            // valores definidos anterior na tabela FichaMedica
            db.update(TABELA, values, "" + COLUMN_ID + " =" + obj.getId() + ";", null);
            close();
            // Escreve o log de insero da tabela na base de dados
            Log.i(TAG_U, TABELA + " UPDATED: " + obj);

        } catch (SQLException e) {
            Log.e(TAG_E, e.getMessage());
        } finally {
            db.close();
        }

    }

    public Imagem getImagem(int id) {

        String sql = "SELESCT * FROM " + TABELA + " WHERE " + COLUMN_ID + " = '" + id + "'";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        Imagem obj = new Imagem();
        // Os blocos try, catch e finally so utilizados para o tratamento de excees
        try {
            while (cursor.moveToNext()) {
                // Construindo o objeto a partir dos registros da base de dados
                obj.setId(cursor.getInt(0));
                obj.setImagem(cursor.getBlob(1));

            }
        } catch (SQLException sqle) {
            Log.e(TAG_G, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return obj;

    }

    /**
     * Remove all from database.
     */
    public void deleteAll() {

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABELA, null, null);
            Log.v(TAG_D, "Registros da tabela "+TABELA+" deletados ");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            db.close();
        }

    }

}

