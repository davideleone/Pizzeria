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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private RelativeLayout layoutIngredienti;
    private RelativeLayout contenitoreIngredienti;
    private String idColonna = "";
    private RelativeLayout layoutAggiunte;
    private RelativeLayout.LayoutParams paramAggiunte;
    private String nomeProdotto = "";


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

        HttpManager.execSimple("ELIMINA_ORDINI_TEMP", context);

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

    private void impostaBottoni(String idOrdine, Object param) {
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
                setNomeProdotto(nomeProdotto);
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


                btnModifica.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (contenitoreIngredienti != null)
                            contenitoreIngredienti.removeAllViews();
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

                        if (tableOrdiniPizza.getChildCount() == 0)
                            layoutContoPizze.setBackgroundResource(0);
                        if (tableOrdiniGastronomia.getChildCount() == 0)
                            layoutContoGastronomia.setBackgroundResource(0);
                        Toast.makeText(context, nomeProdotto + " eliminato!", Toast.LENGTH_SHORT).show();
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


    private void impostaCheckBox(String nomePizza, final Object param) {
        //LAYOUT CHE CONTIENE TUTTO
        contenitoreIngredienti = new RelativeLayout(context);
        //LAYOUT SUPERIORE CON TABELLA CHECKBOX
        layoutIngredienti = new RelativeLayout(context);
        layoutIngredienti.setId(View.generateViewId());
        RelativeLayout.LayoutParams paramLayoutIngredienti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLayoutIngredienti.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramLayoutIngredienti.setMargins(0, 20, 0, 0);
        layoutIngredienti.setLayoutParams(paramLayoutIngredienti);

        //TABELLA CON CHECKBOX
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            int countProdotti = 0;
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String nomeIngrediente = riga.get("nomeingrediente");
                final String idcolonna = riga.get("id_colonna");
                setIdColonna(idcolonna);
                RelativeLayout.LayoutParams layoutSelezione = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                CheckBox selezione = new CheckBox(context);
                selezione.setId(View.generateViewId());
                selezione.setPadding(5, 0, 5, 0);
                selezione.setTextSize(25);
                selezione.setChecked(true);
                selezione.setText(nomeIngrediente);
                selezione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked)
                            HttpManager.execSimple("AGGIUNGI_EXTRA", null, nomeIngrediente, idcolonna, nomeIngrediente, "2");
                    }
                });

                if (layoutIngredienti.getChildCount() > 0) {
                    if (countProdotti > 1)
                        if (countProdotti % 2 == 0) {
                            layoutSelezione.addRule(RelativeLayout.BELOW, layoutIngredienti.getChildAt(layoutIngredienti.getChildCount() - 2).getId());
                        } else {
                            layoutSelezione.addRule(RelativeLayout.BELOW, layoutIngredienti.getChildAt(layoutIngredienti.getChildCount() - 3).getId());
                            layoutSelezione.addRule(RelativeLayout.END_OF, layoutIngredienti.getChildAt(layoutIngredienti.getChildCount() - 1).getId());
                        }
                    else
                        layoutSelezione.addRule(RelativeLayout.END_OF, layoutIngredienti.getChildAt(layoutIngredienti.getChildCount() - 1).getId());
                }

                selezione.setLayoutParams(layoutSelezione);
                countProdotti++;
                layoutIngredienti.addView(selezione);
            }
        }

        contenitoreIngredienti.addView(layoutIngredienti);

        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramBarra.setMargins(30, 30, 30, 30);
        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramBarra.addRule(RelativeLayout.BELOW, layoutIngredienti.getId());

        View barraMezzo = new View(context);
        barraMezzo.setId(View.generateViewId());
        barraMezzo.setBackgroundColor(getResources().getColor(R.color.grigio));
        barraMezzo.setLayoutParams(paramBarra);
        contenitoreIngredienti.addView(barraMezzo);


        //LAYOUT INFERIORE CON TEXTVIEW
        layoutAggiunte = new RelativeLayout(context);
        RelativeLayout.LayoutParams paramLayoutAggiunte = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramLayoutAggiunte.addRule(RelativeLayout.BELOW, barraMezzo.getId());
        layoutAggiunte.setLayoutParams(paramLayoutAggiunte);
        paramAggiunte = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        paramAggiunte.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramAggiunte.setMargins(0, 20, 0, 0);


        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                riempiAggiunte(output);
            }
        }, null, "GET_AGGIUNTE", new String[]{}).execute();

        aggiunte.setLayoutParams(paramAggiunte);
        if (aggiunte.getParent() != null)
            ((ViewGroup) aggiunte.getParent()).removeView(aggiunte);
        layoutAggiunte.addView(aggiunte);

        aggiunte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                new HttpManager.AsyncManager(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        AggiungiExtra(nomeProdotto, output);
                    }
                }, null, "AGGIUNGI_EXTRA", new String[]{aggiunte.getText().toString(), idColonna, aggiunte.getText().toString(), "1"}).execute();
            }
        });

        contenitoreIngredienti.addView(layoutAggiunte);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio " + nomeProdotto);
        builder.setView(contenitoreIngredienti);
        builder.create().show();
    }

    private void AggiungiExtra(String nomeProdotto, Object param) {

        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String idExtra = riga.get("generated_id");

                Toast.makeText(context, aggiunte.getText().toString() + " inserito", Toast.LENGTH_SHORT).show();

                RelativeLayout.LayoutParams layoutSelezione = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutSelezione.addRule(RelativeLayout.BELOW, layoutAggiunte.getChildAt(layoutAggiunte.getChildCount() - 1).getId());
                layoutSelezione.addRule(RelativeLayout.CENTER_HORIZONTAL);

                final CheckBox newIngrediente = new CheckBox(context);
                newIngrediente.setLayoutParams(layoutSelezione);
                newIngrediente.setId(View.generateViewId());
                newIngrediente.setPadding(5, 0, 5, 0);
                newIngrediente.setTextSize(25);
                newIngrediente.setChecked(true);
                newIngrediente.setText(aggiunte.getText().toString());

                newIngrediente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked)
                            HttpManager.execSimple("TOGLI_EXTRA", null, idExtra);
                        layoutAggiunte.removeView(newIngrediente);
                    }
                });

                layoutAggiunte.addView(newIngrediente);
                aggiunte.setText("");
                paramAggiunte.addRule(RelativeLayout.BELOW, newIngrediente.getId());
                //aggiunte.setLayoutParams(paramAggiunte);
            }
        }
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

    private void setIdColonna(String idcolonna) {
        this.idColonna = idcolonna;
    }

    private void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

}