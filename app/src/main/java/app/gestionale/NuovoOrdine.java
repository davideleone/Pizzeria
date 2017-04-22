package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.SparseArray;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
    private SparseArray<TextView> sparsePrezziProdotti = new SparseArray<TextView>();

    private TableLayout tableOrdiniPizza;
    private TableLayout tableOrdiniGastronomia;
    private TableLayout tableOrdiniBibite;
    private ArrayAdapter<String> adapterCitta;
    private Spinner spinnerCitta;
    private ArrayAdapter<String> arrayDate;
    private Spinner spinnerDate;
    private ArrayList<String> listaOre;
    private ArrayAdapter<String> adapterOre;
    private Spinner spinnerOre;
    private String strVia = "";
    private String strCivico = "";
    private String strCitta = "";
    private String idOrdine = "";
    private String cognomeTrovato = "";
    private CheckBox consegna;
    private RadioButton isMetro;
    private RelativeLayout layoutContoMetri;
    private boolean nuovoMetro = false;
    private float totMetriAggiornato = -1;
    private int idMetro_onMod = -1;
    private RelativeLayout layoutBarra_onMod = null;
    private ArrayList<SparseArray<HashMap<String, String>>> clienti;
    private ArrayAdapter adapterClienti;
    private ArrayList<String> listaClienti;
    private ArrayList<String> listaCitta;
    private String nomeTrovato = "";
    private String viaTrovato = "";
    private String telefonoTrovato = "";
    private ArrayList<HashMap<String, String>> listaProdotti;


    private SparseArray<HashMap<TableLayout, List<Integer>>> sparseMetri = new SparseArray<HashMap<TableLayout, List<Integer>>>();
