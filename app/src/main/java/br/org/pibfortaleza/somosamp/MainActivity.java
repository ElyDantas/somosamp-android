package br.org.pibfortaleza.somosamp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.asynctask.GetNewestPostsFromSomosAmpAsyncTask;
import br.org.pibfortaleza.somosamp.asynctask.GetPostsFromSomosAmpAsyncTask;
import br.org.pibfortaleza.somosamp.dao.AutorDAO;
import br.org.pibfortaleza.somosamp.dao.PostDAO;
import br.org.pibfortaleza.somosamp.fragments.AutoresFragment;
import br.org.pibfortaleza.somosamp.fragments.CategoriasFragment;
import br.org.pibfortaleza.somosamp.fragments.ConfiguracoesFragment;
import br.org.pibfortaleza.somosamp.fragments.ConhecaCadaUmaFragment;
import br.org.pibfortaleza.somosamp.fragments.InicioFragment;
import br.org.pibfortaleza.somosamp.fragments.LeituraPostFragment;
import br.org.pibfortaleza.somosamp.fragments.PdfWebViewFragment;
import br.org.pibfortaleza.somosamp.fragments.PostsFragment;
import br.org.pibfortaleza.somosamp.fragments.SobreFragment;
import br.org.pibfortaleza.somosamp.model.Post;
import br.org.pibfortaleza.somosamp.services.NotificationEventReceiver;

