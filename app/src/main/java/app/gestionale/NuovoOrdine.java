package app.gestionale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NuovoOrdine extends Fragment {

    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;
    private RelativeLayout layoutBottoni;
    private ProgressBar progressBar;
    private RelativeLayout layoutCaricamento;
    private Button btnNuovo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_nuovo_ordine, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);

        layoutBottoni = (RelativeLayout) view.findViewById(R.id.parteBottoni);
        layoutCaricamento = (RelativeLayout) view.findViewById(R.id.layoutCaricamento);
        progressBar = (ProgressBar) view.findViewById(R.id.caricamento);

        /*new Loading(progressBar, context) {

            @Override
            protected void onPreExecute() {
                layoutCaricamento.setVisibility(View.VISIBLE);
                layoutBottoni.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        impostaBottoni();
                    }
                });
                return super.doInBackground(voids);
            }

            @Override
            protected void onPostExecute(Void result) {
                layoutCaricamento.setVisibility(View.GONE);
                layoutBottoni.setVisibility(View.VISIBLE);

            }
        }.execute();*/
        impostaBottoni();

        return view;
    }

    private void impostaBottoni() {

        List<HashMap<String, Object>> risultatoQuery;
        risultatoQuery = DBmanager.selectQuery(EnumQuery.GET_LISTA_PIZZE.getValore());
        Iterator<HashMap<String, Object>> itr = risultatoQuery.iterator();
        if (itr.hasNext()) {
            int countElementi = 0;
            int countRiga = 0;
            boolean flag = false;
            while (itr.hasNext()) {
                HashMap<String, Object> riga = itr.next();
                final String nomePizza = riga.get("nome").toString();

                System.out.println("Elementi : " + layoutBottoni.getChildCount());

                RelativeLayout.LayoutParams layoutBtnDx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                Button btnPizza = nuovoBtn(nomePizza);
                if (layoutBottoni.getChildCount() > 0)
                    if (countElementi > 3) {
                        layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 1).getId());
                        flag = true;
                    } else
                        layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 1).getId());

                btnPizza.setLayoutParams(layoutBtnDx);
                if (flag) countElementi = 0;
                else countElementi++;
                layoutBottoni.addView(btnPizza);
            }
        }
    }

    private Button nuovoBtn(String nome) {
        btnNuovo = new Button(context);
        btnNuovo.setId(View.generateViewId());
        btnNuovo.setText(nome);
        btnNuovo.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.btn_width));
        btnNuovo.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.btn_height));
        return btnNuovo;
    }
}