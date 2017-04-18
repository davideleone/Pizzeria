package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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
import android.widget.ImageButton;
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
import java.util.Map;

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
    private RelativeLayout layoutAggiunte;
    private View barraMezzo;
    private RelativeLayout.LayoutParams paramAggiunte;

    private HashMap<String, Float> hashExtra;
    private List<String> tempExtraAgg = new ArrayList<String>();
    private SparseArray<TextView> sparsePrezziPizze = new SparseArray<TextView>();

    private TableLayout tableOrdiniPizza;
    private TableLayout tableOrdiniGastronomia;
    private TableLayout tableOrdiniBibite;

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
        bundle = this.getArguments();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_nuovo_ordine, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        RelativeLayout layoutInserimentoToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutInserimentoToolbar);
        ImageButton btnInserisci = (ImageButton) toolbar.findViewById(R.id.inserisciOrdine);
        layoutInserimentoToolbar.setVisibility(View.VISIBLE);
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
        }, null, "CREA_ORDINE", new String[]{}).execute();


        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout inserimentoLayout = new RelativeLayout(context);
                RelativeLayout.LayoutParams paramInserimento = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramInserimento.addRule(RelativeLayout.CENTER_HORIZONTAL);
                paramInserimento.addRule(RelativeLayout.CENTER_VERTICAL);
                paramInserimento.setMargins(0, 30, 0, 0);
                inserimentoLayout.setLayoutParams(paramInserimento);

                RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextInputLayout nomeInput = new TextInputLayout(context);
                editTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                editTextParams.setMargins(40, 0, 0, 40);
                nomeInput.setLayoutParams(editTextParams);
                nomeInput.setId(View.generateViewId());
                //nome.setLayoutParams(editTextParams);

                final TextInputEditText nome = new TextInputEditText(context);
                final TextInputEditText cognome = new TextInputEditText(context);
                final TextInputEditText telefono = new TextInputEditText(context);
                final CheckBox consegna = new CheckBox(context);
                final TextInputEditText via = new TextInputEditText(context);
                final TextInputEditText civico = new TextInputEditText(context);


                nome.setTextSize(25);
                nome.setHint("Nome");
                nomeInput.addView(nome);

                RelativeLayout.LayoutParams editTextCognomeParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout cognomeInput = new TextInputLayout(context);
                editTextCognomeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                editTextCognomeParams.addRule(RelativeLayout.BELOW, nomeInput.getId());
                editTextCognomeParams.setMargins(40, 0, 0, 40);
                cognomeInput.setLayoutParams(editTextCognomeParams);
                cognome.setTextSize(25);
                cognome.setHint("Cognome");
                cognomeInput.setId(View.generateViewId());
                cognomeInput.addView(cognome);

                RelativeLayout.LayoutParams editTextTelefonoParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout telefonoInput = new TextInputLayout(context);
                editTextTelefonoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                editTextTelefonoParams.addRule(RelativeLayout.BELOW, cognomeInput.getId());
                editTextTelefonoParams.setMargins(40, 0, 0, 40);
                telefonoInput.setLayoutParams(editTextTelefonoParams);
                telefono.setTextSize(25);
                telefono.setHint("Telefono");
                telefonoInput.setId(View.generateViewId());
                telefonoInput.addView(telefono);

                RelativeLayout.LayoutParams chechboxConsegnaParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                chechboxConsegnaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                chechboxConsegnaParams.addRule(RelativeLayout.BELOW, telefonoInput.getId());
                chechboxConsegnaParams.setMargins(40, 0, 0, 40);
                consegna.setText("Consegna");
                consegna.setTextSize(25);
                consegna.setLayoutParams(chechboxConsegnaParams);
                consegna.setId(View.generateViewId());

                RelativeLayout layoutConsegna = new RelativeLayout(context);
                RelativeLayout.LayoutParams layoutConsegnaParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutConsegnaParams.addRule(RelativeLayout.BELOW, consegna.getId());
                layoutConsegnaParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutConsegna.setLayoutParams(layoutConsegnaParams);
                layoutConsegna.setVisibility(View.GONE);

                RelativeLayout.LayoutParams editTextViaParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout viaInput = new TextInputLayout(context);
                editTextViaParams.setMargins(40, 0, 0, 40);
                viaInput.setLayoutParams(editTextViaParams);
                via.setTextSize(25);
                via.setHint("Via/P.zza/Loc.");
                viaInput.addView(via);
                viaInput.setId(View.generateViewId());


                RelativeLayout.LayoutParams editTextCivicoParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout civicoInput = new TextInputLayout(context);
                editTextCivicoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                editTextCivicoParams.addRule(RelativeLayout.END_OF, viaInput.getId());
                editTextCivicoParams.setMargins(40, 0, 0, 40);
                civicoInput.setLayoutParams(editTextCivicoParams);
                civico.setTextSize(25);
                civico.setHint("Civico");
                civicoInput.addView(civico);

                layoutConsegna.addView(viaInput);
                layoutConsegna.addView(civicoInput);

                inserimentoLayout.addView(nomeInput);
                inserimentoLayout.addView(cognomeInput);
                inserimentoLayout.addView(telefonoInput);
                inserimentoLayout.addView(consegna);
                inserimentoLayout.addView(layoutConsegna);


                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(inserimentoLayout)
                        .setTitle("Inserisci Nuovo Ordine")
                        .setPositiveButton("Inserisci", null)
                        .setNegativeButton("Annulla", null)
                        .create();

                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean toClose = true;
                        String strNome = nome.getText().toString();
                        String strCognome = cognome.getText().toString();

                        if (strNome.isEmpty()) {
                            toClose = false;
                            nome.setError("Inserisci nome");
                        }
                        if (strCognome.isEmpty()) {
                            toClose = false;
                            cognome.setError("Inserisci cognome");
                        }

                        if (toClose) {
                            new HttpManager.AsyncManager(new AsyncResponse() {
                                @Override
                                public void processFinish(Object output) {
                                    Toast.makeText(context, "Ordine completato!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }, context, "CREA_FATTORINO", new String[]{strNome, strCognome}).execute();
                        }
                    }
                });
            }

        });

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
                                creaConto(output);
                            }
                        }, null, "AGGIUNGI_PRODOTTO_TO_ORDINE", new String[]{nomeProdotto, ordine}).execute();
                    }
                });
            }
        }
    }

    private void creaConto(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String valColonna = riga.get("generated_id");
                new HttpManager.AsyncManager(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        inizializzaTabelle();
                        riempiConto(output);
                    }
                }, null, "GET_PRODOTTO_IN_ORDINE", new String[]{valColonna}).execute();
            }
        }
    }

    private void inizializzaTabelle() {
        TableLayout.LayoutParams layoutTabella = new TableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (tableOrdiniPizza == null) {
            tableOrdiniPizza = new TableLayout(context);
            tableOrdiniPizza.setLayoutParams(layoutTabella);
            tableOrdiniPizza.setGravity(Gravity.CENTER_HORIZONTAL);

            tableOrdiniGastronomia = new TableLayout(context);
            tableOrdiniGastronomia.setLayoutParams(layoutTabella);
            tableOrdiniGastronomia.setGravity(Gravity.CENTER_HORIZONTAL);

            tableOrdiniBibite = new TableLayout(context);
            tableOrdiniBibite.setLayoutParams(layoutTabella);
            tableOrdiniBibite.setGravity(Gravity.CENTER_HORIZONTAL);

            layoutContoPizze.addView(tableOrdiniPizza);
            layoutContoBibite.addView(tableOrdiniBibite);
            layoutContoGastronomia.addView(tableOrdiniGastronomia);
        }
    }

    private void rimuoviElemento(String tipo, TableRow toRemove) {
        switch (tipo) {
            case "Pizza":
                tableOrdiniPizza.removeView(toRemove);
                break;
            case "Bibita":
                tableOrdiniBibite.removeView(toRemove);
                break;
            case "Gastronomia":
                tableOrdiniGastronomia.removeView(toRemove);
                break;
        }
    }

    private void aggiornaTotale(float prezzo, boolean isSomma) {
        float current = Float.parseFloat((totale.getText().toString()).substring(0, totale.getText().toString().length() - 2));
        current = (isSomma) ? current + prezzo : current - prezzo;
        totale.setText(new DecimalFormat("#0.00 €").format(current));
    }

    private void riempiConto(final Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {

            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String valColonna = riga.get("id_colonna");
                final String tipo = riga.get("tipo");
                final String nomeProdotto = riga.get("nomeprodotto");
                final float prezzoFloat = Float.parseFloat(riga.get("prezzoprodotto"));
                final String prezzoString = new DecimalFormat("#0.00 €").format(prezzoFloat);

                aggiornaTotale(prezzoFloat, true);

                final TableRow rowPizza = new TableRow(context);
                TextView txtPizza;

                if (nomeProdotto.equals("PROSCIUTTO E FUNGHI"))
                    txtPizza = makeTableRowWithText("PROSC. E FUNGHI");
                else
                    txtPizza = makeTableRowWithText(nomeProdotto);

                rowPizza.addView(txtPizza);

                TextView txtPrezzo;
                txtPrezzo = makeTableRowWithText(prezzoString);
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
                                fixIngredientiExtra(output, Integer.parseInt(valColonna), nomeProdotto);
                            }
                        }, null, "GET_LISTA_BASE_CON_EXTRA", new String[]{valColonna, valColonna}).execute();
                    }
                });

                btnElimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpManager.execSimple("TOGLI_PRODOTTO_FROM_ORDINE", null, new String[]{valColonna});
                        /**
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
                         }*/
                        //rowPizza.setVisibility(View.GONE);
                        rimuoviElemento(tipo, rowPizza);
                        if (tableOrdiniPizza.getChildCount() == 0)
                            layoutContoPizze.setBackgroundResource(0);
                        if (tableOrdiniGastronomia.getChildCount() == 0)
                            layoutContoGastronomia.setBackgroundResource(0);
                        aggiornaTotale(Float.parseFloat((sparsePrezziPizze.get(Integer.parseInt(valColonna)).getText().toString()).substring(0, sparsePrezziPizze.get(Integer.parseInt(valColonna)).getText().toString().length() - 2)), false);
                        Toast.makeText(context, nomeProdotto + " eliminato!", Toast.LENGTH_SHORT).show();
                    }
                });

                switch (tipo) {
                    case "Pizza":
                        tableOrdiniPizza.addView(rowPizza);
                        sparsePrezziPizze.put(Integer.parseInt(valColonna), txtPrezzo);
                        break;
                    case "Bibita":
                        tableOrdiniBibite.addView(rowPizza);
                        break;
                    case "Gastronomia":
                        tableOrdiniGastronomia.addView(rowPizza);
                        break;
                }
            }
            if (tableOrdiniPizza.getChildCount() > 0)
                layoutContoPizze.setBackgroundResource(R.drawable.table_bottom_style);
            if (tableOrdiniGastronomia.getChildCount() > 0)
                layoutContoGastronomia.setBackgroundResource(R.drawable.table_bottom_style);
        }
    }

    private void inizializzaAggiunte(Object param, String valColonna, RelativeLayout baseLayout, final List<String> ingrBaseRimossi) {
        if (param != null) {
            final String idColonna = valColonna;
            List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
            Iterator<HashMap<String, String>> itrAgg = lista.iterator();
            hashExtra = new HashMap<String, Float>(lista.size());
            while (itrAgg.hasNext()) {
                HashMap<String, String> riga = itrAgg.next();
                final String nome = riga.get("nomeingrediente");
                final float prezzo = Float.parseFloat(riga.get("prezzo"));
                hashExtra.put(nome, prezzo);
            }
            adapterAggiunte = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(hashExtra.keySet()));

            barraMezzo = new View(context);
            aggiunte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                    if (ingrBaseRimossi.contains(aggiunte.getText().toString())) {
                        Toast.makeText(context, "Impossibile aggiungere l'ingrediente", Toast.LENGTH_SHORT).show();
                    } else {
                        tempExtraAgg.add(aggiunte.getText().toString());
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                aggiungiExtra(output);
                            }
                        }, null, "AGGIUNGI_EXTRA", new String[]{aggiunte.getText().toString(), idColonna, aggiunte.getText().toString(), "1"}).execute();
                    }
                }
            });
            if (aggiunte.getParent() != null)
                ((ViewGroup) aggiunte.getParent()).removeView(aggiunte);
            layoutAggiunte.addView(aggiunte);
            aggiunte.setText("");
        }

        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramBarra.setMargins(30, 30, 30, 30);
        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramBarra.addRule(RelativeLayout.BELOW, layoutIngredienti.getId());

        barraMezzo.setId(View.generateViewId());
        barraMezzo.setBackgroundColor(getResources().getColor(R.color.grigio));
        barraMezzo.setLayoutParams(paramBarra);

        //LAYOUT INFERIORE CON TEXTVIEW
        RelativeLayout.LayoutParams paramLayoutAggiunte = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLayoutAggiunte.addRule(RelativeLayout.BELOW, barraMezzo.getId());
        layoutAggiunte.setLayoutParams(paramLayoutAggiunte);
        paramAggiunte = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        paramAggiunte.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramAggiunte.setMargins(0, 20, 0, 0);
        paramAggiunte.addRule(RelativeLayout.BELOW, layoutAggiunte.getChildAt(layoutAggiunte.getChildCount() - 1).getId());
        aggiunte.setLayoutParams(paramAggiunte);
        aggiunte.setAdapter(adapterAggiunte);

        if (barraMezzo.getParent() != null)
            ((ViewGroup) barraMezzo.getParent()).removeView(barraMezzo);
        baseLayout.addView(barraMezzo);
        if (layoutAggiunte.getParent() != null)
            ((ViewGroup) layoutAggiunte.getParent()).removeView(layoutAggiunte);
        baseLayout.addView(layoutAggiunte);
    }

    private void fixIngredientiExtra(Object param, int idColonna, String nomePizza) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, Integer> hashExtra = new HashMap<String, Integer>();
        /**
         *
         * nomeingrediente        tipo
         Mozzarella               479
         Basilico                 479
         Olio E.v.o.              479
         Pomodoro                 479
         Grana                    1
         Pomodoro                 2
         Pomodoro                 1
         Basilico                 2
         */
        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final String nomeIngrediente = riga.get("nomeingrediente");
            final int tipoIngrediente = Integer.parseInt(riga.get("tipo"));
            if (hashExtra.get(nomeIngrediente) != null) {
                if (tipoIngrediente == 2) {
                    hashExtra.put(nomeIngrediente, 2); // TOGLI INGREDIENTE BASE
                } else if (hashExtra.get(nomeIngrediente) == idColonna || tipoIngrediente == idColonna) {
                    hashExtra.put(nomeIngrediente, 3); // DOPPIO INGREDIENTE BASE
                } else {
                    hashExtra.put(nomeIngrediente, 4); // DOPPIO EXTRA
                }
            } else {
                hashExtra.put(nomeIngrediente, tipoIngrediente);
            }
        }

        impostaExtra(nomePizza, idColonna, hashExtra);
    }


    private void impostaExtra(String nomePizza, final int valColonna, HashMap<String, Integer> hashExtra) {
        //LAYOUT CHE CONTIENE TUTTO
        contenitoreIngredienti = new RelativeLayout(context);
        //LAYOUT SUPERIORE CON TABELLA CHECKBOX
        layoutIngredienti = new RelativeLayout(context);
        layoutIngredienti.setId(View.generateViewId());
        RelativeLayout.LayoutParams paramLayoutIngredienti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLayoutIngredienti.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramLayoutIngredienti.setMargins(0, 20, 0, 0);
        layoutIngredienti.setLayoutParams(paramLayoutIngredienti);

        layoutAggiunte = new RelativeLayout(context);
        layoutAggiunte.setId(View.generateViewId());

        tempExtraAgg.clear();
        final List<String> ingrBaseRimossi = new ArrayList<String>();

        final String nomeProdotto = nomePizza;
        final String idColonna = Integer.toString(valColonna);

        //TABELLA CON CHECKBOX
        int countProdotti = 0;
        Iterator itr = hashExtra.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry riga = (Map.Entry) itr.next();
            final String nomeIngrediente = riga.getKey().toString();
            final int tipoIngrediente = Integer.parseInt(riga.getValue().toString());
            if (tipoIngrediente == 2 || tipoIngrediente == 3 || tipoIngrediente == valColonna) {
                RelativeLayout.LayoutParams layoutSelezione = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                final CheckBox selezione = new CheckBox(context);
                selezione.setId(View.generateViewId());
                selezione.setPadding(5, 0, 5, 0);
                selezione.setTextSize(25);
                selezione.setChecked(tipoIngrediente != 2);
                if (tipoIngrediente == 2) ingrBaseRimossi.add(nomeIngrediente);
                selezione.setText((tipoIngrediente == 3) ? "2 x " + nomeIngrediente : nomeIngrediente);
                selezione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            if (selezione.getText().toString().contains("2 x")) {
                                HttpManager.execSimple("ELIMINA_EXTRA_PER_NOME", null, idColonna, nomeIngrediente);
                                selezione.setChecked(true);
                                selezione.setText(nomeIngrediente);
                            } else if (!tempExtraAgg.contains(nomeIngrediente)) {
                                HttpManager.execSimple("AGGIUNGI_EXTRA", null, nomeIngrediente, idColonna, nomeIngrediente, "2");
                                ingrBaseRimossi.add(nomeIngrediente);
                            } else {
                                Toast.makeText(context, "Impossibile rimuovere l'ingrediente", Toast.LENGTH_SHORT).show();
                                selezione.setChecked(true);
                            }
                        } else {
                            HttpManager.execSimple("ELIMINA_EXTRA_PER_NOME", null, idColonna, nomeIngrediente);
                            ingrBaseRimossi.remove(nomeIngrediente);
                        }
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
            } else {
                RelativeLayout.LayoutParams layoutSelezione = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (layoutAggiunte.getChildCount() > 0)
                    layoutSelezione.addRule(RelativeLayout.BELOW, layoutAggiunte.getChildAt(layoutAggiunte.getChildCount() - 1).getId());
                layoutSelezione.addRule(RelativeLayout.CENTER_HORIZONTAL);

                final CheckBox newIngrediente = new CheckBox(context);
                newIngrediente.setLayoutParams(layoutSelezione);
                newIngrediente.setId(View.generateViewId());
                newIngrediente.setPadding(5, 0, 5, 0);
                newIngrediente.setTextSize(25);
                newIngrediente.setChecked(true);
                newIngrediente.setText((tipoIngrediente == 4) ? "2 x " + nomeIngrediente : nomeIngrediente);

                newIngrediente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        /**
                         if (!isChecked) {
                         HttpManager.execSimple("TOGLI_EXTRA", null, idExtra);
                         aggiornaTotale(prezzoIngrediente, false);
                         layoutAggiunte.removeView(newIngrediente);
                         }*/
                        if (!isChecked) {
                            if (newIngrediente.getText().toString().contains("2 x")) {
                                newIngrediente.setChecked(true);
                                newIngrediente.setText(nomeIngrediente);
                            }
                            HttpManager.execSimple("ELIMINA_EXTRA_PER_NOME", null, idColonna, nomeIngrediente);
                        }
                    }
                });

                layoutAggiunte.addView(newIngrediente);
            }
            itr.remove(); // avoids a ConcurrentModificationException
        }

        contenitoreIngredienti.addView(layoutIngredienti);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio " + nomeProdotto);
        builder.setView(contenitoreIngredienti);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                new HttpManager.AsyncManager(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        aggiornaTotale(Float.parseFloat((sparsePrezziPizze.get(valColonna).getText().toString()).substring(0, sparsePrezziPizze.get(valColonna).getText().toString().length() - 2)), false);
                        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) output;
                        Float prezzoFinale = Float.parseFloat(lista.get(0).get("prezzo_finale"));
                        sparsePrezziPizze.get(valColonna).setText(new DecimalFormat("#0.00 €").format(prezzoFinale));
                        aggiornaTotale(prezzoFinale, true);
                    }
                }, context, "GET_PREZZO_PIZZA_FINALE", new String[]{idColonna}).execute();
            }
        });

        if (hashExtra.isEmpty()) {
            new HttpManager.AsyncManager(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    inizializzaAggiunte(output, idColonna, contenitoreIngredienti, ingrBaseRimossi);
                    builder.create().show();
                }
            }, null, "GET_AGGIUNTE", new String[]{}).execute();
        } else {
            inizializzaAggiunte(null, idColonna, contenitoreIngredienti, ingrBaseRimossi);
            builder.create().show();
        }
    }

    private void aggiungiExtra(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String idExtra = riga.get("generated_id");
                final String nomeExtra = aggiunte.getText().toString();

                Toast.makeText(context, nomeExtra + " inserito", Toast.LENGTH_SHORT).show();
                //aggiornaTotale(prezzoIngrediente, true);

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
                        if (!isChecked) {
                            HttpManager.execSimple("ELIMINA_EXTRA_PER_ID", null, idExtra);
                            tempExtraAgg.remove(nomeExtra);
                            layoutAggiunte.removeView(newIngrediente);
                        }
                    }
                });

                layoutAggiunte.addView(newIngrediente);
                aggiunte.setText("");
                paramAggiunte.addRule(RelativeLayout.BELOW, newIngrediente.getId());
                //aggiunte.setLayoutParams(paramAggiunte);
            }
        }
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