/*    final int contOrdini[] = ne4w int[listaOre.size()];
    final int[] nPizze = new int[listaOre.size()];*/

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
        Button btnInserisci = (Button) toolbar.findViewById(R.id.inserisciOrdine);
        layoutInserimentoToolbar.setVisibility(View.VISIBLE);
        context = view.getContext();
        super.onCreate(savedInstanceState);

        clienti = (ArrayList<SparseArray<HashMap<String, String>>>) bundle.getSerializable("LISTA_CLIENTI");
        listaCitta = (ArrayList<String>) bundle.getSerializable("LISTA_CITTA");
        listaProdotti = (ArrayList<HashMap<String, String>>) bundle.getSerializable("LISTA_PRODOTTI");


        adapterCitta = new ArrayAdapter<String>(context, R.layout.date_spinner_new_orders, listaCitta);


        isMetro = (RadioButton) view.findViewById(R.id.mezzoMetro);
        final ImageButton spezzaPizza = (ImageButton) view.findViewById(R.id.spezzaPizze);

        isMetro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spezzaPizza.setVisibility((isChecked) ? View.VISIBLE : View.GONE);
            }
        });

        spezzaPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovoMetro = true;
                Toast.makeText(context, "Nuova Pizza-Metro predisposta", Toast.LENGTH_SHORT).show();
            }
        });

        layoutBottoniPizze = (RelativeLayout) view.findViewById(R.id.parteBottoni);
        layoutBottoniBibite = (RelativeLayout) view.findViewById(R.id.layoutBottoniBibite);
        layoutBottoniGastronomia = (RelativeLayout) view.findViewById(R.id.layoutBottoniGastronomia);

        layoutCaricamento = (RelativeLayout) view.findViewById(R.id.layoutCaricamento);
        progressBar = (ProgressBar) view.findViewById(R.id.caricamento);
        layoutContoMetri = (RelativeLayout) view.findViewById(R.id.riassuntoOrdineMetri);
        layoutContoPizze = (RelativeLayout) view.findViewById(R.id.riassuntoOrdinePizze);
        layoutContoBibite = (RelativeLayout) view.findViewById(R.id.riassuntoOrdineBibite);
        layoutContoGastronomia = (RelativeLayout) view.findViewById(R.id.riassuntoOrdineGastronomia);
        totale = (TextView) view.findViewById(R.id.totaleEuro);
        aggiunte = new AutoCompleteTextView(context);

        inizializzaTabelle();

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                creaOrdine(output);
            }
        }, null, "CREA_ORDINE", new String[]{}).execute();


        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aggiornaTotaliMetri();

                /*new HttpManager.AsyncManager(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        checkOrario(output);
                    }
                }, context, "CHECK_ORARIO", new String[]{}).execute();*/

                ScrollView scrollView = new ScrollView(context);

                RelativeLayout inserimentoLayout = new RelativeLayout(context);
                RelativeLayout.LayoutParams paramInserimento = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramInserimento.addRule(RelativeLayout.CENTER_HORIZONTAL);
                paramInserimento.addRule(RelativeLayout.CENTER_VERTICAL);
                paramInserimento.setMargins(0, 30, 0, 0);
                inserimentoLayout.setLayoutParams(paramInserimento);

                RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                paramBarra.setMargins(30, 20, 30, 10);
                paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);

                View view = new View(context);
                view.setId(View.generateViewId());
                view.setBackgroundColor(getResources().getColor(R.color.celeste));
                view.setLayoutParams(paramBarra);
                inserimentoLayout.addView(view);

                final TextInputEditText nome = new TextInputEditText(context);
                final AutoCompleteTextView cognome = new AutoCompleteTextView(context);
                final TextInputEditText telefono = new TextInputEditText(context);
                final TextInputEditText via = new TextInputEditText(context);
                final TextInputEditText civico = new TextInputEditText(context);
                final TextInputEditText citta = new TextInputEditText(context);

                RelativeLayout.LayoutParams editTextCognomeParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout cognomeInput = new TextInputLayout(context);
                editTextCognomeParams.addRule(RelativeLayout.BELOW, view.getId());
                cognomeInput.setId(View.generateViewId());
                editTextCognomeParams.setMargins(40, 15, 0, 40);
                cognomeInput.setLayoutParams(editTextCognomeParams);
                listaClienti = getListaCognomi();
                adapterClienti = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(listaClienti));
                cognome.setAdapter(adapterClienti);

                cognome.setTextSize(25);
                cognome.setHint("Cognome");
                cognomeInput.addView(cognome);

                RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextParams.addRule(RelativeLayout.BELOW, cognomeInput.getId());
                TextInputLayout nomeInput = new TextInputLayout(context);
                editTextParams.setMargins(40, 0, 0, 40);
                nomeInput.setLayoutParams(editTextParams);
                nomeInput.setId(View.generateViewId());
                nome.setTextSize(25);
                nome.setHint("Nome");
                nomeInput.addView(nome);

                RelativeLayout.LayoutParams editTextTelefonoParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout telefonoInput = new TextInputLayout(context);
                telefonoInput.setId(View.generateViewId());
                editTextTelefonoParams.addRule(RelativeLayout.BELOW, nomeInput.getId());
                editTextTelefonoParams.setMargins(40, 0, 0, 40);
                telefonoInput.setLayoutParams(editTextTelefonoParams);
                telefono.setTextSize(25);
                telefono.setHint("Telefono");
                telefono.setInputType(InputType.TYPE_CLASS_PHONE);
                telefonoInput.addView(telefono);

                RelativeLayout.LayoutParams editTextChechboxParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                consegna = new CheckBox(context);
                consegna.setId(View.generateViewId());
                editTextChechboxParams.addRule(RelativeLayout.BELOW, telefonoInput.getId());
                editTextChechboxParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                editTextChechboxParams.setMargins(40, 0, 0, 40);
                consegna.setLayoutParams(editTextChechboxParams);
                consegna.setTextSize(25);
                consegna.setText("Consegna");

                RelativeLayout.LayoutParams editLayoutConsegnaParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final RelativeLayout layoutConsegna = new RelativeLayout(context);
                editLayoutConsegnaParams.addRule(RelativeLayout.BELOW, consegna.getId());
                layoutConsegna.setId(View.generateViewId());
                layoutConsegna.setLayoutParams(editLayoutConsegnaParams);
                layoutConsegna.setVisibility(View.GONE);

                consegna.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            layoutConsegna.setVisibility(View.GONE);
                        } else {
                            layoutConsegna.setVisibility(View.VISIBLE);
                        }
                    }
                });

                RelativeLayout.LayoutParams editTextViaParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout viaInput = new TextInputLayout(context);
                viaInput.setId(View.generateViewId());
                editTextViaParams.setMargins(40, 0, 0, 40);
                viaInput.setLayoutParams(editTextViaParams);
                via.setTextSize(25);
                via.setHint("Via/P.zza/Loc.");
                if (!viaTrovato.isEmpty())
                    via.setText(viaTrovato);
                viaInput.addView(via);

                RelativeLayout.LayoutParams editTextCittaTxtParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_100dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextCittaTxtParams.addRule(RelativeLayout.BELOW, viaInput.getId());
                editTextCittaTxtParams.setMargins(40, 0, 0, 40);

                TextView txtCitta = new TextView(context);
                txtCitta.setTextSize(25);
                txtCitta.setText("Citta'");
                txtCitta.setId(View.generateViewId());
                txtCitta.setLayoutParams(editTextCittaTxtParams);

                RelativeLayout.LayoutParams editTextCittaSpinnerParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextCittaSpinnerParams.addRule(RelativeLayout.BELOW, viaInput.getId());
                editTextCittaSpinnerParams.addRule(RelativeLayout.END_OF, txtCitta.getId());
                editTextCittaSpinnerParams.addRule(RelativeLayout.ALIGN_BASELINE, txtCitta.getId());

                editTextCittaSpinnerParams.setMargins(40, 0, 0, 40);

                spinnerCitta = new Spinner(context);
                spinnerCitta.setAdapter(adapterCitta);
                spinnerCitta.setLayoutParams(editTextCittaSpinnerParams);

                RelativeLayout.LayoutParams editTextDataTxtParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_100dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextDataTxtParams.addRule(RelativeLayout.BELOW, layoutConsegna.getId());
                editTextDataTxtParams.setMargins(40, 0, 0, 40);

                TextView txtData = new TextView(context);
                txtData.setTextSize(25);
                txtData.setText("Data");
                txtData.setId(View.generateViewId());
                txtData.setLayoutParams(editTextDataTxtParams);

                RelativeLayout.LayoutParams editTextDataSpinnerParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextDataSpinnerParams.addRule(RelativeLayout.BELOW, layoutConsegna.getId());
                editTextDataSpinnerParams.addRule(RelativeLayout.END_OF, txtData.getId());
                editTextDataSpinnerParams.addRule(RelativeLayout.ALIGN_BASELINE, txtData.getId());
                editTextDataSpinnerParams.setMargins(40, 0, 0, 40);

                spinnerDate = new Spinner(context);
                spinnerDate.setLayoutParams(editTextDataSpinnerParams);

                String[] dateSettimana;
                Calendar c = Calendar.getInstance(Locale.ITALY);
                Calendar c_copia = Calendar.getInstance(Locale.ITALY);

                dateSettimana = new String[8 - (c.get(Calendar.DAY_OF_WEEK) - 1)];
                int count = 0;
                String giorno = "";
                String mese = "";
                String anno = "";
                for (int i = c.get(Calendar.DAY_OF_WEEK) - 1; i < 7; i++) {
                    giorno = (c_copia.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + (c_copia.get(Calendar.DAY_OF_MONTH)) : "" + (c_copia.get(Calendar.DAY_OF_MONTH));
                    mese = (c_copia.get(Calendar.MONTH) + 1 < 10) ? "0" + (c_copia.get(Calendar.MONTH) + 1) : "" + (c_copia.get(Calendar.MONTH) + 1);
                    anno = "" + (c_copia.get(Calendar.YEAR));
                    dateSettimana[count] = giorno + "-" + mese + "-" + anno;
                    c_copia.add(Calendar.DATE, 1);
                    count++;
                }
                giorno = (c_copia.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + (c_copia.get(Calendar.DAY_OF_MONTH)) : "" + (c_copia.get(Calendar.DAY_OF_MONTH));
                mese = (c_copia.get(Calendar.MONTH) + 1 < 10) ? "0" + (c_copia.get(Calendar.MONTH) + 1) : "" + (c_copia.get(Calendar.MONTH) + 1);
                anno = "" + (c_copia.get(Calendar.YEAR));

                dateSettimana[dateSettimana.length - 1] = giorno + "-" + mese + "-" + anno;

                arrayDate = new ArrayAdapter<String>(context, R.layout.date_spinner_new_orders, dateSettimana); //selected item will look like a spinner set from XML
                arrayDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDate.setAdapter(arrayDate);

                RelativeLayout.LayoutParams editTextOraTxtParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_100dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextOraTxtParams.addRule(RelativeLayout.BELOW, txtData.getId());
                editTextOraTxtParams.setMargins(40, 0, 0, 40);

                TextView txtOra = new TextView(context);
                txtOra.setTextSize(25);
                txtOra.setText("Ora");
                txtOra.setId(View.generateViewId());
                txtOra.setLayoutParams(editTextOraTxtParams);

                RelativeLayout.LayoutParams editTextOraSpinnerParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                editTextOraSpinnerParams.addRule(RelativeLayout.BELOW, txtData.getId());
                editTextOraSpinnerParams.addRule(RelativeLayout.END_OF, txtOra.getId());
                editTextOraSpinnerParams.addRule(RelativeLayout.ALIGN_BASELINE, txtOra.getId());
                editTextOraSpinnerParams.setMargins(40, 0, 0, 40);

                listaOre = new ArrayList<String>();

                listaOre.add("18:00");
                listaOre.add("18:30");
                listaOre.add("19:00");
                listaOre.add("19:30");
                listaOre.add("20:00");
                listaOre.add("20:30");
                listaOre.add("21:00");
                listaOre.add("21:30");

                adapterOre = new ArrayAdapter<String>(context, R.layout.date_spinner_new_orders, listaOre); //selected item will look like a spinner set from XML
                spinnerOre = new Spinner(context);
                spinnerOre.setAdapter(adapterOre);
                spinnerOre.setLayoutParams(editTextOraSpinnerParams);


                inserimentoLayout.addView(nomeInput);
                inserimentoLayout.addView(cognomeInput);
                inserimentoLayout.addView(telefonoInput);
                inserimentoLayout.addView(consegna);
                layoutConsegna.addView(viaInput);
                layoutConsegna.addView(txtCitta);
                layoutConsegna.addView(spinnerCitta);
                inserimentoLayout.addView(layoutConsegna);
                inserimentoLayout.addView(txtData);
                inserimentoLayout.addView(spinnerDate);
                inserimentoLayout.addView(txtOra);
                inserimentoLayout.addView(spinnerOre);
                scrollView.addView(inserimentoLayout);

                cognome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        cognome.setText(clienti.get(0).valueAt(pos).get("cognome"));
                        nome.setText(clienti.get(0).valueAt(pos).get("nome"));
                        via.setText(clienti.get(0).valueAt(pos).get("via"));
                        telefono.setText(clienti.get(0).valueAt(pos).get("telefono"));
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(scrollView)
                        .setTitle("Inserisci Nuovo Ordine - Totale: " + totale.getText().toString())
                        .setPositiveButton("Inserisci", null)
                        .setNegativeButton("Annulla", null)
                        .create();

                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean toClose = true;
                        final String strNome = nome.getText().toString();
                        final String strCognome = cognome.getText().toString();
                        final String strTelefono = telefono.getText().toString();
                        final String strData = Funzioni.formattaData(spinnerDate.getSelectedItem().toString());
                        final String strOra = spinnerOre.getSelectedItem().toString();

                        if (strCognome.isEmpty()) {
                            toClose = false;
                            cognome.setError("Inserisci cognome");
                        }
                        if (strTelefono.isEmpty() || strTelefono.length() != 10) {
                            toClose = false;
                            telefono.setError("Inserisci telefono");
                        }
                        if (consegna.isChecked()) {
                            strVia = via.getText().toString();
                            strCivico = civico.getText().toString();
                            strCitta = spinnerCitta.getSelectedItem().toString();
                            if (strVia.isEmpty()) {
                                toClose = false;
                                via.setError("Inserisci via");
                            }
                            if (strCivico.isEmpty()) {
                                toClose = false;
                                civico.setError("Inserisci civico");
                            }
                        }

                        if (toClose) {
                            new HttpManager.AsyncManager(new AsyncResponse() {
                                @Override
                                public void processFinish(Object output) {
                                    completaOrdine(strData, strOra, strNome, strCognome, strTelefono, output);
                                }
                            }, null, "INSERISCI_CLIENTE", new String[]{strCognome, strNome, strTelefono, strVia, strCitta}).execute();


                            dialog.dismiss();

                        }
                    }
                });
            }

        });

        return view;
    }

    private void recuperaOrdine(String id_ordine) {
        // riempiConto
    }

    private void completaOrdine(String strData, String strOra, String strNome, String strCognome, String strTelefono, Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, String> riga = itr.next();
        final String idClienteCreato = riga.get("generated_id");

        if (consegna.isChecked())
            HttpManager.execSimple("AGGIORNA_ORDINE_DOMICILIO", null, (totale.length() > 5) ? totale.getText().subSequence(0, 4).toString() : totale.getText().subSequence(0, 3).toString(), strCitta, strOra, strData, strVia, strTelefono, idOrdine);
        else
            HttpManager.execSimple("AGGIORNA_ORDINE_ASPORTO", null, (totale.length() > 5) ? totale.getText().subSequence(0, 4).toString() : totale.getText().subSequence(0, 3).toString(), strOra, strData, strTelefono, idOrdine);
                            /*TODO
                                   if (!checkUtentePresente(idcliente))
                            DBmanager.updateQuery(EnumQuery.INSERISCI_CLIENTE.getValore(), false, idcliente, cognomeString, nomeString, telefonoString.substring(0, 4), telefonoString.substring(4, 10));
                            */
        HttpManager.execSimple("ASSOCIA_ORDINE_CLIENTE", null, idOrdine, idClienteCreato, strNome, strCognome, "Pizzeria");

        Toast.makeText(context, "Ordine completato con successo!", Toast.LENGTH_SHORT).show();

        RiepilogoOrdini fragment = new RiepilogoOrdini();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .commit();
    }

    private void creaOrdine(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, String> riga = itr.next();
        final String ID_ORDINE = riga.get("generated_id");
        idOrdine = ID_ORDINE;

        impostaBottoni();
    }

    private void impostaBottoni() {
        final String ordine = idOrdine;
        //FARE L'ITERATOR SU HASHMAP ALTRIMENTI RITORNA SEMPRE L'ULTIMO ELEMENTO DELL'HASMAP
        for (int i = 0; i < listaProdotti.get(i).size(); i++) {
            int countPizze = 0;
            int countBibite = 0;
            int countGastronomia = 0;

            final String nomeProdotto = listaProdotti.get(i).get("nome");
            final String tipo = listaProdotti.get(i).get("tipo");
            final String prezzo = listaProdotti.get(i).get("prezzo");
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
                            creaConto(tipo, output);
                        }
                    }, null, "AGGIUNGI_PRODOTTO_TO_ORDINE", new String[]{nomeProdotto, ordine}).execute();
                }
            });
        }
    }


    private void creaConto(final String tipoProdotto, Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        final String idColonna = lista.get(0).get("generated_id");

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(final Object output) {
                if (tipoProdotto.equals("Pizza") && isMetro.isChecked()) {
                    if ((sparseMetri.size() == 0 || nuovoMetro || isUltimoMetroFull()) && idMetro_onMod == -1) {
                        nuovoMetro = false;
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output_bis) {
                                List<HashMap<String, String>> listaBis = (List<HashMap<String, String>>) output_bis;
                                final int idMetro = Integer.parseInt(listaBis.get(0).get("generated_id"));
                                riempiConto(output, idMetro);
                            }
                        }, null, "INSERISCI_PIZZA_IN_METRO", new String[]{idColonna}).execute();
                    } else {
                        int idMetro = (idMetro_onMod == -1) ? sparseMetri.keyAt(sparseMetri.size() - 1) : idMetro_onMod;
                        if (layoutBarra_onMod != null) {
                            layoutBarra_onMod.setBackgroundResource(0);
                            idMetro_onMod = -1;
                            layoutBarra_onMod = null;
                        }
                        HttpManager.execSimple("INSERISCI_PIZZA_IN_METRO_CON_ID", null, Integer.toString(idMetro), idColonna);
                        riempiConto(output, idMetro);
                    }
                } else {
                    riempiConto(output, -1);
                }
            }
        }, null, "GET_PRODOTTO_IN_ORDINE", new String[]{idColonna}).execute();
    }

    private void aggiornaTotaliMetri() {
        if (totMetriAggiornato > -1) aggiornaTotale(totMetriAggiornato, false);

        float totToAdd = 0;
        for (int i = 0; i < sparseMetri.size(); i++) {
            List<Integer> listaPizze = sparseMetri.valueAt(i).entrySet().iterator().next().getValue();
            float totaleMetro = 0;
            for (int idPizza : listaPizze)
                totaleMetro += Float.parseFloat((sparsePrezziProdotti.get(idPizza).getText().toString()).substring(0, sparsePrezziProdotti.get(idPizza).getText().toString().length() - 2));
            switch (listaPizze.size()) {
                case 1:
                    totaleMetro *= 2.5;
                    break;
                case 2:
                    totaleMetro *= 1.25;
                    break;
                case 3:
                    totaleMetro *= 0.83;
                    break;
            }
            totToAdd += totaleMetro;
        }
        totMetriAggiornato = totToAdd;
        aggiornaTotale(totToAdd, true);
    }

    private boolean isUltimoMetroFull() {
        return (sparseMetri.valueAt(sparseMetri.size() - 1)) != null && sparseMetri.valueAt(sparseMetri.size() - 1).entrySet().iterator().next().getValue().size() == 3;
    }

    private boolean isColonnaMetro(int idPizza) {
        boolean isMetro = false;
        for (int i = 0; i < sparseMetri.size() && !isMetro; i++) {
            isMetro = sparseMetri.valueAt(i).entrySet().iterator().next().getValue().contains(idPizza);
        }
        return isMetro;
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

    private void rimuoviElemento(String tipo, TableRow toRemove, int idMetro, int idColonna) {
        if (idMetro == -1) {
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
        } else {
            sparseMetri.get(idMetro).entrySet().iterator().next().getValue().remove((Integer) idColonna);
            sparseMetri.get(idMetro).entrySet().iterator().next().getKey().removeView(toRemove);
            if (sparseMetri.get(idMetro).entrySet().iterator().next().getValue().isEmpty()) {
                layoutContoMetri.removeView(sparseMetri.get(idMetro).entrySet().iterator().next().getKey());
                sparseMetri.remove(idMetro);
            }
        }
        sparsePrezziProdotti.remove(idColonna);
    }

    private void aggiornaTotale(float prezzo, boolean isSomma) {
        float current = Float.parseFloat((totale.getText().toString()).substring(0, totale.getText().toString().length() - 2));
        current = (isSomma) ? current + prezzo : current - prezzo;
        totale.setText(new DecimalFormat("#0.00 €").format(Funzioni.arrotonda(current)));
    }

    private void riempiConto(final Object param, final int idMetro) {
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

                if (idMetro == -1) aggiornaTotale(prezzoFloat, true);

                final TableRow rowPizza = new TableRow(context);
                rowPizza.setId(View.generateViewId());

                TextView txtPizza;

                if (nomeProdotto.equals("PROSCIUTTO E FUNGHI"))
                    txtPizza = makeTableRowWithText("PROSC. E FUNGHI");
                else
                    txtPizza = makeTableRowWithText(nomeProdotto);

                TextView txtPrezzo;
                txtPrezzo = makeTableRowWithText(prezzoString);
                txtPrezzo.setGravity(Gravity.CENTER);
                rowPizza.addView(txtPizza);
                rowPizza.addView(txtPrezzo);

                Button btnModifica = new Button(context);
                btnModifica.setText("Modifica");
                if (!(tipo.equals("Bibita") || (tipo.equals("Gastronomia") && !(nomeProdotto.equals("PANUOZZO") || nomeProdotto.equals("PANUOZZO XXL")))))
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
                        HttpManager.execSimple("TOGLI_PRODOTTO_FROM_ORDINE", null, valColonna);
                        if (idMetro == -1)
                            aggiornaTotale(Float.parseFloat((sparsePrezziProdotti.get(Integer.parseInt(valColonna)).getText().toString()).substring(0, sparsePrezziProdotti.get(Integer.parseInt(valColonna)).getText().toString().length() - 2)), false);

                        rimuoviElemento(tipo, rowPizza, idMetro, Integer.parseInt(valColonna));
                        if (tableOrdiniPizza.getChildCount() == 0)
                            layoutContoPizze.setBackgroundResource(0);
                        if (tableOrdiniGastronomia.getChildCount() == 0)
                            layoutContoGastronomia.setBackgroundResource(0);

                        Toast.makeText(context, nomeProdotto + " eliminato!", Toast.LENGTH_SHORT).show();
                    }
                });

                if (idMetro != -1 && sparseMetri.get(idMetro) == null) {
                    RelativeLayout.LayoutParams layoutTabellaMetri = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    HashMap<TableLayout, List<Integer>> hashTemp = new HashMap<TableLayout, List<Integer>>(1);
                    List<Integer> tempList = new ArrayList<Integer>();
                    tempList.add(Integer.parseInt(valColonna));

                    final TableLayout tableOrdiniMetri = new TableLayout(context);
                    tableOrdiniMetri.setLayoutParams(layoutTabellaMetri);
                    tableOrdiniMetri.setGravity(Gravity.CENTER_HORIZONTAL);
                    tableOrdiniMetri.setId(View.generateViewId());
                    if (layoutContoMetri.getChildCount() > 0)
                        layoutTabellaMetri.addRule(RelativeLayout.BELOW, layoutContoMetri.getChildAt(layoutContoMetri.getChildCount() - 1).getId());

                    final RelativeLayout layoutBarra = new RelativeLayout(context);
                    RelativeLayout.LayoutParams paramLayoutBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramLayoutBarra.setMargins(30, 0, 30, 0);
                    layoutBarra.setLayoutParams(paramLayoutBarra);

                    View barraSx = new View(context);
                    barraSx.setBackgroundColor(getResources().getColor(R.color.giallo));

                    RelativeLayout.LayoutParams paramBarraSx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                    paramBarraSx.setMargins(0, 0, 30, 0);
                    paramBarraSx.addRule(RelativeLayout.CENTER_VERTICAL);

                    TextView testoInMezzo = new TextView(context);
                    testoInMezzo.setId(View.generateViewId());
                    testoInMezzo.setTextAppearance(context, R.style.testoGrande);
                    testoInMezzo.setTextColor(getResources().getColor(R.color.giallo));
                    testoInMezzo.setText("Mezzo-Metro");

                    RelativeLayout.LayoutParams testoInMezzoParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    testoInMezzoParam.setMargins(0, 0, 30, 0);
                    testoInMezzoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    testoInMezzoParam.addRule(RelativeLayout.CENTER_VERTICAL);
                    testoInMezzo.setLayoutParams(testoInMezzoParam);

                    paramBarraSx.addRule(RelativeLayout.START_OF, testoInMezzo.getId());
                    barraSx.setLayoutParams(paramBarraSx);

                    RelativeLayout.LayoutParams paramBtn = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramBtn.setMargins(18, 0, 0, 0);
                    paramBtn.addRule(RelativeLayout.CENTER_VERTICAL);
                    paramBtn.addRule(RelativeLayout.END_OF, testoInMezzo.getId());

                    ImageButton btnModificaMezzoMetro = new ImageButton(context);
                    btnModificaMezzoMetro.setImageResource(R.drawable.modifica);
                    btnModificaMezzoMetro.setId(View.generateViewId());
                    btnModificaMezzoMetro.setLayoutParams(paramBtn);
                    btnModificaMezzoMetro.setBackgroundResource(0);

                    btnModificaMezzoMetro.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (sparseMetri.get(idMetro).entrySet().iterator().next().getValue().size() < 3) {
                                if (idMetro_onMod == -1) {
                                    idMetro_onMod = idMetro;
                                    layoutBarra_onMod = layoutBarra;
                                    layoutBarra.setBackgroundResource(R.color.menu_primary);
                                } else {
                                    idMetro_onMod = -1;
                                    layoutBarra_onMod = null;
                                    layoutBarra.setBackgroundResource(0);
                                }
                            }
                        }
                    });

                    View barradx = new View(context);
                    barradx.setBackgroundColor(getResources().getColor(R.color.giallo));

                    RelativeLayout.LayoutParams parambarradx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                    parambarradx.setMargins(30, 0, 0, 0);
                    parambarradx.addRule(RelativeLayout.CENTER_VERTICAL);
                    parambarradx.addRule(RelativeLayout.END_OF, btnModificaMezzoMetro.getId());
                    barradx.setLayoutParams(parambarradx);

                    layoutBarra.addView(barraSx);
                    layoutBarra.addView(testoInMezzo);
                    layoutBarra.addView(btnModificaMezzoMetro);
                    layoutBarra.addView(barradx);
                    tableOrdiniMetri.addView(layoutBarra);

                    tableOrdiniMetri.addView(rowPizza);
                    hashTemp.put(tableOrdiniMetri, tempList);
                    sparseMetri.put(idMetro, hashTemp);
                    layoutContoMetri.addView(tableOrdiniMetri);
                } else if (sparseMetri.get(idMetro) != null) {
                    sparseMetri.get(idMetro).entrySet().iterator().next().getKey().addView(rowPizza);
                    sparseMetri.get(idMetro).entrySet().iterator().next().getValue().add(Integer.parseInt(valColonna));
                }

                switch (tipo) {
                    case "Pizza":
                        if (idMetro == -1) tableOrdiniPizza.addView(rowPizza);
                        break;
                    case "Bibita":
                        tableOrdiniBibite.addView(rowPizza);
                        break;
                    case "Gastronomia":
                        tableOrdiniGastronomia.addView(rowPizza);
                        break;
                }
                sparsePrezziProdotti.put(Integer.parseInt(valColonna), txtPrezzo);
            }
            if (tableOrdiniPizza.getChildCount() > 0)
                layoutContoPizze.setBackgroundResource(R.drawable.table_bottom_style);
            if (tableOrdiniGastronomia.getChildCount() > 0)
                layoutContoGastronomia.setBackgroundResource(R.drawable.table_bottom_style);
            if (layoutContoMetri.getChildCount() > 0)
                layoutContoMetri.setBackgroundResource(R.drawable.table_bottom_style);
        }
    }

    private void inizializzaAggiunte(Object param, final String valColonna, RelativeLayout baseLayout, final List<String> ingrBaseRimossi) {
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
                        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) output;
                        Float prezzoFinale = Float.parseFloat(lista.get(0).get("prezzo_finale"));
                        if (!isColonnaMetro(valColonna)) {
                            aggiornaTotale(Float.parseFloat((sparsePrezziProdotti.get(valColonna).getText().toString()).substring(0, sparsePrezziProdotti.get(valColonna).getText().toString().length() - 2)), false);
                            aggiornaTotale(prezzoFinale, true);
                        }
                        sparsePrezziProdotti.get(valColonna).setText(new DecimalFormat("#0.00 €").format(prezzoFinale));
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


    /*public void checkOrario(Object param) {

        for (int i = 0; i < listaOre.size(); i++) {
            List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
            Iterator<HashMap<String, String>> itrAgg = lista.iterator();
            HashMap<String, String> riga = itrAgg.next();
            nPizze[i] = Integer.parseInt(riga.get("npizze"));
        }

        adapterOre = new ArrayAdapter<String>(context, R.layout.date_spinner_new_orders, listaOre) {
            // Disable click item < month current
            @Override
            public boolean isEnabled(int position) {
                return !(nPizze[position] + tableOrdiniPizza.getChildCount() >= 15 && contOrdini[position] > 1);
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (nPizze[position] + tableOrdiniPizza.getChildCount() >= 15 && contOrdini[position] > 1) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        adapterOre.setDropDownViewResource(R.layout.date_spinner_new_orders);
        spinnerOre.setAdapter(adapterOre);
    }*/

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

    private ArrayList<String> getListaCognomi() {
        ArrayList<String> listaCognomi = new ArrayList<>();
        for (int i = 0; i < clienti.get(0).size(); i++) {
            listaCognomi.add(clienti.get(0).valueAt(i).get("cognome") + ", " + clienti.get(0).valueAt(i).get("via"));
        }
        return listaCognomi;
    }

}