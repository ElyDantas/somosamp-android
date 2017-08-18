package br.org.pibfortaleza.somosamp.asynctask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;
import br.org.pibfortaleza.somosamp.dao.AutorDAO;
import br.org.pibfortaleza.somosamp.dao.PostDAO;
import br.org.pibfortaleza.somosamp.model.Autor;
import br.org.pibfortaleza.somosamp.model.Post;
import br.org.pibfortaleza.somosamp.services.NotificationEventReceiver;

import static br.org.pibfortaleza.somosamp.Util.Constants.AUTORES_URL;
import static br.org.pibfortaleza.somosamp.Util.Constants.NOTIFICATION_POST_UPDATED_ID;
import static br.org.pibfortaleza.somosamp.Util.Constants.SOMOSAMP_URL;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getAutorLinks;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getPostFromUrl;
import static br.org.pibfortaleza.somosamp.Util.PageParserUtil.getPostLinks;
import static br.org.pibfortaleza.somosamp.Util.Util.getNotificationIcon;


/**
 * Created by rafael on 23/5/16.
 */
public class ServiceGetNewestPostsFromSomosAmpAsyncTask extends AsyncTask<String, Void, Boolean> {

    private Context context;

    private AutorDAO autorDAO;

    private PostDAO postDAO;

    private ArrayList<Post> appPostUrls;

    private int qtdPostsAntes;

    private int qtdPostsDepois;

    private Post post;

    private ArrayList<Autor> autores;

    public ServiceGetNewestPostsFromSomosAmpAsyncTask(Context context, ArrayList<Post> appPostUrls) {
        this.context = context;
        this.autorDAO = AutorDAO.getInstance(context);
        this.postDAO = PostDAO.getInstance(context);
        this.appPostUrls = appPostUrls;
        this.qtdPostsAntes = appPostUrls.size();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... urls) {

        // Fazer a captura da lista de autores
        autores = getAutorLinks(AUTORES_URL);

        // Atualiza autores no BD
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

            boolean contem = false;

            while (flagLinks) {

                ArrayList<String> postsMaisAntigos = getPostLinks(SOMOSAMP_URL + "page/" + contador);

                if (postsMaisAntigos != null) {

                    // Verifica se ultimo post do app esta na página recuperada atual
                    for (String p : postsMaisAntigos) {
                        if (appPostUrls.size() > 0) {
                            if (p.equals(appPostUrls.get(0).getPostUrl())) {
                                contem = true;
                                break;
                            }
                        }
                    }

                    // Encerra o loop quando ultimo post do app for achado na página recuperada atual
                    if (contem) {
                        flagLinks = false;
                        postUrls.addAll(postsMaisAntigos);
                    } else {
                        postUrls.addAll(postsMaisAntigos);
                    }

                } else {
                    flagLinks = false;
                }
                contador++;

            }

            for (int i = 0; i < appPostUrls.size(); i++) {
                for (int j = 0; j < postUrls.size(); j++) {

                    if (appPostUrls.get(i).getPostUrl().equals(postUrls.get(j))) {
                        postUrls.remove(j);
                        j--;
                    }

                }
            }

            // Recuperar as informações de cada post por meio do link
            ArrayList<Post> posts = new ArrayList<>();
            for (String url : postUrls) {
                posts.add(getPostFromUrl(url));
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

            ArrayList<Post> postListAtualizado = postDAO.listarPostes();

            this.qtdPostsDepois = postListAtualizado.size();

            if (postListAtualizado.size() >= 1) {
                this.post = postListAtualizado.get(0);
            } else {
                this.post = new Post();
            }

            return true;
        } else {
            return false;
        }

    }


    protected void onPostExecute(Boolean success) {
        if (success) {

            if (qtdPostsDepois > qtdPostsAntes) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("NEW_POST_FROM_SOMOSAMP",post);

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle("#SOMOSAMP")
                        .setContentText("Post novo !!! Confere Aqui :)")
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .setSmallIcon(getNotificationIcon());

                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        NOTIFICATION_POST_UPDATED_ID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(context));

                final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_POST_UPDATED_ID, builder.build());

            }

        } else {
            Toast.makeText(context, "Ocorreu um erro. Tente novamente...", Toast.LENGTH_LONG).show();
        }
    }
}


