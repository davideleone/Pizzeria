package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import terranovaproductions.newcomicreader.FloatingActionMenu;

public class RiepilogoOrdini extends Fragment {

    private TableLayout tabellaOrdini;
    private TableLayout tabellaAssegnati;

    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;

    private Spinner spinnerDate;
    private ArrayAdapter<String> arrayDate;
    private String dataRicerca;

    private Spinner spinnerFattorini;
    private ArrayAdapter<String> arrayFattorini;

    private List<Integer> idFattorini = new ArrayList<Integer>();
    private List<String> nomiFattorini = new ArrayList<String>();

    private Map<TableRow, TableLayout> mappaRows = new HashMap<TableRow, TableLayout>();

    private TextView recyclableTextView;
    private ImageButton recyclableImageButton;

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

        View view = inflater.inflate(R.layout.activity_riepilogo_ordini, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);

        tabellaOrdini = (TableLayout) view.findViewById(R.id.tabella_ordini);
        tabellaAssegnati = (TableLayout) view.findViewById(R.id.tabella_ordini_assegnati);

        spinnerDate = (Spinner) view.findViewById(R.id.date_settimane);

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

        arrayDate = new ArrayAdapter<String>(context, R.layout.date_spinner, dateSettimana); //selected item will look like a spinner set from XML
        arrayDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDate.setAdapter(arrayDate);
        dataRicerca = Funzioni.formattaData(spinnerDate.getSelectedItem().toString()); // FIRST RUN
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!dataRicerca.equals(Funzioni.formattaData(spinnerDate.getSelectedItem().toString()))) {
                    dataRicerca = Funzioni.formattaData(spinnerDate.getSelectedItem().toString());
                    aggiornaTabella();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });

        spinnerFattorini = new Spinner(context);

        caricaOrdini();
        caricaFattorini();

        FloatingActionMenu menu = (FloatingActionMenu) view.findViewById(R.id.fab_menu_circle);
        menu.setMultipleOfFB(3.2f);
        menu.setIsCircle(true);

        menu.setOnMenuItemClickListener(new FloatingActionMenu.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionMenu fam, int index, FloatingActionButton item) {
                switch (index) {
                    case 0:
                        // DELETE TAB
                        break;
                    case 1:
                        // INFO TAB
                        break;
                    case 2:
                        NuovoOrdine fragment = new NuovoOrdine();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.flContent, fragment)
                                .commit();
                        break;
                    default:
                }
            }
        });

        return view;
    }

    private void aggiornaTabella() {
        // DELETE OLD
        for (Map.Entry<TableRow, TableLayout> entry  : mappaRows.entrySet()) (entry.getValue()).removeView(entry.getKey());
        mappaRows.clear();

        // UPDATE
        caricaOrdini();
    }

    private void caricaFattorini(){
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                aggiornaFattorini( output );
            };
        }, context, "GET_ELENCO_FATTORINI", new String[]{}).execute();
    }

    private void aggiornaFattorini(Object param) {
        // DELETE OLD
        nomiFattorini.clear();
        idFattorini.clear();

        // UPDATE
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if(!lista.isEmpty()){
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final Integer idFatt = Integer.parseInt(riga.get("idfattorino"));
                final String nomeFatt = riga.get("nomecompleto");
                nomiFattorini.add(nomeFatt);
                idFattorini.add(idFatt);
            }
        }

        // UPDATE SPINNER
        String[] arrayFatt = new String[nomiFattorini.size()];
        arrayFatt = nomiFattorini.toArray(arrayFatt);

        arrayFattorini = new ArrayAdapter<String>(context, R.layout.fattorini_spinner, arrayFatt);
        arrayFattorini.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFattorini.setAdapter(arrayFattorini);

    }

    private String getFattorinoSelezionato() {
        return idFattorini.get(spinnerFattorini.getSelectedItemPosition()).toString();
    }

    private void getPizzeOrdine(String idOrdine){
        List<HashMap<String, Object>> risultatoQuery = DBmanager.selectQuery(EnumQuery.GET_PIZZA_IN_ORDINE.getValore(), idOrdine);
        Iterator<HashMap<String, Object>> itr = risultatoQuery.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, Object> riga = itr.next();
                final String nomePizza = riga.get("nomeprodotto").toString();
                final String prezzoPizza = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("prezzoprodotto").toString())) + " \u20ac";
                final String idExtra = riga.get("id_colonna").toString();
                List<String> ingredienti = getIngredientiPizza(idExtra);
                System.out.println("PIZZA = " + nomePizza);
                System.out.println("PREZZO = " + prezzoPizza);
                System.out.println("INGREDIENTI = ");
                for(String item : ingredienti){
                    System.out.println(item);
                }
                System.out.println("---------------------------");
            }
        }
    }

    private List<String> getIngredientiPizza(String idExtra){
        List<String> ingredienti = new ArrayList<String>();
        List<HashMap<String, Object>> risultatoQuery = DBmanager.selectQuery(EnumQuery.GET_LISTA_INGREDIENTI_ED_EXTRA.getValore(), idExtra, idExtra, idExtra);
        Iterator<HashMap<String, Object>> itr = risultatoQuery.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, Object> riga = itr.next();
                ingredienti.add(riga.get("nomeingrediente").toString());
            }
        }
        return ingredienti;
    }

    private void caricaOrdini(){
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                processaOrdini( output );
            };
        }, context, "MONITORA_ORDINE", new String[]{dataRicerca}).execute();
    }


    private void processaOrdini(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if(!lista.isEmpty()){
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String cognome = riga.get("cognome");
                final String idOrdine = riga.get("id");
                final int stato = Integer.parseInt(riga.get("tipo"));
                final String consegna = riga.get("consegna");
                final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("totale"))) + " \u20ac";
                final String social = riga.get("social");
                final String telefono = riga.get("prefisso") + riga.get("telefono");

                TableRow row = new TableRow(context);
                row.setBackgroundResource(R.drawable.table_bottom_style);

                TextView dataOrdine = makeTableRowWithText(riga.get("dataconsegna"), R.dimen.dim_150dp);
                TextView oraOrdine = makeTableRowWithText(riga.get("oraconsegna").substring(0, 5), R.dimen.dim_80dp);
                TextView nomeCliente = makeTableRowWithText((riga.get("nome")), R.dimen.dim_150dp);
                TextView cognomeCliente = makeTableRowWithText(cognome, R.dimen.dim_150dp);
                TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_100dp);
                TextView viaOrdine = makeTableRowWithText(riga.get("via"), R.dimen.dim_200dp);
                TextView cittaOrdine = makeTableRowWithText(riga.get("citta"), R.dimen.dim_150dp);

                ImageButton btnMostra = makeTableRowWithImageButton(R.drawable.mostra);
                ImageButton btnAccetta = makeTableRowWithImageButton(R.drawable.accetta);
                ImageButton btnConsegna = makeTableRowWithImageButton(R.drawable.consegna);
                ImageButton btnElimina = makeTableRowWithImageButton(R.drawable.elimina);

                btnMostra.setOnClickListener(new View.OnClickListener() {
                    @Override
                public void onClick(View v) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                fixDettagliPizze( output , telefono, cognome);
                            };
                        }, context, "GET_PIZZE_CON_EXTRA", new String[]{idOrdine}).execute();
                    }
                });

                btnConsegna.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    RelativeLayout layoutDialog = new RelativeLayout(context);
                    RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    spinnerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    spinnerParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    spinnerFattorini.setLayoutParams(spinnerParams);
                    if (spinnerFattorini.getParent() != null)
                        ((ViewGroup) spinnerFattorini.getParent()).removeView(spinnerFattorini);
                    layoutDialog.addView(spinnerFattorini);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Scelta Fattorino");
                    builder.setView(layoutDialog);
                    builder.setPositiveButton("Consegna", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (stato != 3) {
                                new HttpManager.AsyncManager(new AsyncResponse() {
                                    @Override
                                    public void processFinish(Object output) {
                                        new HttpManager.AsyncManager(new AsyncResponse() {
                                            @Override
                                            public void processFinish(Object output) {
                                                Toast.makeText(context, "Consegna affidata al fattorino", Toast.LENGTH_SHORT).show();
                                                aggiornaTabella();
                                            };
                                        }, context, "MANDA_IN_CONSEGNA", new String[]{idOrdine}).execute();
                                    };
                                }, context, "ASSEGNA_FATTORINO", new String[]{getFattorinoSelezionato(), idOrdine}).execute();
                            } else {
                                new HttpManager.AsyncManager(new AsyncResponse() {
                                    @Override
                                    public void processFinish(Object output) {
                                        Toast.makeText(context, "Fattorino cambiato correttamente", Toast.LENGTH_SHORT).show();
                                        aggiornaTabella();
                                    };
                                }, context, "CAMBIA_FATTORINO", new String[]{getFattorinoSelezionato(), idOrdine}).execute();
                            }
                        }
                    });
                    builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                    }
                });

                btnAccetta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpManager.execSimple("ACCETTA_ORDINE", context, idOrdine);
                    }
                });


                btnElimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Elimina ordine")
                            .setMessage("Sei sicuro di voler eliminare l'ordine di " + cognome + "?")
                            .setPositiveButton("Si, elimina", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new HttpManager.AsyncManager(new AsyncResponse() {
                                        @Override
                                        public void processFinish(Object output) {
                                            aggiornaTabella();
                                            Toast.makeText(context, "Ordine Eliminato!", Toast.LENGTH_SHORT).show();
                                        };
                                    }, context, "ELIMINA_ORDINE", new String[]{idOrdine}).execute();
                                }
                            })
                            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();
                    }
                });

                row.addView(dataOrdine);
                row.addView(oraOrdine);
                row.addView(nomeCliente);
                row.addView(cognomeCliente);
                row.addView(totaleOrdine);
                row.addView(viaOrdine);
                row.addView(cittaOrdine);
                row.addView(btnMostra);
                row.addView(btnAccetta);
                row.addView(btnConsegna);
                row.addView(btnElimina);

                mappaRows.put(row, ((stato != 3) ? tabellaOrdini : tabellaAssegnati));
                ((stato != 3) ? tabellaOrdini : tabellaAssegnati).addView(row);
            }
        } else {
            Toast.makeText(context, "Nessun nuovo ordine", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostraDettaglio(SparseArray<List<HashMap<String, String>>> hashPizze, String telefono, String cognome, boolean isTolti){
        final RelativeLayout layoutContenitore = new RelativeLayout(context);

        RelativeLayout layoutDettaglio = new RelativeLayout(context);
        layoutDettaglio.setId(View.generateViewId());

        RelativeLayout.LayoutParams paramsStatoText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsStatoText.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsStatoText.setMargins(0, 15, 0, 0);
        TextView textStato = new TextView(context);
        textStato.setId(View.generateViewId());
        textStato.setText("Stato: ");
        textStato.setTextAppearance(context, R.style.testoGrande);
        textStato.setLayoutParams(paramsStatoText);

        RelativeLayout.LayoutParams paramsStato = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsStato.addRule(RelativeLayout.END_OF, textStato.getId());
        paramsStato.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ImageView imgStato = new ImageView(context);
        imgStato.setId(View.generateViewId());
        imgStato.setLayoutParams(paramsStato);
        imgStato.setImageResource(R.drawable.giallo);

        RelativeLayout.LayoutParams paramsConsegna = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsConsegna.addRule(RelativeLayout.BELOW, textStato.getId());
        paramsConsegna.addRule(RelativeLayout.CENTER_HORIZONTAL);
        TextView textConsegna = new TextView(context);
        textConsegna.setId(View.generateViewId());
        textConsegna.setText("Assegnato a: ");
        textConsegna.setTextAppearance(context, R.style.testoGrande);
        textConsegna.setLayoutParams(paramsConsegna);

        RelativeLayout.LayoutParams paramsTelefono = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTelefono.addRule(RelativeLayout.BELOW, textConsegna.getId());
        paramsTelefono.addRule(RelativeLayout.CENTER_HORIZONTAL);
        TextView textTelefono = new TextView(context);
        textTelefono.setId(View.generateViewId());
        textTelefono.setText("Telefono: " + telefono);
        textTelefono.setTextAppearance(context, R.style.testoGrande);
        textTelefono.setLayoutParams(paramsTelefono);

        RelativeLayout.LayoutParams paramsLinea = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramsLinea.addRule(RelativeLayout.BELOW, textTelefono.getId());
        paramsLinea.setMargins(30, 10, 30, 0);
        View separator = new View(context);
        separator.setId(View.generateViewId());
        separator.setBackgroundColor(getResources().getColor(R.color.grigio));
        separator.setLayoutParams(paramsLinea);

        RelativeLayout.LayoutParams paramsLayoutPizze = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutPizze.addRule(RelativeLayout.BELOW, layoutDettaglio.getId());
        RelativeLayout layoutPizze = new RelativeLayout(context);
        layoutPizze.setGravity(Gravity.CENTER);
        layoutPizze.setLayoutParams(paramsLayoutPizze);

        RelativeLayout.LayoutParams paramsCaricamento = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsCaricamento.addRule(RelativeLayout.CENTER_HORIZONTAL, separator.getId());

        layoutPizze = dettaglioPizze(hashPizze, isTolti, layoutPizze);

        layoutDettaglio.addView(textStato);
        layoutDettaglio.addView(imgStato);
        layoutDettaglio.addView(textConsegna);
        layoutDettaglio.addView(textTelefono);
        layoutDettaglio.addView(separator);
        layoutContenitore.addView(layoutDettaglio);
        layoutContenitore.addView(layoutPizze);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio ordine di " + cognome);
        builder.setView(layoutContenitore);
        builder.create().show();

        //getPizzeOrdine(idOrdine);
    }

    private void fixDettagliPizze(Object param, String telefono, String cognome){
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        SparseArray<List<HashMap<String, String>>> hashColonne = new SparseArray<List<HashMap<String, String>>>();
        boolean isTolti = false;
        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final int idcolonna = Integer.parseInt(riga.get("id_colonna"));
            List<HashMap<String, String>> listaTemp = (hashColonne.get(idcolonna) != null) ? hashColonne.get(idcolonna) : new ArrayList<HashMap<String, String>>();
            HashMap<String, String> row = new HashMap<String, String>(4);
            row.put("nomeprodotto", riga.get("nomeprodotto"));
            row.put("prezzoprodotto", riga.get("prezzoprodotto"));
            row.put("nomeextra", riga.get("nomeextra"));
            row.put("tipo", riga.get("tipo"));
            if(Integer.parseInt(riga.get("tipo")) == 2) isTolti = true;
            listaTemp.add(row);
            hashColonne.put(idcolonna, listaTemp);
        }
        mostraDettaglio(hashColonne, telefono, cognome, isTolti);
    }

    private RelativeLayout dettaglioPizze(SparseArray<List<HashMap<String, String>>> hashPizze, boolean isTolti, RelativeLayout layoutPizze) {
        for (int i = 0; i < hashPizze.size(); i++) {
            boolean baseLayoutCreata = false;
            List<HashMap<String, String>> listaPizza = hashPizze.valueAt(i);
            Iterator<HashMap<String, String>> itrPizza = listaPizza.iterator();

            TextView nomeingredientiAggiunti = new TextView(context);
            TextView nomeingredientiTolti = new TextView(context);
            while (itrPizza.hasNext()) {
                HashMap<String, String> valorePizza = itrPizza.next();
                final String nome = valorePizza.get("nomeprodotto");
                final float prezzo = Float.parseFloat(valorePizza.get("prezzoprodotto"));
                final int tipoExtra = Integer.parseInt(valorePizza.get("tipo"));
                final String nomeExtra = valorePizza.get("nomeextra");

                if (!baseLayoutCreata) {
                    RelativeLayout.LayoutParams paramsNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if (i > 0)
                        paramsNome.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
                    paramsNome.setMargins(30, 30, 0, 0);

                    TextView nomePizza = new TextView(context);
                    nomePizza.setText("- " + nome);
                    nomePizza.setId(View.generateViewId());
                    nomePizza.setTextAppearance(context, R.style.testoGrande);
                    nomePizza.setLayoutParams(paramsNome);
                    layoutPizze.addView(nomePizza);

                    RelativeLayout.LayoutParams paramsPrezzo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsPrezzo.addRule(RelativeLayout.END_OF, nomePizza.getId());
                    paramsPrezzo.setMargins(20, 30, 0, 0);

                    TextView prezzoPizza = new TextView(context);
                    prezzoPizza.setText(new DecimalFormat("#0.00 €").format(prezzo));
                    prezzoPizza.setId(View.generateViewId());
                    prezzoPizza.setTextAppearance(context, R.style.testoGrande);
                    prezzoPizza.setLayoutParams(paramsPrezzo);

                    layoutPizze.addView(prezzoPizza);

                    RelativeLayout.LayoutParams paramTolti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramTolti.addRule(RelativeLayout.BELOW, nomePizza.getId());
                    paramTolti.setMargins(80, 0, 0, 0);

                    nomeingredientiTolti.setText("NO ");
                    nomeingredientiTolti.setId(View.generateViewId());
                    nomeingredientiTolti.setTextAppearance(context, R.style.testoPiccolo);
                    nomeingredientiTolti.setMaxEms(20);
                    nomeingredientiTolti.setLayoutParams(paramTolti);

                    RelativeLayout.LayoutParams paramAggiunti = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramAggiunti.addRule(RelativeLayout.BELOW, nomeingredientiTolti.getId());
                    if (isTolti)
                        paramAggiunti.addRule(RelativeLayout.ALIGN_START, nomeingredientiTolti.getId());
                    nomeingredientiAggiunti.setText("PIU' ");
                    nomeingredientiAggiunti.setTextAppearance(context, R.style.testoPiccolo);
                    nomeingredientiAggiunti.setMaxEms(20);
                    nomeingredientiAggiunti.setLayoutParams(paramAggiunti);
                    baseLayoutCreata = true;
                }

                if (tipoExtra == 1) {
                    if (itrPizza.hasNext())
                        nomeingredientiAggiunti.setText(nomeingredientiAggiunti.getText() + nomeExtra + ", ");
                    else
                        nomeingredientiAggiunti.setText(nomeingredientiAggiunti.getText() + nomeExtra + "");

                } else {
                    if (itrPizza.hasNext())
                        nomeingredientiTolti.setText(nomeingredientiTolti.getText() + nomeExtra + ", ");
                    else
                        nomeingredientiTolti.setText(nomeingredientiTolti.getText() + nomeExtra + "");
                }
            }
            layoutPizze.addView(nomeingredientiTolti);
            layoutPizze.addView(nomeingredientiAggiunti);
        }
        return layoutPizze;
    }


    private TextView makeTableRowWithText(String text, int resource) {
        recyclableTextView = new TextView(context);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setText(text);
        recyclableTextView.setTextSize(25);
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(resource));
        return recyclableTextView;
    }

    private ImageButton makeTableRowWithImageButton(int img) {
        recyclableImageButton = new ImageButton(context);
        recyclableImageButton.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        recyclableImageButton.setImageResource(img);
        recyclableImageButton.setBackgroundColor(Color.TRANSPARENT);
        return recyclableImageButton;
    }

}