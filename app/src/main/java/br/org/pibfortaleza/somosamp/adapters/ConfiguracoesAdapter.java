package br.org.pibfortaleza.somosamp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;
import br.org.pibfortaleza.somosamp.asynctask.GetPostsFromSomosAmpAsyncTask;
import br.org.pibfortaleza.somosamp.dao.AutorDAO;
import br.org.pibfortaleza.somosamp.dao.ImagemDAO;
import br.org.pibfortaleza.somosamp.dao.PostDAO;
import br.org.pibfortaleza.somosamp.services.NotificationEventReceiver;

import static android.content.Context.MODE_PRIVATE;
import static br.org.pibfortaleza.somosamp.Util.Util.isOnline;

/**
 * Created by elydantas on 11/08/15.
 */
public class ConfiguracoesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private int[] mIcons;
    private String[] mItens;
    private String[] mContextItens;

    public ConfiguracoesAdapter(int[] pIcons, String[] pItens, String[] pContextItens, Context context) {
        this.mIcons = pIcons;
        this.mItens = pItens;
        this.mContextItens = pContextItens;
        this.mContext = context;
    }

    class ViewHolderSwitch extends RecyclerView.ViewHolder {
        private LinearLayout mRecyclerViewItem;
        private ImageView mImageView;
        private TextView mTextView;
        private TextView mTextViewContext;
        private SwitchCompat mSwitchNotificacao;

        public ViewHolderSwitch(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iconConfiguracoes);
            mTextView = (TextView) itemView.findViewById(R.id.itemConfiguracoes);
            mTextViewContext = (TextView) itemView.findViewById(R.id.itemContextoConfiguracoes);
            mRecyclerViewItem = (LinearLayout) itemView.findViewById(R.id.recyclerViewItem);
            mSwitchNotificacao = (SwitchCompat) itemView.findViewById(R.id.switchNotificacao);
        }
    }

    class ViewHolderDefault extends RecyclerView.ViewHolder {
        private LinearLayout mRecyclerViewItem;
        private ImageView mImageView;
        private TextView mTextView;
        private TextView mTextViewContext;

        public ViewHolderDefault(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iconConfiguracoes);
            mTextView = (TextView) itemView.findViewById(R.id.itemConfiguracoes);
            mTextViewContext = (TextView) itemView.findViewById(R.id.itemContextoConfiguracoes);
            mRecyclerViewItem = (LinearLayout) itemView.findViewById(R.id.recyclerViewItem);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mItens[position].equals(mContext.getResources().getString(R.string.notificacao_novo_post))) {
            return 2;
        } else {
            return super.getItemViewType(position);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_configuracoes, parent, false);
        View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_switch_configuracoes, parent, false);

        switch (viewType) {

            case 0:
                return new ViewHolderDefault(v);

            case 2:
                return new ViewHolderSwitch(v2);

        }

        return new ViewHolderDefault(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder paramHolder, int position) {

        switch (paramHolder.getItemViewType()) {
            case 0:

                final ViewHolderDefault holder = (ViewHolderDefault) paramHolder;
                holder.mImageView.setImageResource(mIcons[position]);
                holder.mTextView.setText(mItens[position]);
                holder.mTextViewContext.setText(mContextItens[position]);
                holder.mRecyclerViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (holder.getAdapterPosition()) {
                            case 1:

                                // Testar se existe conexão com a internet
                                if (isOnline(mContext)) {

                                    // Limpar todas as tabelas do banco de dados
                                    AutorDAO autorDAO = AutorDAO.getInstance(mContext);
                                    ImagemDAO imagemDAO = ImagemDAO.getInstance(mContext);
                                    PostDAO postDAO = PostDAO.getInstance(mContext);
                                    autorDAO.deleteAll();
                                    imagemDAO.deleteAll();
                                    postDAO.deleteAll();

                                    // Chamar asynctask para fazer o download dos artigos
                                    new GetPostsFromSomosAmpAsyncTask((MainActivity)mContext).execute();

                                } else {

                                    Toast.makeText(view.getContext(), mContext.getString(R.string.sem_conexao), Toast.LENGTH_SHORT).show();

                                }

                                break;
                            case 2:

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/html");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"elydantas.andrade@gmail.com"});
                                intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name) + " - " + mContext.getString(R.string.feedback));

                                try {
                                    mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.escolha_app)));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(view.getContext(), mContext.getString(R.string.no_email_clients_installed), Toast.LENGTH_SHORT).show();
                                }


                                break;
                        }


                    }
                });

                break;

            case 2:
                ViewHolderSwitch holderSwitch = (ViewHolderSwitch) paramHolder;

                final SharedPreferences spNotification = mContext.getSharedPreferences("PREFERENCE_UPDATE_NOTIFICATION_KEY", MODE_PRIVATE);

                if (spNotification.getBoolean("active_notification", false)) {
                    holderSwitch.mSwitchNotificacao.setChecked(true);
                } else {
                    holderSwitch.mSwitchNotificacao.setChecked(false);
                }

                holderSwitch.mImageView.setImageResource(mIcons[position]);
                holderSwitch.mTextView.setText(mItens[position]);
                holderSwitch.mTextViewContext.setText(mContextItens[position]);
                holderSwitch.mSwitchNotificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            // Ativar notificacoes
                            NotificationEventReceiver.setupAlarm(mContext);

                            SharedPreferences.Editor e = spNotification.edit();

                            e.putBoolean("active_notification", true);

                            e.commit();

                        } else {
                            // Desativar notificacoes
                            (NotificationEventReceiver.getDeleteIntent(mContext)).cancel();

                            SharedPreferences.Editor e = spNotification.edit();

                            e.putBoolean("active_notification", false);

                            e.commit();

                        }

                    }
                });

                break;
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItens.length;
    }
}