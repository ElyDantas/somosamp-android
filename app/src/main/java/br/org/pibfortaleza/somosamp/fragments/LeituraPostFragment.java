package br.org.pibfortaleza.somosamp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import br.org.pibfortaleza.somosamp.MainActivity;
import br.org.pibfortaleza.somosamp.R;

import static br.org.pibfortaleza.somosamp.Util.Util.fromHtml;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LeituraPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LeituraPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeituraPostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;

    private ImageView imageViewImagem;

    private TextView textViewTitulo;

    private TextView textViewTexto;

    private TextView textViewDataDescricao;

    private TextView textViewNomeAutor;

    private MainActivity main;

    private YouTubePlayer youTubePlayer;

    private FrameLayout youtube_fragment;

    public LeituraPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeituraPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeituraPostFragment newInstance(String param1, String param2) {
        LeituraPostFragment fragment = new LeituraPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_leitura_post, container, false);

        main = ((MainActivity) getActivity());

        youtube_fragment = (FrameLayout) view.findViewById(R.id.youtube_fragment);

        imageViewImagem = (ImageView) view.findViewById(R.id.imageViewImagem);

        textViewTitulo = (TextView) view.findViewById(R.id.textViewTitulo);

        textViewTexto = (TextView) view.findViewById(R.id.textViewTexto);

        textViewNomeAutor = (TextView) view.findViewById(R.id.textViewNomeAutor);

        textViewDataDescricao = (TextView) view.findViewById(R.id.textViewDataDescricao);

        textViewTitulo.setText(main.postLeituraAtual.getTitulo());

        textViewNomeAutor.setText(main.autorDAO.getAutor(main.postLeituraAtual.getIdAutor()).getNome());

        textViewTexto.setText(fromHtml(main.postLeituraAtual.getTexto()));

        textViewDataDescricao.setText(main.postLeituraAtual.getDataDescricao());

        if (main.postLeituraAtual.getImagemUrl() != null) {
            if (main.postLeituraAtual.getImagemUrl().trim().length() > 0) {
                Picasso.with(getContext())
                        .load(main.postLeituraAtual.getImagemUrl())
                        .placeholder(R.drawable.logo_amp)
                        .into(imageViewImagem);
            } else {
                imageViewImagem.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.logo_amp));
            }
        }

        if (main.postLeituraAtual.getYoutubeUrl() != null) {
            if (!main.postLeituraAtual.getYoutubeUrl().equals("")) {

                //startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(mLista.get(position).getYoutubeUrl())));

                imageViewImagem.setVisibility(View.GONE);

                youtube_fragment.setVisibility(View.VISIBLE);

                final YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

                youTubePlayerFragment.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                        if (!wasRestored) {
                            youTubePlayer = player;
                            youTubePlayer.loadVideo(main.postLeituraAtual.getYoutubeUrl());
                            youTubePlayer.play();
                        }

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                        // TODO Auto-generated method stub

                    }
                });

                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.youtube_fragment, youTubePlayerFragment)
                        .commit();


            }
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
