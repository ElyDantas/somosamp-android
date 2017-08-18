package br.org.pibfortaleza.somosamp.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.model.Autor;

/**
 * Created by elydantas on 05/08/15.
 */
public class AutorDAO extends SQLiteOpenHelper {

    private static final String TAG_L = "LISTAR_AUTOR";
    private static final String TAG_U = "UPDATE_AUTOR";
    private static final String TAG_G = "GET_AUTOR";
    private static final String TAG_I = "INSERT_AUTOR";
    private static final String TAG_D = "DELETE_AUTOR";
    private static final String TAG_E = "ERROR";

    // Criao de constantes para auxiliar no controle de verses do banco de dados.
    private static final int VERSAO = 2;
    private static final String TABELA = "AUTOR";
    private static final String DATABASE = "AUTOR.db";
    private static final String COLUMN_AUTOR_ID = "_ID";
    private static final String COLUMN_AUTOR_NAME = "NAME";
    private static final String COLUMN_AUTOR_NAME_ID = "NAME_ID";
    private static final String COLUMN_AUTOR_QTD_POSTS = "QTD";
    private static final String COLUMN_AUTOR_AVATAR = "AVATAR";
    private static final String COLUMN_AUTOR_URL = "URL";
    private static final String DATABASE_IMAGEM = "IMAGEM";
    private static AutorDAO mAutorDaoInstance;

    // Construtor gerado por meio da superclasse
    public AutorDAO(Context context) {
        //		super(context, name, factory, version);
        super(context, DATABASE, null, VERSAO);
        // TODO Auto-generated constructor stub
    }

    public static AutorDAO getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mAutorDaoInstance == null) {
            mAutorDaoInstance = new AutorDAO(ctx.getApplicationContext());
        }
        return mAutorDaoInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA + "("
                + COLUMN_AUTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_AUTOR_NAME + " TEXT NOT NULL,"
                + COLUMN_AUTOR_NAME_ID + " TEXT,"
                + COLUMN_AUTOR_QTD_POSTS + " INTEGER,"
                + COLUMN_AUTOR_AVATAR + " INTEGER,"
                + COLUMN_AUTOR_URL + " TEXT NOT NULL,"
                + " FOREIGN KEY(" + COLUMN_AUTOR_AVATAR + ") REFERENCES "+DATABASE_IMAGEM+"(_ID)"
                + ")";
        // Execuo do comando para criao de tabelas no SQLite
        db.execSQL(sql);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        //onCreate(db);

        switch(oldVersion) {
            case 1:
                db.execSQL("DELETE FROM "+TABELA+";");
        }

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int salvarAutor(Autor autor) {

        ArrayList<Autor> autors = listarAutores();

        for (Autor a : autors) {
            if (autor.getNome().equals(a.getNome())) return a.getId();
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();
        long id = 0;

        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(COLUMN_AUTOR_NAME, autor.getNome());
            values.put(COLUMN_AUTOR_NAME_ID, autor.getNomeId());
            values.put(COLUMN_AUTOR_QTD_POSTS, autor.getNumPosts());
            values.put(COLUMN_AUTOR_AVATAR, autor.getIdAvatar());
            values.put(COLUMN_AUTOR_URL, autor.getUrl());

            id = db.insert(TABELA, null, values);
            Log.v(TAG_I, "[Autor added]: " + autor);
        } catch (SQLException sqle) {
            Log.e(TAG_E, sqle.getMessage());
        } finally {
            db.close();
        }

        return (int) id;
    }

    public ArrayList<Autor> listarAutores() {

        ArrayList<Autor> listaAutor = new ArrayList<>();

        String sql = "Select * from " + TABELA + " order by " + COLUMN_AUTOR_NAME;

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {

                Autor obj = new Autor();

                obj.setId(cursor.getInt(0));
                obj.setNome(cursor.getString(1));
                obj.setNomeId(cursor.getString(2));
                obj.setNumPosts(cursor.getInt(3));
                obj.setIdAvatar(cursor.getInt(4));
                obj.setUrl(cursor.getString(5));

                listaAutor.add(obj);
            }
        } catch (SQLException sqle) {
            Log.e(TAG_L, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return listaAutor;
    }

    public void delAutor(Autor obj) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Query para deletar o item recebido como param
            db.delete(TABELA, COLUMN_AUTOR_ID + " = '" + obj.getId() + "'", null);
            Log.v(TAG_D, "Autor deleted: " + obj.getNome() + ", id_request_code: " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            db.close();
        }

    }

    public void updateAutor(Autor obj) {

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        try {

            values.put(COLUMN_AUTOR_ID, obj.getId());
            values.put(COLUMN_AUTOR_NAME, obj.getNome());
            values.put(COLUMN_AUTOR_NAME_ID, obj.getNomeId());
            values.put(COLUMN_AUTOR_QTD_POSTS, obj.getNumPosts());
            values.put(COLUMN_AUTOR_AVATAR, obj.getIdAvatar());
            values.put(COLUMN_AUTOR_URL, obj.getUrl());

            // Utiliza o mtodo insert da base de dados para inserir os
            // valores definidos anterior na tabela FichaMedica
            db.update(TABELA, values, "" + COLUMN_AUTOR_ID + " =" + obj.getId() + ";", null);
            close();
            // Escreve o log de insero da tabela na base de dados
            Log.i(TAG_U, TABELA + " UPDATED: " + obj);

        } catch (SQLException e) {
            Log.e(TAG_E, e.getMessage());
        } finally {
            db.close();
        }

    }

    public Autor getAutor(int id) {

        String sql = "Select * from " + TABELA + " where " + COLUMN_AUTOR_ID + " = '" + id + "'";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        Autor obj = new Autor();
        // Os blocos try, catch e finally so utilizados para o tratamento de excees
        try {
            while (cursor.moveToNext()) {
                // Construindo o objeto a partir dos registros da base de dados
                obj.setId(cursor.getInt(0));
                obj.setNome(cursor.getString(1));
                obj.setNomeId(cursor.getString(2));
                obj.setNumPosts(cursor.getInt(3));
                obj.setIdAvatar(cursor.getInt(4));
                obj.setUrl(cursor.getString(5));

            }
        } catch (SQLException sqle) {
            Log.e(TAG_G, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return obj;

    }

    public Autor getAutor(String nome) {

        String sql = "SELECT * FROM " + TABELA + " WHERE " + COLUMN_AUTOR_NAME + " = '" + nome + "'";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {

            Autor obj = new Autor();

            while (cursor.moveToNext()) {
                obj.setId(cursor.getInt(0));
                obj.setNome(cursor.getString(1));
                obj.setNomeId(cursor.getString(2));
                obj.setNumPosts(cursor.getInt(3));
                obj.setIdAvatar(cursor.getInt(4));
                obj.setUrl(cursor.getString(5));
            }

            return obj;

        } catch (SQLException sqle) {
            Log.e(TAG_G, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return null;

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

