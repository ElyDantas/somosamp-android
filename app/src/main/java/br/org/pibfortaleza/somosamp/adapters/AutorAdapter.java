package br.org.pibfortaleza.somosamp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;
import br.org.pibfortaleza.somosamp.fragments.PostsFragment;
import br.org.pibfortaleza.somosamp.fragments.SobreFragment;
import br.org.pibfortaleza.somosamp.model.Autor;

import static br.org.pibfortaleza.somosamp.MainActivity.TAG_POSTS;

/**
 * Created by elydantas on 28/08/15.
 */
public class AutorAdapter extends RecyclerView.Adapter<AutorAdapter.ViewHolder> {

    private final Activity activity;
    private ArrayList<Autor> mLista;

    public AutorAdapter(Activity activity,ArrayList<Autor> mLista) {
        this.activity = activity;
        this.mLista = mLista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewFotoAutor;
        private final TextView textViewNomeAutor;
        private final TextView textViewQtd;
        private final Context mContext;
        private final CardView cardViewAutor;


        public ViewHolder(View itemView) {
            super(itemView);

            imageViewFotoAutor = (ImageView) itemView.findViewById(R.id.imageViewFotoAutor);

            textViewNomeAutor = (TextView) itemView.findViewById(R.id.textViewNomeAutor);

            textViewQtd = (TextView) itemView.findViewById(R.id.textViewQtd);

            cardViewAutor = (CardView) itemView.findViewById(R.id.cardViewAutor);

            mContext = itemView.getContext();

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.autor_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.textViewNomeAutor.setText(mLista.get(position).getNome());

        holder.textViewQtd.setText(String.valueOf(mLista.get(position).getNumPosts()));

        holder.cardViewAutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)activity).getSupportFragmentManager().beginTransaction().add(R.id.container, PostsFragment.newInstance("AUTOR",String.valueOf(mLista.get(holder.getAdapterPosition()).getId()),mLista.get(holder.getAdapterPosition()).getNome()), TAG_POSTS).commit();
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

    public void addAll(ArrayList<Autor> paramList) {
        mLista.addAll(paramList);

    }

    public void setmListaHospitais(ArrayList<Autor> mLista) {
        this.mLista = mLista;
    }

}
