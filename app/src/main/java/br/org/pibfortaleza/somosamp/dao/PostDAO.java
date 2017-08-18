package br.org.pibfortaleza.somosamp.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.model.Post;

/**
 * Created by elydantas on 05/08/15.
 */
public class PostDAO extends SQLiteOpenHelper {

    private static final String TAG_L = "LISTAR_POST";
    private static final String TAG_U = "UPDATE_POST";
    private static final String TAG_G = "GET_POST";
    private static final String TAG_I = "INSERT_POST";
    private static final String TAG_D = "DELETE_POST";
    private static final String TAG_E = "ERROR";

    // Criao de constantes para auxiliar no controle de verses do banco de dados.
    private static final int VERSAO = 3;
    private static final String TABELA = "POST";
    private static final String DATABASE = "POST.db";
    private static final String COLUMN_ID = "_ID";
    private static final String COLUMN_TITULO = "TITULO";
    private static final String COLUMN_DESCRICAO = "DESCRICAO";
    private static final String COLUMN_TEXTO = "TEXTO";
    private static final String COLUMN_DATA = "DATA";
    private static final String COLUMN_DATA_DESCRICAO = "DATA_DESCRICAO";
    private static final String COLUMN_CATEGORIA = "CATEGORIA";
    private static final String COLUMN_ID_AUTOR = "ID_AUTOR";
    private static final String COLUMN_IMAGEM_URL = "IMAGEM_URL";
    private static final String COLUMN_POST_URL = "POST_URL";
    private static final String COLUMN_POST_YOUTUBE_URL = "POST_YOUTUBE_URL";
    private static final String DATABASE_AUTOR = "AUTOR";
    private static PostDAO mPostDaoInstance;

    private static final String DATABASE_ALTER_TABLE_1 = "ALTER TABLE "
            + TABELA + " ADD COLUMN " + COLUMN_POST_YOUTUBE_URL + " TEXT;";

    // Construtor gerado por meio da superclasse
    public PostDAO(Context context) {
        //		super(context, name, factory, version);
        super(context, DATABASE, null, VERSAO);
        // TODO Auto-generated constructor stub
    }

