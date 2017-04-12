package app.gestionale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
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
    private RelativeLayout layoutConto;
    private TextView recyclableTextView;


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
        layoutConto = (RelativeLayout) view.findViewById(R.id.riassuntoOrdine);

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

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                creaOrdine(output);
            }

            ;
        }, null, "CREA_ORDINE", new String[]{}).execute();


        return view;
    }

    private void creaOrdine(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, String> riga = itr.next();
        final String idOrdine = riga.get("generated_id");


        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                impostaBottoni(idOrdine, output);
            }
        }, null, "GET_LISTA_PIZZE", new String[]{}).execute();
    }

    private void impostaBottoni(String idOrdine, Object param) {
        final String ordine = idOrdine;
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            int countElementi = 0;
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String nomePizza = riga.get("nome");

                RelativeLayout.LayoutParams layoutBtnDx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                Button btnPizza;

                if (nomePizza.equals("PROSCIUTTO E FUNGHI"))
                    btnPizza = nuovoBtn("PROSC. E FUNGHI");
                else
                    btnPizza = nuovoBtn(nomePizza);

                if (layoutBottoni.getChildCount() > 0) {
                    if (countElementi > 3)
                        if (countElementi % 4 == 0) {
                            layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 3).getId());
                        } else {
                            layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 4).getId());
                            layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 1).getId());
                        }
                    else
                        layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoni.getChildAt(layoutBottoni.getChildCount() - 1).getId());
                }

                btnPizza.setLayoutParams(layoutBtnDx);
                countElementi++;
                layoutBottoni.addView(btnPizza);

                btnPizza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                Toast.makeText(context, "Pizza Inserita!", Toast.LENGTH_SHORT).show();
                                creaConto(ordine, output);
                            }

                            ;
                        }, null, "AGGIUNGI_PRODOTTO_TO_ORDINE", new String[]{nomePizza, ordine}).execute();
                    }
                });

            }
        }
    }

    private void creaConto(String idOrdine, Object param) {
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                riempiConto(output);
            }

            ;
        }, null, "GET_PIZZA_IN_ORDINE", new String[]{idOrdine}).execute();
    }

    private void riempiConto(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            TableLayout tableOrdini = new TableLayout(context);
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String nomePizza = riga.get("nomeprodotto");
                final String prezzoProdotto = new DecimalFormat("#0.00 €").format((double) Float.parseFloat(riga.get("prezzoprodotto")));

                TableLayout.LayoutParams layoutTabella = new TableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                tableOrdini.setLayoutParams(layoutTabella);

                TableRow rowPizza = new TableRow(context);
                TextView txtPizza;

                if (nomePizza.equals("PROSCIUTTO E FUNGHI"))
                    txtPizza = makeTableRowWithText("PROSC. E FUNGHI");
                else
                    txtPizza = makeTableRowWithText(nomePizza);

                rowPizza.addView(txtPizza);

                TextView txtPrezzo;
                txtPrezzo = makeTableRowWithText(prezzoProdotto);
                rowPizza.addView(txtPrezzo);

                Button btnModifica = new Button(context);
                btnModifica.setText("Modifica");
                rowPizza.addView(btnModifica);


                Button btnElimina = new Button(context);
                btnElimina.setText("Elimina");
                rowPizza.addView(btnElimina);

                /*RelativeLayout.LayoutParams paramTolti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramTolti.addRule(RelativeLayout.BELOW, txtPizza.getId());
                paramTolti.setMargins(40, 0, 0, 0);

                TextView nomeingredientiTolti = new TextView(context);
                nomeingredientiTolti.setText("NO ");
                nomeingredientiTolti.setId(View.generateViewId());
                nomeingredientiTolti.setTextAppearance(context, R.style.testoPiccolo);
                nomeingredientiTolti.setMaxEms(10);
                nomeingredientiTolti.setLayoutParams(paramTolti);

                if (!DBmanager.selectQuery(EnumQuery.LISTA_EXTRA_TOLTI.getValore(), idcolonna).isEmpty()) {
                    Iterator<HashMap<String, Object>> itrTolti = DBmanager.selectQuery(EnumQuery.LISTA_EXTRA_TOLTI.getValore(), idcolonna).iterator();
                    while (itrTolti.hasNext()) {
                        HashMap<String, Object> riga2 = itrTolti.next();
                        if (itrTolti.hasNext()) {
                            nomeingredientiTolti.setText(nomeingredientiTolti.getText() + riga2.get("nomeextra").toString() + ", ");
                        } else {
                            nomeingredientiTolti.setText(nomeingredientiTolti.getText() + riga2.get("nomeextra").toString() + "");
                        }
                    }
                    flag = true;
                    layoutPizze.addView(nomeingredientiTolti);
                }

                RelativeLayout.LayoutParams paramAggiunti = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramAggiunti.setMargins(40, 0, 0, 0);
                TextView nomeingredientiAggiunti = new TextView(context);

                paramAggiunti.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
                if (flag)
                    paramAggiunti.addRule(RelativeLayout.ALIGN_START, nomeingredientiTolti.getId());
                nomeingredientiAggiunti.setText("PIU' ");
                nomeingredientiAggiunti.setTextAppearance(context, R.style.testoPiccolo);
                nomeingredientiAggiunti.setMaxEms(10);
                nomeingredientiAggiunti.setLayoutParams(paramAggiunti);

                if (!DBmanager.selectQuery(EnumQuery.LISTA_EXTRA_AGGIUNTI.getValore(), idcolonna).isEmpty()) {
                    Iterator<HashMap<String, Object>> itrAggiunti = DBmanager.selectQuery(EnumQuery.LISTA_EXTRA_AGGIUNTI.getValore(), idcolonna).iterator();
                    while (itrAggiunti.hasNext()) {
                        HashMap<String, Object> riga2 = itrAggiunti.next();
                        if (itrAggiunti.hasNext()) {
                            nomeingredientiAggiunti.setText(nomeingredientiAggiunti.getText() + riga2.get("nomeextra").toString() + ", ");
                        } else {
                            nomeingredientiAggiunti.setText(nomeingredientiAggiunti.getText() + riga2.get("nomeextra").toString() + "");
                        }
                    }
                    layoutPizze.addView(nomeingredientiAggiunti);
                }*/


                tableOrdini.addView(rowPizza);


            }
            layoutConto.addView(tableOrdini);
        }
    }


    private TextView makeTableRowWithText(String text) {
        recyclableTextView = new TextView(context);
        recyclableTextView.setId(View.generateViewId());
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setTextColor(getResources().getColor(R.color.giallo));
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_150dp));
        recyclableTextView.setText(text);
        recyclableTextView.setTextSize(25);
        return recyclableTextView;
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