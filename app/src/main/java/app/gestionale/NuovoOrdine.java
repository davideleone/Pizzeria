package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.CheckBox;
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
    private TextView totale;


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
        totale = (TextView) view.findViewById(R.id.totaleEuro);

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

        HttpManager.execSimple("ELIMINA_ORDINI_TEMP", null, new String[]{});

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

    private void riempiConto(final Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            TableLayout tableOrdini = new TableLayout(context);
            float totaleOrdine = 0;
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String nomePizza = riga.get("nomeprodotto");
                totaleOrdine += Float.parseFloat(riga.get("prezzoprodotto"));
                final String prezzoProdotto = new DecimalFormat("#0.00 €").format((double) Float.parseFloat(riga.get("prezzoprodotto")));

                totale.setText(new DecimalFormat("#0.00 €").format(totaleOrdine));


                TableLayout.LayoutParams layoutTabella = new TableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                tableOrdini.setLayoutParams(layoutTabella);
                tableOrdini.setGravity(Gravity.CENTER_HORIZONTAL);

                TableRow rowPizza = new TableRow(context);
                TextView txtPizza;

                if (nomePizza.equals("PROSCIUTTO E FUNGHI"))
                    txtPizza = makeTableRowWithText("PROSC. E FUNGHI");
                else
                    txtPizza = makeTableRowWithText(nomePizza);

                rowPizza.addView(txtPizza);

                TextView txtPrezzo;
                txtPrezzo = makeTableRowWithText(prezzoProdotto);
                txtPrezzo.setGravity(Gravity.CENTER);
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

                btnModifica.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                impostaCheckBox(nomePizza, output);
                            }
                        }, null, "GET_LISTA_INGREDIENTI", new String[]{nomePizza}).execute();
                    }
                });


                tableOrdini.addView(rowPizza);


            }
            layoutConto.addView(tableOrdini);
        }
    }


    private void impostaCheckBox(String nomePizza, Object param) {
        RelativeLayout layoutIngredienti = new RelativeLayout(context);
        ;
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            RelativeLayout.LayoutParams paramsIngredienti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsIngredienti.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsIngredienti.setMargins(0, 20, 0, 0);
            TableLayout contenitore = new TableLayout(context);
            contenitore.setLayoutParams(paramsIngredienti);

            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String nomeIngrediente = riga.get("nomeingrediente");
                final String idcolonna = riga.get("id_colonna");
                TableRow row = new TableRow(context);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                final CheckBox selezione = new CheckBox(context);
                selezione.setId(View.generateViewId());
                selezione.setPadding(5, 0, 5, 0);
                selezione.setTextSize(18);
                selezione.setChecked(true);
                selezione.setText(nomeIngrediente);
                row.addView(selezione);


                selezione.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!selezione.isChecked())
                            HttpManager.execSimple("AGGIUNGI_EXTRA", null, new String[]{nomeIngrediente, idcolonna, nomeIngrediente, "2"});
                    }
                });

                final CheckBox selezione2;

                if (itr.hasNext()) {
                    riga = itr.next();
                    final String nomeIngrediente2 = riga.get("nomeingrediente");
                    selezione2 = new CheckBox(context);
                    selezione2.setId(View.generateViewId());
                    selezione2.setPadding(5, 0, 5, 0);
                    selezione2.setTextSize(18);
                    selezione2.setChecked(true);
                    selezione2.setText(nomeIngrediente2);
                    row.addView(selezione2);

                    selezione2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!selezione2.isChecked())
                                HttpManager.execSimple("AGGIUNGI_EXTRA", null, new String[]{nomeIngrediente2, idcolonna, nomeIngrediente2, "2"});
                        }
                    });
                }
                contenitore.addView(row);
            }
            layoutIngredienti.addView(contenitore);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio " + nomePizza);
        builder.setView(layoutIngredienti);
        builder.create().show();
    }

    private TextView makeTableRowWithText(String text) {
        recyclableTextView = new TextView(context);
        recyclableTextView.setId(View.generateViewId());
        recyclableTextView.setTextColor(getResources().getColor(R.color.giallo));
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_200dp));
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