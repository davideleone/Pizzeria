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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NuovoOrdine fragment = new NuovoOrdine();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .commit();
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
        }, "GET_ELENCO_FATTORINI", new String[]{}).execute();
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
        }, "MONITORA_ORDINE", new String[]{dataRicerca}).execute();
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

                        final ProgressBar progressBar = new ProgressBar(context);
                        progressBar.setIndeterminate(true);
                        progressBar.setVisibility(View.GONE);
                        progressBar.setLayoutParams(paramsCaricamento);


                        new Loading(progressBar, context) {
                            @Override
                            protected void onPreExecute() {
                                progressBar.setVisibility(View.VISIBLE);
                            }


                        }.execute();
                        layoutPizze = dettaglioPizze(idOrdine, layoutPizze);

                        /**
                         //DA SISTEMARE
                         new Loading(progressBar, context, layoutPizze){
                        @Override protected void onPreExecute() {
                        bar.setVisibility(View.VISIBLE);
                        layoutPizze.setVisibility(View.GONE);
                        }

                        @Override protected void onProgressUpdate(Integer... values) {
                        layoutPizze = dettaglioPizze(idOrdine, layoutPizze);
                        super.onProgressUpdate(values);
                        }

                        @Override protected void onPostExecute(Void result) {
                        bar.setVisibility(View.GONE);
                        layoutPizze.setVisibility(View.VISIBLE);
                        layoutContenitore.addView(layoutPizze);
                        }
                        }.execute();*/


                        layoutDettaglio.addView(textStato);
                        layoutDettaglio.addView(imgStato);
                        layoutDettaglio.addView(textConsegna);
                        layoutDettaglio.addView(textTelefono);
                        layoutDettaglio.addView(separator);
                        layoutDettaglio.addView(progressBar);
                        layoutContenitore.addView(layoutDettaglio);
                        layoutContenitore.addView(layoutPizze);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Dettaglio ordine di " + cognome);
                        builder.setView(layoutContenitore);
                        builder.create().show();

                        //getPizzeOrdine(idOrdine);
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
                                        }, "MANDA_IN_CONSEGNA", new String[]{idOrdine}).execute();
                                    };
                                }, "ASSEGNA_FATTORINO", new String[]{getFattorinoSelezionato(), idOrdine}).execute();
                            } else {
                                new HttpManager.AsyncManager(new AsyncResponse() {
                                    @Override
                                    public void processFinish(Object output) {
                                        Toast.makeText(context, "Fattorino cambiato correttamente", Toast.LENGTH_SHORT).show();
                                        aggiornaTabella();
                                    };
                                }, "CAMBIA_FATTORINO", new String[]{getFattorinoSelezionato(), idOrdine}).execute();
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
                        HttpManager.execSimple("ACCETTA_ORDINE", idOrdine);
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
                                    /** TODO
                                     AGGIUNGERE SU DB REGOLA -> ON DELETE CASCADE COSI DA AVERE SOLO UN'UNICA QUERY
                                    DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE0.getValore(), false, idOrdine);
                                    DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE1.getValore(), false, idOrdine);
                                    DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE2.getValore(), false, idOrdine);
                                    DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE3.getValore(), false, idOrdine);
                                    */
                                    new HttpManager.AsyncManager(new AsyncResponse() {
                                        @Override
                                        public void processFinish(Object output) {
                                            aggiornaTabella();
                                            Toast.makeText(context, "Ordine Eliminato!", Toast.LENGTH_SHORT).show();
                                        };
                                    }, "ELIMINA_ORDINE", new String[]{idOrdine}).execute();
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

    private RelativeLayout dettaglioPizze(String idOrdine, RelativeLayout layoutPizze) {
        List<HashMap<String, Object>> risultatoQuery2;
        risultatoQuery2 = DBmanager.selectQuery(EnumQuery.GET_PIZZA_IN_ORDINE.getValore(), idOrdine);
        Iterator<HashMap<String, Object>> itr2 = risultatoQuery2.iterator();
        boolean flag = false;
        int count = 0;
        while (itr2.hasNext()) {
            HashMap<String, Object> riga = itr2.next();
            final String idcolonna = riga.get("id_colonna").toString();
            final String pizza = riga.get("nomeprodotto").toString();
            final float prezzo = Float.parseFloat(riga.get("prezzoprodotto").toString());

            RelativeLayout.LayoutParams paramsNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (count > 0)
                paramsNome.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
            paramsNome.setMargins(30, 30, 0, 0);
            TextView nomePizza = new TextView(context);
            nomePizza.setText("- " + pizza);
            nomePizza.setId(View.generateViewId());
            nomePizza.setTextAppearance(context, R.style.testoGrande);
            nomePizza.setLayoutParams(paramsNome);

            layoutPizze.addView(nomePizza);

            /*RelativeLayout.LayoutParams paramsPrezzo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsPrezzo.addRule(RelativeLayout.END_OF, nomePizza.getId());
            paramsPrezzo.setMargins(10,0,0,0);
            TextView prezzoPizza = new TextView(context);
            prezzoPizza.setText(""+prezzo);
            prezzoPizza.setId(View.generateViewId());
            prezzoPizza.setTextAppearance(context, R.style.testoGrande);
            prezzoPizza.setLayoutParams(paramsPrezzo);

            layoutPizze.addView(prezzoPizza);*/

            RelativeLayout.LayoutParams paramTolti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramTolti.addRule(RelativeLayout.BELOW, nomePizza.getId());
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
            }
            count++;
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