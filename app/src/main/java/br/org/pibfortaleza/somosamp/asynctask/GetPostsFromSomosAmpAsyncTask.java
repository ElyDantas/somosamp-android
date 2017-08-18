package br.org.pibfortaleza.somosamp.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;
import br.org.pibfortaleza.somosamp.dao.AutorDAO;
import br.org.pibfortaleza.somosamp.dao.ImagemDAO;
import br.org.pibfortaleza.somosamp.dao.PostDAO;
import br.org.pibfortaleza.somosamp.model.Autor;
import br.org.pibfortaleza.somosamp.model.Post;

import static br.org.pibfortaleza.somosamp.Util.Constants.AUTORES_URL;
import static br.org.pibfortaleza.somosamp.Util.Constants.SOMOSAMP_URL;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getAutorLinks;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getPostFromUrl;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getPostLinks;


/**
 * Created by rafael on 23/5/16.
 */
public class GetPostsFromSomosAmpAsyncTask extends AsyncTask<String, Void, Boolean> {

    private Activity context;

    private AutorDAO autorDAO;

    private PostDAO postDAO;

    private ProgressDialog progressDialog;

    private ArrayList<Autor> autores;

    public GetPostsFromSomosAmpAsyncTask(Activity context) {
        this.context = context;
        this.autorDAO = AutorDAO.getInstance(context);
        this.postDAO = PostDAO.getInstance(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int resId = context.getResources().getIdentifier("carregando_posts", "string", context.getPackageName());

        this.progressDialog.setMessage(context.getResources().getString(resId));

        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        this.progressDialog.setIndeterminate(false);

        this.progressDialog.setMax(100);

        this.progressDialog.show();

    }

    @Override
    protected Boolean doInBackground(String... urls) {

        // Fazer a captura da lista de autores
        autores = getAutorLinks(AUTORES_URL);

        this.progressDialog.incrementProgressBy(20);

        if (autores != null) {

            for (Autor a : autores) {
                // O metodo de Salvar Autor se encarrega de não haver autores duplicados
                autorDAO.salvarAutor(a);
            }

            // Atualiza lista de autores (_ID)
            autores = autorDAO.listarAutores();

            // Fazer a captura dos links dos posts pela pagina inicial do #SOMOSAMP
            ArrayList<String> postUrls = new ArrayList<>();

            boolean flagLinks = true;

            int contador = 1;

            while (flagLinks) {

                ArrayList<String> postsMaisAntigos = getPostLinks(SOMOSAMP_URL + "page/" + contador);

                if (postsMaisAntigos != null) {
                    postUrls.addAll(postsMaisAntigos);
                } else {
                    flagLinks = false;
                }
                contador++;

            }

            // Recuperar as informações de cada post por meio do link
            ArrayList<Post> posts = new ArrayList<>();
            for (String url : postUrls) {

                Post post = getPostFromUrl(url);

                if (post != null)
                    posts.add(post);

                this.progressDialog.incrementProgressBy(80 / postUrls.size());

            }

            // Adiciona o id do autor no obj post
            for (Post p : posts) {
                for (Autor a : autores) {
                        if (p.getAutor().equals(a.getNome()))
                            p.setIdAutor(a.getId());

                }

                // Salva post no BD
                p.setId(postDAO.salvarPost(p));
            }

            return true;
        } else {
            return false;
        }

    }


    protected void onPostExecute(Boolean success) {

        this.progressDialog.dismiss();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (success) {
            ((MainActivity) context).postsLoadedSucessfully();
        } else {
            Toast.makeText(context, "Ocorreu um erro. Tente novamente...", Toast.LENGTH_LONG).show();
        }
    }
}