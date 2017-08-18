package br.org.pibfortaleza.somosamp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;
import br.org.pibfortaleza.somosamp.dao.AutorDAO;
import br.org.pibfortaleza.somosamp.fragments.LeituraPostFragment;
import br.org.pibfortaleza.somosamp.model.Post;

import static br.org.pibfortaleza.somosamp.MainActivity.TAG_LEITURA_POST;
import static br.org.pibfortaleza.somosamp.Util.Util.fromHtml;

/**
 * Created by elydantas on 28/08/15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<Post> mLista;
    private AutorDAO autorDAO;

    public PostAdapter(Activity activity, ArrayList<Post> mLista) {
        this.activity = activity;
        this.mLista = mLista;
        this.autorDAO = AutorDAO.getInstance(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private final CardView cardViewPost;
        private final ImageView imageViewImagem;
        private final TextView textViewTitulo;
        private final TextView textViewTexto;
        private final TextView textViewDataDescricao;
        private final TextView textViewNomeAutor;
        private final ImageView imageViewBookmark;
        private final TextView textViewImageTextPlaceHolder;


        public ViewHolder(View itemView) {
            super(itemView);

            imageViewBookmark = (ImageView) itemView.findViewById(R.id.imageViewBookmark);

            imageViewImagem = (ImageView) itemView.findViewById(R.id.imageViewImagem);

            textViewImageTextPlaceHolder = (TextView) itemView.findViewById(R.id.textViewImageTextPlaceHolder);

            textViewTitulo = (TextView) itemView.findViewById(R.id.textViewTitulo);

            textViewNomeAutor = (TextView) itemView.findViewById(R.id.textViewNomeAutor);

            textViewTexto = (TextView) itemView.findViewById(R.id.textViewTexto);

            textViewDataDescricao = (TextView) itemView.findViewById(R.id.textViewDataDescricao);

            cardViewPost = (CardView) itemView.findViewById(R.id.cardViewPost);

            mContext = itemView.getContext();

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mLista.get(position).getImagemUrl() != null) {
            if (mLista.get(position).getImagemUrl().trim().length() > 0) {

                Picasso.with(holder.mContext)
                        .load(mLista.get(position).getImagemUrl())
                        .into(holder.imageViewImagem);

                holder.textViewImageTextPlaceHolder.setVisibility(View.GONE);

            } else {
                holder.textViewImageTextPlaceHolder.setVisibility(View.VISIBLE);
            }
        }

        String autor = autorDAO.getAutor(mLista.get(holder.getAdapterPosition()).getIdAutor()).getNome();

        String categoria = mLista.get(holder.getAdapterPosition()).getCategoria();

        if (autor != null) {
            if (autor.equals(categoria)) {
                holder.textViewNomeAutor.setText(autor);
            } else {
                holder.textViewNomeAutor.setText(autor + " - " + mLista.get(holder.getAdapterPosition()).getCategoria());
            }
        }

        holder.textViewTitulo.setText(mLista.get(position).getTitulo());

        holder.textViewTexto.setText(fromHtml(mLista.get(position).getTexto()));

        holder.textViewDataDescricao.setText(mLista.get(position).getDataDescricao());

        holder.cardViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) activity).postLeituraAtual = mLista.get(holder.getAdapterPosition());
                ((MainActivity) activity).getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .add(R.id.container, new LeituraPostFragment(), TAG_LEITURA_POST)
                        .commit();

            }

        });

    }

    @Override
    public int getItemCount() {
        return mLista.size();
    }

    public void deleteItem(int index) {
        mLista.remove(index);
        notifyItemRemoved(index);
    }

    public void clear() {
        mLista.clear();
    }

    public void addAll(ArrayList<Post> paramList) {
        mLista.addAll(paramList);
    }

    public void setmListaHospitais(ArrayList<Post> mLista) {
        this.mLista = mLista;
    }

}
