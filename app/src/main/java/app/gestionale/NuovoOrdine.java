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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NuovoOrdine extends Fragment {

    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;
    private RelativeLayout layoutBottoniPizze;
    private RelativeLayout layoutBottoniBibite;
    private RelativeLayout layoutBottoniGastronomia;
    private ProgressBar progressBar;
    private RelativeLayout layoutCaricamento;
    private Button btnNuovo;
    private RelativeLayout layoutContoPizze;
    private RelativeLayout layoutContoBibite;
    private RelativeLayout layoutContoGastronomia;
    private ArrayAdapter<String> adapterAggiunte;
    private AutoCompleteTextView aggiunte;
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

        layoutBottoniPizze = (RelativeLayout) view.findViewById(R.id.parteBottoni);
        layoutBottoniBibite = (RelativeLayout) view.findViewById(R.id.layoutBottoniBibite);
        layoutBottoniGastronomia = (RelativeLayout) view.findViewById(R.id.layoutBottoniGastronomia);

        layoutCaricamento = (RelativeLayout) view.findViewById(R.id.layoutCaricamento);
        progressBar = (ProgressBar) view.findViewById(R.id.caricamento);
        layoutContoPizze = (RelativeLayout) view.findViewById(R.id.riassuntoOrdinePizze);
        layoutContoBibite = (RelativeLayout) view.findViewById(R.id.riassuntoOrdineBibite);
        layoutContoGastronomia = (RelativeLayout) view.findViewById(R.id.riassuntoOrdineGastronomia);
        totale = (TextView) view.findViewById(R.id.totaleEuro);
        aggiunte = new AutoCompleteTextView(context);

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

        HttpManager.execSimple("ELIMINA_ORDINI_TEMP", context, new String[]{});

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
        }, null, "GET_LISTA_PRODOTTI", new String[]{}).execute();
    }

    private void impostaBottoni(final String idOrdine, Object param) {
        final String ordine = idOrdine;
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            int countPizze = 0;
            int countBibite = 0;
            int countGastronomia = 0;
            while (itr.hasNext()) {

                HashMap<String, String> riga = itr.next();
                final String nomeProdotto = riga.get("nome");
                final String tipo = riga.get("tipo");
                Button btnProdotto = null;

                if (tipo.equals("Pizza")) {
                    RelativeLayout.LayoutParams layoutBtnDx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if (nomeProdotto.equals("PROSCIUTTO E FUNGHI"))
                        btnProdotto = nuovoBtn("PROSC. E FUNGHI");
                    else
                        btnProdotto = nuovoBtn(nomeProdotto);

                    if (layoutBottoniPizze.getChildCount() > 0) {
                        if (countPizze > 3)
                            if (countPizze % 4 == 0) {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniPizze.getChildAt(layoutBottoniPizze.getChildCount() - 3).getId());
                            } else {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniPizze.getChildAt(layoutBottoniPizze.getChildCount() - 4).getId());
                                layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniPizze.getChildAt(layoutBottoniPizze.getChildCount() - 1).getId());
                            }
                        else
                            layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniPizze.getChildAt(layoutBottoniPizze.getChildCount() - 1).getId());
                    }

                    btnProdotto.setLayoutParams(layoutBtnDx);
                    countPizze++;
                    layoutBottoniPizze.addView(btnProdotto);


                } else if (tipo.equals("Bibita")) {
                    RelativeLayout.LayoutParams layoutBtnDx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    btnProdotto = nuovoBtn(nomeProdotto);

                    if (layoutBottoniBibite.getChildCount() > 0) {
                        if (countBibite > 2)
                            if (countBibite % 3 == 0) {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniBibite.getChildAt(layoutBottoniBibite.getChildCount() - 3).getId());
                            } else {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniBibite.getChildAt(layoutBottoniBibite.getChildCount() - 4).getId());
                                layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniBibite.getChildAt(layoutBottoniBibite.getChildCount() - 1).getId());
                            }
                        else
                            layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniBibite.getChildAt(layoutBottoniBibite.getChildCount() - 1).getId());
                    }

                    btnProdotto.setLayoutParams(layoutBtnDx);
                    countBibite++;
                    layoutBottoniBibite.addView(btnProdotto);
                } else if (tipo.equals("Gastronomia")) {
                    RelativeLayout.LayoutParams layoutBtnDx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    btnProdotto = nuovoBtn(nomeProdotto);

                    if (layoutBottoniGastronomia.getChildCount() > 0) {
                        if (countGastronomia > 2)
                            if (countGastronomia % 3 == 0) {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniGastronomia.getChildAt(layoutBottoniGastronomia.getChildCount() - 3).getId());
                            } else {
                                layoutBtnDx.addRule(RelativeLayout.BELOW, layoutBottoniGastronomia.getChildAt(layoutBottoniGastronomia.getChildCount() - 4).getId());
                                layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniGastronomia.getChildAt(layoutBottoniGastronomia.getChildCount() - 1).getId());
                            }
                        else
                            layoutBtnDx.addRule(RelativeLayout.END_OF, layoutBottoniGastronomia.getChildAt(layoutBottoniGastronomia.getChildCount() - 1).getId());
                    }

                    btnProdotto.setLayoutParams(layoutBtnDx);
                    countGastronomia++;
                    layoutBottoniGastronomia.addView(btnProdotto);
                }

                btnProdotto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                Toast.makeText(context, "Prodotto Inserito!", Toast.LENGTH_SHORT).show();
                                creaConto(ordine, output);
                            }

                            ;
                        }, null, "AGGIUNGI_PRODOTTO_TO_ORDINE", new String[]{nomeProdotto, ordine}).execute();
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
        }, null, "GET_PRODOTTO_IN_ORDINE", new String[]{idOrdine}).execute();
    }

    private void riempiConto(final Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            final TableLayout tableOrdiniPizza = new TableLayout(context);
            TableLayout.LayoutParams layoutTabella = new TableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tableOrdiniPizza.setLayoutParams(layoutTabella);
            tableOrdiniPizza.setGravity(Gravity.CENTER_HORIZONTAL);
            final TableLayout tableOrdiniGastronomia = new TableLayout(context);
            tableOrdiniGastronomia.setLayoutParams(layoutTabella);
            tableOrdiniGastronomia.setGravity(Gravity.CENTER_HORIZONTAL);
            final TableLayout tableOrdiniBibite = new TableLayout(context);
            tableOrdiniBibite.setLayoutParams(layoutTabella);
            tableOrdiniBibite.setGravity(Gravity.CENTER_HORIZONTAL);
            float totaleOrdine = 0;
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String idColonna = riga.get("id_colonna");
                final String tipo = riga.get("tipo");
                final String nomeProdotto = riga.get("nomeprodotto");
                totaleOrdine += Float.parseFloat(riga.get("prezzoprodotto"));
                final String prezzoProdotto = new DecimalFormat("#0.00 €").format((double) Float.parseFloat(riga.get("prezzoprodotto")));

                totale.setText(new DecimalFormat("#0.00 €").format(totaleOrdine));

                final TableRow rowPizza = new TableRow(context);
                TextView txtPizza;

                if (nomeProdotto.equals("PROSCIUTTO E FUNGHI"))
                    txtPizza = makeTableRowWithText("PROSC. E FUNGHI");
                else
                    txtPizza = makeTableRowWithText(nomeProdotto);

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
                                impostaCheckBox(nomeProdotto, output);
                            }
                        }, null, "GET_LISTA_INGREDIENTI", new String[]{nomeProdotto}).execute();
                    }
                });

                btnElimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpManager.execSimple("TOGLI_PRODOTTO_FROM_ORDINE", null, new String[]{idColonna});
                        switch (tipo) {
                            case "Pizza":
                                tableOrdiniPizza.removeView(rowPizza);
                                break;
                            case "Bibita":
                                tableOrdiniBibite.removeView(rowPizza);
                                break;
                            case "Gastronomia":
                                tableOrdiniGastronomia.removeView(rowPizza);
                                break;
                        }
                        Toast.makeText(context, nomeProdotto+" eliminato!", Toast.LENGTH_SHORT).show();
                    }
                });

                switch (tipo) {
                    case "Pizza":
                        tableOrdiniPizza.addView(rowPizza);
                        break;
                    case "Bibita":
                        tableOrdiniBibite.addView(rowPizza);
                        break;
                    case "Gastronomia":
                        tableOrdiniGastronomia.addView(rowPizza);
                        break;
                }
            }
            layoutContoPizze.addView(tableOrdiniPizza);
            if (tableOrdiniPizza.getChildCount() > 0)
                layoutContoPizze.setBackgroundResource(R.drawable.table_bottom_style);
            layoutContoBibite.addView(tableOrdiniBibite);
            layoutContoGastronomia.addView(tableOrdiniGastronomia);
            if (tableOrdiniGastronomia.getChildCount() > 0)
                layoutContoGastronomia.setBackgroundResource(R.drawable.table_bottom_style);

        }
    }


    private void impostaCheckBox(String nomePizza, Object param) {
        final RelativeLayout layoutIngredienti = new RelativeLayout(context);
        TableLayout contenitore = new TableLayout(context);
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            RelativeLayout.LayoutParams paramsIngredienti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsIngredienti.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsIngredienti.setMargins(0, 20, 0, 0);
            contenitore.setId(View.generateViewId());
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
                selezione.setTextSize(25);
                selezione.setChecked(true);
                selezione.setText(nomeIngrediente);
                row.addView(selezione);


                selezione.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selezione.isChecked())
                            HttpManager.execSimple("AGGIUNGI_EXTRA", null, nomeIngrediente, idcolonna, nomeIngrediente, "2");
                    }
                });

                final CheckBox selezione2;

                if (itr.hasNext()) {
                    riga = itr.next();
                    final String nomeIngrediente2 = riga.get("nomeingrediente");
                    selezione2 = new CheckBox(context);
                    selezione2.setId(View.generateViewId());
                    selezione2.setPadding(5, 0, 5, 0);
                    selezione2.setTextSize(25);
                    selezione2.setChecked(true);
                    selezione2.setText(nomeIngrediente2);
                    row.addView(selezione2);

                    selezione2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!selezione2.isChecked())
                                HttpManager.execSimple("AGGIUNGI_EXTRA", null, nomeIngrediente2, idcolonna, nomeIngrediente2, "2");
                        }
                    });
                }
                contenitore.addView(row);

                aggiunte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        HttpManager.execSimple("AGGIUNGI_EXTRA", null, aggiunte.getText().toString(), idcolonna, aggiunte.getText().toString(), "1");
                        Toast.makeText(context, aggiunte.getText().toString() + " inserito", Toast.LENGTH_SHORT).show();


                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(aggiunte.getWindowToken(), 0);


                        RelativeLayout.LayoutParams layoutSelezione = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutSelezione.addRule(RelativeLayout.BELOW, layoutIngredienti.getChildAt(layoutIngredienti.getChildCount() - 1).getId());

                        final CheckBox newIngrediente = new CheckBox(context);
                        newIngrediente.setLayoutParams(layoutSelezione);
                        newIngrediente.setId(View.generateViewId());
                        newIngrediente.setPadding(5, 0, 5, 0);
                        newIngrediente.setTextSize(25);
                        newIngrediente.setChecked(true);
                        newIngrediente.setText(aggiunte.getText().toString());

                        layoutIngredienti.addView(newIngrediente);


                        aggiunte.setText("");
                    }
                });
            }
            layoutIngredienti.addView(contenitore);
        }

        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramBarra.setMargins(30, 30, 30, 30);
        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramBarra.addRule(RelativeLayout.BELOW, contenitore.getId());

        View barraMezzo = new View(context);
        barraMezzo.setId(View.generateViewId());
        barraMezzo.setBackgroundColor(getResources().getColor(R.color.grigio));
        barraMezzo.setLayoutParams(paramBarra);
        layoutIngredienti.addView(barraMezzo);

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                riempiAggiunte(output);
            }
        }, null, "GET_AGGIUNTE", new String[]{}).execute();

        RelativeLayout.LayoutParams paramAggiunte = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        paramAggiunte.addRule(RelativeLayout.BELOW, barraMezzo.getId());
        paramAggiunte.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramAggiunte.setMargins(0, 20, 0, 0);
        aggiunte.setLayoutParams(paramAggiunte);
        layoutIngredienti.addView(aggiunte);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio " + nomePizza);
        builder.setView(layoutIngredienti);
        builder.create().show();
    }

    private void riempiAggiunte(Object param) {
        final ArrayList<String> listaAggiunte = new ArrayList<>();
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itrAgg = lista.iterator();
        while (itrAgg.hasNext()) {
            HashMap<String, String> riga2 = itrAgg.next();
            listaAggiunte.add(riga2.get("nomeingrediente"));
        }

        adapterAggiunte = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, listaAggiunte);
        aggiunte.setAdapter(adapterAggiunte);
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