import static br.org.pibfortaleza.somosamp.Util.Util.isOnline;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InicioFragment.OnFragmentInteractionListener, SobreFragment.OnFragmentInteractionListener, AutoresFragment.OnFragmentInteractionListener, CategoriasFragment.OnFragmentInteractionListener, ConhecaCadaUmaFragment.OnFragmentInteractionListener, PostsFragment.OnFragmentInteractionListener, LeituraPostFragment.OnFragmentInteractionListener, ConfiguracoesFragment.OnFragmentInteractionListener, PdfWebViewFragment.OnFragmentInteractionListener {

    public static final String TAG_INICIO = "TAG_INICIO";
    public static final String TAG_SOBRE = "TAG_SOBRE";
    public static final String TAG_AUTORES = "TAG_AUTORES";
    public static final String TAG_CATEGORIAS = "TAG_CATEGORIAS";
    public static final String TAG_CONHECA_CADA_UMA = "TAG_CONHECA_CADA_UMA";
    public static final String TAG_POSTS = "TAG_POSTS";
    public static final String TAG_LEITURA_POST = "TAG_LEITURA_POST";
    private static final String TAG_CONFIGURACOES = "TAG_CONFIGURACOES";
    private static final String TAG_WEB_PDF = "TAG_WEB_PDF";
    private InicioFragment inicioFragment;
    private SobreFragment sobreFragment;
    private ConfiguracoesFragment configuracoesFragment;
    private AutoresFragment autoresFragment;
    private PdfWebViewFragment pdfWebViewFragment;
    private CategoriasFragment categoriasFragment;
    private ConhecaCadaUmaFragment conhecaCadaUmaFragment;
    private PostsFragment postsFragment;
    private LeituraPostFragment leituraPostFragment;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public PostDAO postDAO;
    public AutorDAO autorDAO;
    private ArrayList<Post> posts;
    private SharedPreferences sp;
    private Toolbar toolbar;
    public Post postLeituraAtual;
    private FloatingActionButton fabDeletar;
    private SharedPreferences spNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabDeletar = (FloatingActionButton) findViewById(R.id.fabDeletar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        postDAO = PostDAO.getInstance(getApplicationContext());

        autorDAO = AutorDAO.getInstance(getApplicationContext());

        posts = new ArrayList<>();

        firstTimeAppRun();

        Intent i = getIntent();

        int fragmentID = i.getIntExtra("FRAGMENT_IDENTIFIER", 0);

        if (savedInstanceState == null && fragmentID == 0) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new InicioFragment(), TAG_INICIO).commit();
        } else {

            if (savedInstanceState != null) {
                postLeituraAtual = (Post) savedInstanceState.getSerializable("postLeituraAtual");
            }

            if (fragmentID == 1) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SobreFragment(), TAG_SOBRE).commit();
            } else if (fragmentID == 2) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AutoresFragment(), TAG_AUTORES).commit();
            } else if (fragmentID == 3) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new CategoriasFragment(), TAG_CATEGORIAS).commit();
            }

        }

        // Verifica se existe novo post do #somosamp alocado nos Extras
        Post newPost = (Post) getIntent().getSerializableExtra("NEW_POST_FROM_SOMOSAMP");

        if (newPost != null) {
            if (!newPost.getTitulo().isEmpty()) {

                postLeituraAtual = newPost;

                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .add(R.id.container, new LeituraPostFragment(), TAG_LEITURA_POST)
                        .commit();

            }
        }

        fabDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postDAO.delPost(postDAO.listarPostes().get(0));

            }
        });

    }

    private void firstTimeAppRun() {

        spNotification = getApplicationContext().getSharedPreferences("PREFERENCE_UPDATE_NOTIFICATION_KEY", MODE_PRIVATE);

        if (!spNotification.getBoolean("active", false)) {

            System.out.println("NotificationEventReceiver ACTIVATED");

            NotificationEventReceiver.setupAlarm(getApplicationContext());

            SharedPreferences.Editor e = spNotification.edit();

            e.putBoolean("active", true);

            e.apply();

            if (!spNotification.getBoolean("active_notification", false)) {

                System.out.println("Notification ACTIVATED");

                e.putBoolean("active_notification", true);

                e.commit();

            }

        } else {
            System.out.println("NotificationEventReceiver is already ACTIVATED");
        }

        sp = getApplicationContext().getSharedPreferences("PREFERENCE_WELCOME_KEY", MODE_PRIVATE);

        boolean isFirstStart = sp.getBoolean("welcome", true);

        if (isFirstStart) {

            if (isOnline(this)) {

                new GetPostsFromSomosAmpAsyncTask(MainActivity.this).execute();

            } else {
                Toast.makeText(this, "Sem conexão a internet...", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("postLeituraAtual", postLeituraAtual);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                initGetNewestPostsFromSomosAmpAsyncTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checa true o item Inicio do Navigation View
     */
    private void checkInicioNavagationItem() {
        navigationView.getMenu().getItem(0).setChecked(true);
        for (int i = 1; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public void updatePosts() {
        recreate();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        if (id == R.id.nav_inicio) {

            showInicioFragment();

            drawer.closeDrawer(GravityCompat.START);

        } /*else if (id == R.id.nav_sobre) {

            sobreFragment = (SobreFragment) getSupportFragmentManager().findFragmentByTag(TAG_SOBRE);
            if (sobreFragment == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SobreFragment(), TAG_SOBRE).commit();
            } else {
                if (!sobreFragment.isVisible())
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, sobreFragment, TAG_SOBRE).commit();
            }

            drawer.closeDrawer(GravityCompat.START);*//*

        }*/ else if (id == R.id.nav_autores) {

            showAutoresFragment();

            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_categorias) {

            showCategoriasFragment();

            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_fotos) {

            Toast.makeText(getApplicationContext(), "Abrindo Dropbox...", Toast.LENGTH_SHORT).show();
            initActionView("com.dropbox.android", "https://www.dropbox.com/sh/vyghvu65h7gw3lp/AACP4Q8ZxRBQVya5HYvGDhWDa?dl=0");


        } else if (id == R.id.nav_onde_estamos) {

            navigationDrawerOptionOndeEstamos();


        } else if (id == R.id.nav_redes_sociais) {

            item.getActionView().findViewById(R.id.redes_sociais_facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Abrindo Facebook...", Toast.LENGTH_SHORT).show();
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL(getApplicationContext());
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    startActivity(facebookIntent);
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(10);

                }
            });

            item.getActionView().findViewById(R.id.redes_sociais_instagram).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Abrindo Instagram...", Toast.LENGTH_SHORT).show();
                    initActionView("com.instagram.android", "https://instagram.com/_u/somosamp");
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(10);

                }
            });

            item.getActionView().findViewById(R.id.redes_sociais_youtube).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getApplicationContext(), "Abrindo Youtube...", Toast.LENGTH_SHORT).show();
                    initActionView("com.google.android.youtube", "https://www.youtube.com/channel/UCEb-mQqWdp0lhuzdprOT2WQ");
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(10);

                }
            });

        } else if (id == R.id.nav_um_a_um_discipulado) {

            navigationDrawerOptionDiscipulado();

        } else if (id == R.id.nav_configuracoes) {

            showConfiguracoesFragment();

            drawer.closeDrawer(GravityCompat.START);

        }

        return true;
    }

    /**
     * Funcao que executa acao da opcao Autores do Navigation Drawer
     */
    private void showAutoresFragment() {
        autoresFragment = (AutoresFragment) getSupportFragmentManager().findFragmentByTag(TAG_AUTORES);
        if (autoresFragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AutoresFragment(), TAG_AUTORES).commit();
        } else {
            if (!autoresFragment.isVisible())
                getSupportFragmentManager().beginTransaction().replace(R.id.container, autoresFragment, TAG_AUTORES).commit();
        }
    }

    /**
     * Funcao que executa acao da opcao Inicio do Navigation Drawer
     */
    private void showInicioFragment() {

        inicioFragment = (InicioFragment) getSupportFragmentManager().findFragmentByTag(TAG_INICIO);
        if (inicioFragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
        } else {
            if (!inicioFragment.isVisible())
                getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
        }

    }

    /**
     * Funcao que executa acao da opcao de Categorias do Navigation Drawer
     */
    private void showCategoriasFragment() {

        categoriasFragment = (CategoriasFragment) getSupportFragmentManager().findFragmentByTag(TAG_CATEGORIAS);
        if (categoriasFragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new CategoriasFragment(), TAG_CATEGORIAS).commit();
        } else {
            if (!categoriasFragment.isVisible())
                getSupportFragmentManager().beginTransaction().replace(R.id.container, categoriasFragment, TAG_CATEGORIAS).commit();
        }

    }

    /**
     * Funcao que executa acao da opcao de Configuracoes do Navigation Drawer
     */
    private void showConfiguracoesFragment() {

        configuracoesFragment = (ConfiguracoesFragment) getSupportFragmentManager().findFragmentByTag(TAG_CONFIGURACOES);
        if (configuracoesFragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ConfiguracoesFragment(), TAG_CONFIGURACOES).commit();
        } else {
            if (!configuracoesFragment.isVisible())
                getSupportFragmentManager().beginTransaction().replace(R.id.container, configuracoesFragment, TAG_CONFIGURACOES).commit();
        }

    }

    /**
     * Funcao que executa acao da opcao de Onde-Estamos do Navigation Drawer
     */
    private void navigationDrawerOptionOndeEstamos() {

        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_youtube, null);

        LinearLayout linearLayoutAssistaCultoOnlineYoutube = (LinearLayout) layout.findViewById(R.id.linearLayoutAssistaCultoOnlineYoutube);
        LinearLayout linearLayoutGoogleMaps = (LinearLayout) layout.findViewById(R.id.linearLayoutGoogleMaps);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Onde estamos?")
                .setView(layout)
                .setMessage("\nTodo Sábado às 19h30.\n\nRua Silva Paulet, 1111.\n")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();

        linearLayoutAssistaCultoOnlineYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Abrindo Youtube...", Toast.LENGTH_SHORT).show();
                initActionView("com.google.android.youtube", "https://www.youtube.com/channel/UCEb-mQqWdp0lhuzdprOT2WQ");
                dialog.dismiss();

            }
        });

        linearLayoutGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Abrindo Google Maps...", Toast.LENGTH_SHORT).show();
                initActionView("com.google.android.apps.maps", "geo:-3.734641,-38.505349?q=Rua Silva Paulet, 1111 - Aldeota, Fortaleza - CE, 60120-020, Brasil");
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    /**
     * Funcao que executa acao da opcao de Discipulado do Navigation Drawer
     */
    private void navigationDrawerOptionDiscipulado() {

        spNotification = getApplicationContext().getSharedPreferences("PREFERENCE_DISCIPULADO_KEY", MODE_PRIVATE);

        if (!spNotification.getBoolean("active", false)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();

            View passwordView = inflater.inflate(R.layout.alert_dialog_password, null);

            final TextInputEditText passwordInputEditText = (TextInputEditText) passwordView.findViewById(R.id.passwordInputEditText);

            builder.setView(passwordView)
                    .setTitle(getString(R.string.informe_senha))
                    .setPositiveButton(R.string.entrar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            if (passwordInputEditText.getText().toString().equals("1a1amp2017")) {

                                SharedPreferences.Editor e = spNotification.edit();

                                e.putBoolean("active", true);

                                e.apply();

                                initPdfWebView(); // Inicializa PDF Webview

                                drawer.closeDrawer(GravityCompat.START);

                            } else {

                                Toast.makeText(getApplicationContext(), "Senha Incorreta", Toast.LENGTH_SHORT).show();

                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    });
            builder.create();
            builder.show();

        } else {

            initPdfWebView();
            drawer.closeDrawer(GravityCompat.START);

        }

    }

    /**
     * Inicializa Fragment para leitura de PDF no Webview
     */
    private void initPdfWebView() {

        pdfWebViewFragment = (PdfWebViewFragment) getSupportFragmentManager().findFragmentByTag(TAG_WEB_PDF);
        if (pdfWebViewFragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new PdfWebViewFragment(), TAG_WEB_PDF).commit();
        } else {
            if (!pdfWebViewFragment.isVisible())
                getSupportFragmentManager().beginTransaction().replace(R.id.container, pdfWebViewFragment, TAG_WEB_PDF).commit();
        }

    }

    /**
     * Inicializa Intent que chamara o aplicativo correto para abrir o conteudo respectivo
     *
     * @param packagename Nome do pacote
     * @param url         Url
     */
    private void initActionView(String packagename, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(packagename);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

    }

    /**
     * Retorna um boolean dizendo se pacote especifico esta instalado no celular do usuario
     *
     * @param packagename
     * @param packageManager
     * @return Boolean
     */
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public String getFacebookPageURL(Context context) {

        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + "https://www.facebook.com/SomosAMP";
            } else { //older versions of fb app
                return "fb://page/" + "SomosAMP";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "https://www.facebook.com/SomosAMP"; //normal web url
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void postsLoadedSucessfully() {

        sp = getApplicationContext().getSharedPreferences("PREFERENCE_WELCOME_KEY", MODE_PRIVATE);

        if (sp.getBoolean("welcome", true)) {

            SharedPreferences.Editor e = sp.edit();

            e.putBoolean("welcome", false); // we save the value "false", indicating that it is no longer the first appstart

            e.commit();

        }

        recreate();

    }

    private void sendNotification(String msg) {

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());

    }

    public void initGetNewestPostsFromSomosAmpAsyncTask() {

        if (isOnline(this)) {

            posts = postDAO.listarPostes();

            if (posts.size() == 0) {
                // Inicia o asynctask de carregar posts do 0
                new GetPostsFromSomosAmpAsyncTask(MainActivity.this).execute();
            } else {
                // Inicia o asynctask de atualiar os posts do app
                new GetNewestPostsFromSomosAmpAsyncTask(this, postDAO.listarPostes()).execute();
                Toast.makeText(this, "Atualizando...", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Sem conexão a internet...", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            inicioFragment = (InicioFragment) getSupportFragmentManager().findFragmentByTag(TAG_INICIO);
            pdfWebViewFragment = (PdfWebViewFragment) getSupportFragmentManager().findFragmentByTag(TAG_WEB_PDF);
            configuracoesFragment = (ConfiguracoesFragment) getSupportFragmentManager().findFragmentByTag(TAG_CONFIGURACOES);
            sobreFragment = (SobreFragment) getSupportFragmentManager().findFragmentByTag(TAG_SOBRE);
            autoresFragment = (AutoresFragment) getSupportFragmentManager().findFragmentByTag(TAG_AUTORES);
            categoriasFragment = (CategoriasFragment) getSupportFragmentManager().findFragmentByTag(TAG_CATEGORIAS);
            conhecaCadaUmaFragment = (ConhecaCadaUmaFragment) getSupportFragmentManager().findFragmentByTag(TAG_CONHECA_CADA_UMA);
            postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentByTag(TAG_POSTS);
            leituraPostFragment = (LeituraPostFragment) getSupportFragmentManager().findFragmentByTag(TAG_LEITURA_POST);


            if (sobreFragment != null && sobreFragment.isVisible()) {
                checkInicioNavagationItem();
                if (inicioFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
                }
            } else if (autoresFragment != null && autoresFragment.isVisible()) {
                checkInicioNavagationItem();

                if (postsFragment != null && postsFragment.isVisible()) {
                    if (leituraPostFragment != null && leituraPostFragment.isVisible()) {
                        getSupportFragmentManager().beginTransaction().remove(leituraPostFragment).commit();
                    } else {
                        if (getSupportActionBar() != null)
                            getSupportActionBar().setTitle(getString(R.string.autores));
                        getSupportFragmentManager().beginTransaction().remove(postsFragment).commit();
                    }
                } else {

                    if (inicioFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
                    }
                }
            } else if (categoriasFragment != null && categoriasFragment.isVisible()) {

                if (conhecaCadaUmaFragment != null && conhecaCadaUmaFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction().remove(conhecaCadaUmaFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, categoriasFragment, TAG_CATEGORIAS).commit();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(getString(R.string.categorias));


                } else {

                    if (postsFragment != null && postsFragment.isVisible()) {

                        if (leituraPostFragment != null && leituraPostFragment.isVisible()) {
                            getSupportFragmentManager().beginTransaction().remove(leituraPostFragment).commit();
                        } else {
                            if (getSupportActionBar() != null)
                                getSupportActionBar().setTitle(getString(R.string.categorias));
                            getSupportFragmentManager().beginTransaction().remove(postsFragment).commit();
                        }

                    } else {

                        checkInicioNavagationItem();
                        if (inicioFragment != null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
                        }

                    }

                }

            } else if (configuracoesFragment != null && configuracoesFragment.isVisible()) {
                checkInicioNavagationItem();
                if (inicioFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
                }
            } else if (pdfWebViewFragment != null && pdfWebViewFragment.isVisible()) {
                checkInicioNavagationItem();
                if (inicioFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, inicioFragment, TAG_INICIO).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new InicioFragment(), TAG_INICIO).commit();
                }
            } else {

                if (leituraPostFragment != null && leituraPostFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction().remove(leituraPostFragment).commit();
                } else {
                    super.onBackPressed();
                }

            }
        }

    }

}
