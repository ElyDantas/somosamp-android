package br.org.pibfortaleza.somosamp.model;

import java.io.Serializable;

/**
 * Created by Ely on 28/01/17.
 */

public class Post implements Serializable {

    private int id;

    private String titulo;

    private String descricao;

    private String texto;

    private String data;

    private String dataDescricao;

    private String categoria;

    private String imagemUrl;

    private String autor;

    private String postUrl;

    private String youtubeUrl;

    private int idAutor;

    private int idImagem;

    public Post(String titulo, String texto, String data, String categoria, int idAutor, int idImagem, String descricao, String youtubeUrl) {
        this.titulo = titulo;
        this.texto = texto;
        this.data = data;
        this.categoria = categoria;
        this.idAutor = idAutor;
        this.idImagem = idImagem;
        this.descricao = descricao;
        this.youtubeUrl = youtubeUrl;
    }

    public Post() {

        this.titulo = "";
        this.texto = "";
        this.data = "";
        this.categoria = "";
        this.descricao = "";

    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(int idImagem) {
        this.idImagem = idImagem;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDataDescricao() {
        return dataDescricao;
    }

    public void setDataDescricao(String dataDescricao) {
        this.dataDescricao = dataDescricao;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }
}
