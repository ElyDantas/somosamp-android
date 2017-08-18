package br.org.pibfortaleza.somosamp.model;

/**
 * Created by Ely on 28/01/17.
 */

public class Imagem {

    private int id;

    private byte[] imagem;

    public Imagem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}