    public static PostDAO getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mPostDaoInstance == null) {
            mPostDaoInstance = new PostDAO(ctx.getApplicationContext());
        }
        return mPostDaoInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITULO + " TEXT NOT NULL,"
                + COLUMN_DESCRICAO + " TEXT,"
                + COLUMN_TEXTO + " TEXT NOT NULL,"
                + COLUMN_DATA + " TEXT NOT NULL,"
                + COLUMN_DATA_DESCRICAO + " TEXT,"
                + COLUMN_CATEGORIA + " TEXT NOT NULL,"
                + COLUMN_IMAGEM_URL + " TEXT,"
                + COLUMN_POST_URL + " TEXT NOT NULL,"
                + COLUMN_POST_YOUTUBE_URL + " TEXT,"
                + COLUMN_ID_AUTOR + " INTEGER NOT NULL,"
                + " FOREIGN KEY(" + COLUMN_ID_AUTOR + ") REFERENCES "+DATABASE_AUTOR+"(_ID)"
                + ")";
        // Execuo do comando para criao de tabelas no SQLite
        db.execSQL(sql);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        switch(oldVersion) {
            case 1:
                db.execSQL("DELETE FROM "+TABELA+";");
            case 2:
                db.execSQL(DATABASE_ALTER_TABLE_1);
        }

        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int salvarPost(Post obj) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();
        long id = 0;

        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITULO, obj.getTitulo());
            values.put(COLUMN_DESCRICAO, obj.getDescricao());
            values.put(COLUMN_TEXTO, obj.getTexto());
            values.put(COLUMN_DATA, obj.getData());
            values.put(COLUMN_DATA_DESCRICAO, obj.getDataDescricao());
            values.put(COLUMN_CATEGORIA, obj.getCategoria());
            values.put(COLUMN_IMAGEM_URL, obj.getImagemUrl());
            values.put(COLUMN_POST_URL, obj.getPostUrl());
            values.put(COLUMN_POST_YOUTUBE_URL, obj.getYoutubeUrl());
            values.put(COLUMN_ID_AUTOR, obj.getIdAutor());

            id = db.insert(TABELA, null, values);
            Log.v(TAG_I, "[Post added]: " + obj.getTitulo());
        } catch (SQLException sqle) {
            Log.e(TAG_E, sqle.getMessage());
        } finally {
            db.close();
        }

        return (int) id;
    }

    public ArrayList<Post> listarPostes() {

        ArrayList<Post> listaPost = new ArrayList<>();

        String sql = "SELECT * FROM " + TABELA + " ORDER BY " + COLUMN_DATA + " DESC";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {

                Post obj = new Post();

                obj.setId(cursor.getInt(0));
                obj.setTitulo(cursor.getString(1));
                obj.setDescricao(cursor.getString(2));
                obj.setTexto(cursor.getString(3));
                obj.setData(cursor.getString(4));
                obj.setDataDescricao(cursor.getString(5));
                obj.setCategoria(cursor.getString(6));
                obj.setImagemUrl(cursor.getString(7));
                obj.setPostUrl(cursor.getString(8));
                obj.setYoutubeUrl(cursor.getString(9));
                obj.setIdAutor(cursor.getInt(10));

                listaPost.add(obj);
            }
        } catch (SQLException sqle) {
            Log.e(TAG_L, sqle.getMessage());
        } finally {
            cursor.close();
        }

        for (Post p : listaPost){

            System.out.println(p.getTitulo() + " - Imagem: " + p.getImagemUrl());

        }

        return listaPost;
    }

    public ArrayList<Post> listarPostsPorAutor(String idAutor) {

        ArrayList<Post> listaPost = new ArrayList<>();

        String sql = "SELECT * FROM " + TABELA + " WHERE "+COLUMN_ID_AUTOR+" = "+idAutor+" ORDER BY " + COLUMN_DATA + " DESC";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {

                Post obj = new Post();

                obj.setId(cursor.getInt(0));
                obj.setTitulo(cursor.getString(1));
                obj.setDescricao(cursor.getString(2));
                obj.setTexto(cursor.getString(3));
                obj.setData(cursor.getString(4));
                obj.setDataDescricao(cursor.getString(5));
                obj.setCategoria(cursor.getString(6));
                obj.setImagemUrl(cursor.getString(7));
                obj.setPostUrl(cursor.getString(8));
                obj.setYoutubeUrl(cursor.getString(9));
                obj.setIdAutor(cursor.getInt(10));

                listaPost.add(obj);
            }
        } catch (SQLException sqle) {
            Log.e(TAG_L, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return listaPost;
    }

    public ArrayList<Post> listarPostsPorCategoria(String categoria) {

        ArrayList<Post> listaPost = new ArrayList<>();

        String sql = "SELECT * FROM " + TABELA + " WHERE "+COLUMN_CATEGORIA+" = '"+categoria+"' ORDER BY " + COLUMN_DATA + " DESC";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {

                Post obj = new Post();

                obj.setId(cursor.getInt(0));
                obj.setTitulo(cursor.getString(1));
                obj.setDescricao(cursor.getString(2));
                obj.setTexto(cursor.getString(3));
                obj.setData(cursor.getString(4));
                obj.setDataDescricao(cursor.getString(5));
                obj.setCategoria(cursor.getString(6));
                obj.setImagemUrl(cursor.getString(7));
                obj.setPostUrl(cursor.getString(8));
                obj.setYoutubeUrl(cursor.getString(9));
                obj.setIdAutor(cursor.getInt(10));

                listaPost.add(obj);
            }
        } catch (SQLException sqle) {
            Log.e(TAG_L, sqle.getMessage());
        } finally {
            cursor.close();
        }

        return listaPost;
    }

    public void delPost(Post obj) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Query para deletar o item recebido como param
            db.delete(TABELA, COLUMN_ID + " = '" + obj.getId() + "'", null);
            Log.v(TAG_D, "Post deleted: " + obj.getTitulo() + ", id_request_code: " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            db.close();
        }

    }

    public void updatePost(Post obj) {

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        try {

            values.put(COLUMN_ID, obj.getId());
            values.put(COLUMN_TITULO, obj.getTitulo());
            values.put(COLUMN_DESCRICAO, obj.getDescricao());
            values.put(COLUMN_TEXTO, obj.getTexto());
            values.put(COLUMN_DATA, obj.getData());
            values.put(COLUMN_DATA_DESCRICAO, obj.getDataDescricao());
            values.put(COLUMN_CATEGORIA, obj.getCategoria());
            values.put(COLUMN_IMAGEM_URL, obj.getImagemUrl());
            values.put(COLUMN_POST_URL, obj.getPostUrl());
            values.put(COLUMN_POST_YOUTUBE_URL, obj.getYoutubeUrl());
            values.put(COLUMN_ID_AUTOR, obj.getIdAutor());

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

    public Post getPost(int id) {

        String sql = "SELECT * FROM " + TABELA + " WHERE " + COLUMN_ID + " = '" + id + "'";

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        Post obj = new Post();
        try {
            while (cursor.moveToNext()) {
                obj.setId(cursor.getInt(0));
                obj.setTitulo(cursor.getString(1));
                obj.setDescricao(cursor.getString(2));
                obj.setTexto(cursor.getString(3));
                obj.setData(cursor.getString(4));
                obj.setDataDescricao(cursor.getString(5));
                obj.setCategoria(cursor.getString(6));
                obj.setImagemUrl(cursor.getString(7));
                obj.setPostUrl(cursor.getString(8));
                obj.setYoutubeUrl(cursor.getString(9));
                obj.setIdAutor(cursor.getInt(10));
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

