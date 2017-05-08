package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
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
    private List<Integer> idFattorini = new ArrayList<Integer>();
    private List<String> nomiFattorini = new ArrayList<String>();
    private Map<TableRow, TableLayout> mappaRows = new HashMap<TableRow, TableLayout>();
    private TextView recyclableTextView;
    private ImageButton recyclableImageButton;
    private String[] arrayFatt;
    private String testoDaStampare = "";
    private String intestazione = "";
    private String idOrdine = "";
    private HashMap<String, String> hashOrdineCompletato = null;
    private TextView nomeProdottoTxt;
    private ScrollView scrollView;
    private String sconto = "";

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

        View view = inflater.inflate(R.layout.activity_riepilogo_ordini, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        RelativeLayout layoutInserimentoToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutInserimentoToolbar);
        layoutInserimentoToolbar.setVisibility(View.GONE);
        RelativeLayout layoutRicercaToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutRicercaToolbar);
        layoutRicercaToolbar.setVisibility(View.GONE);
        tabellaOrdini = (TableLayout) view.findViewById(R.id.tabella_ordini);
        tabellaAssegnati = (TableLayout) view.findViewById(R.id.tabella_ordini_assegnati);
        spinnerDate = (Spinner) view.findViewById(R.id.date_settimane);

        if (bundle.getString("ID_ORDINE_COMPLETATO") != null) {
            idOrdine = bundle.getString("ID_ORDINE_COMPLETATO");
            hashOrdineCompletato = (HashMap<String, String>) bundle.getSerializable("HASH_ORDINE_COMPLETATO");
            sconto = hashOrdineCompletato.get("Sconto");

            new HttpManager.AsyncManager(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    stampaOrdine(output, hashOrdineCompletato);
                }
            }, context, "GET_PIZZE_CON_EXTRA", new String[]{idOrdine}).execute();


        }

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
        intestazione = "Riepilogo serata del " + spinnerDate.getSelectedItem().toString();
        testoDaStampare += intestazione + "\n";
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
                        RelativeLayout.LayoutParams paramContenitore = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        RelativeLayout layoutInterno = new RelativeLayout(context);
                        layoutInterno.setLayoutParams(paramContenitore);

                        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                        paramBarra.setMargins(30, 10, 30, 10);
                        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);

                        View view = new View(context);
                        view.setId(View.generateViewId());
                        view.setBackgroundColor(getResources().getColor(R.color.celeste));
                        view.setLayoutParams(paramBarra);
                        layoutInterno.addView(view);

                        RelativeLayout.LayoutParams paramMessaggio = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        paramMessaggio.addRule(RelativeLayout.BELOW, view.getId());
                        paramMessaggio.setMargins(50, 50, 0, 0);
                        paramMessaggio.addRule(RelativeLayout.CENTER_HORIZONTAL);

                        TextView messaggio = new TextView(context);
                        messaggio.setText("Con quest'azione tutti i dati relativi agli ordini saranno eliminati definitavamente!");
                        messaggio.setTextSize(15);
                        messaggio.setLayoutParams(paramMessaggio);
                        messaggio.setTextColor(getResources().getColor(R.color.nero));
                        layoutInterno.addView(messaggio);

                        new AlertDialog.Builder(context)
                                .setTitle("Sei sicuro di voler eliminare tutti gli ordini?")
                                .setView(layoutInterno)
                                .setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new HttpManager.AsyncManager(new AsyncResponse() {
                                            @Override
                                            public void processFinish(Object output) {
                                                aggiornaTabella();
                                                Toast.makeText(context, "Reset completato!", Toast.LENGTH_SHORT).show();
                                            }
                                        }, context, "ELIMINA_ORDINI_COMPLETI", new String[]{}).execute();
                                    }
                                })
                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setIcon(R.mipmap.alert)
                                .show();
                        break;
                    case 1:
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                mostraRiepilogo(output);
                            }
                        }, context, "GET_RIEPILOGO", new String[]{}).execute();
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

    private void mostraRiepilogo(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        testoDaStampare = "";
        if (!lista.isEmpty()) {
            RelativeLayout.LayoutParams paramContenitoreInformazioni = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout layoutInformazioni = new RelativeLayout(context);
            layoutInformazioni.setLayoutParams(paramContenitoreInformazioni);

            RelativeLayout.LayoutParams paramBarra2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
            paramBarra2.setMargins(30, 10, 30, 10);

            View view2 = new View(context);
            view2.setId(View.generateViewId());
            view2.setBackgroundColor(getResources().getColor(R.color.celeste));
            view2.setLayoutParams(paramBarra2);
            layoutInformazioni.addView(view2);

            RelativeLayout.LayoutParams paramtotale = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramtotale.addRule(RelativeLayout.BELOW, view2.getId());
            paramtotale.setMargins(20, 20, 0, 0);


            String euro = Currency.getInstance(Locale.ITALY).getCurrencyCode();

            TextView totaleSerata = new TextView(context);
            totaleSerata.setText("Totale serata: " + new DecimalFormat("#0.00 €").format(Float.parseFloat(lista.get(0).get("totale_serata"))));
            testoDaStampare += "\n" + totaleSerata.getText().toString().replace("€", euro) + "\n";
            totaleSerata.setId(View.generateViewId());
            totaleSerata.setTextSize(15);
            totaleSerata.setLayoutParams(paramtotale);
            totaleSerata.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(totaleSerata);

            RelativeLayout.LayoutParams paramTotaleConsegna = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramTotaleConsegna.addRule(RelativeLayout.BELOW, totaleSerata.getId());
            paramTotaleConsegna.setMargins(20, 10, 0, 0);

            TextView totaleSerataConsegna = new TextView(context);
            totaleSerataConsegna.setText("Totale consegne: " + new DecimalFormat("#0.00 €").format(Float.parseFloat(lista.get(0).get("totale_domicilio"))));
            testoDaStampare += totaleSerataConsegna.getText().toString().replace("€", euro) + "\n";
            totaleSerataConsegna.setId(View.generateViewId());
            totaleSerataConsegna.setTextSize(15);
            totaleSerataConsegna.setLayoutParams(paramTotaleConsegna);
            totaleSerataConsegna.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(totaleSerataConsegna);

            RelativeLayout.LayoutParams paramTotaleAsporto = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramTotaleAsporto.addRule(RelativeLayout.BELOW, totaleSerataConsegna.getId());
            paramTotaleAsporto.setMargins(20, 20, 0, 0);
            TextView totaleSerataAsporto = new TextView(context);
            totaleSerataAsporto.setText("Totale asporto: " + new DecimalFormat("#0.00 €").format(Float.parseFloat(lista.get(0).get("totale_asporto"))));
            testoDaStampare += totaleSerataAsporto.getText().toString().replace("€", euro) + "\n";
            totaleSerataAsporto.setId(View.generateViewId());
            totaleSerataAsporto.setTextSize(15);
            totaleSerataAsporto.setLayoutParams(paramTotaleAsporto);
            totaleSerataAsporto.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(totaleSerataAsporto);

            RelativeLayout.LayoutParams paramBarra3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
            paramBarra3.setMargins(30, 10, 30, 10);
            paramBarra3.addRule(RelativeLayout.BELOW, totaleSerataAsporto.getId());

            View view3 = new View(context);
            view3.setId(View.generateViewId());
            view3.setBackgroundColor(getResources().getColor(R.color.grigio));
            view3.setLayoutParams(paramBarra3);
            layoutInformazioni.addView(view3);

            for (int i = 0; i < intestazione.length() - 1; i++)
                testoDaStampare += "=";

            testoDaStampare += "\n";

            RelativeLayout.LayoutParams paramCountPizze = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramCountPizze.addRule(RelativeLayout.BELOW, view3.getId());
            paramCountPizze.setMargins(20, 20, 0, 0);
            TextView countPizze = new TextView(context);
            countPizze.setText("Pizze servite: " + lista.get(0).get("totale_pizze"));
            testoDaStampare += countPizze.getText().toString() + "\n";

            countPizze.setId(View.generateViewId());
            countPizze.setTextSize(15);
            countPizze.setLayoutParams(paramCountPizze);
            countPizze.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(countPizze);

            RelativeLayout.LayoutParams paramCountBibite = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramCountBibite.addRule(RelativeLayout.BELOW, countPizze.getId());
            paramCountBibite.setMargins(20, 20, 0, 0);

            TextView countBibite = new TextView(context);
            countBibite.setText("Bibite servite: " + lista.get(0).get("totale_bibite"));
            testoDaStampare += countBibite.getText().toString() + "\n";
            countBibite.setId(View.generateViewId());
            countBibite.setTextSize(15);
            countBibite.setLayoutParams(paramCountBibite);
            countBibite.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(countBibite);

            RelativeLayout.LayoutParams paramCountGastro = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramCountGastro.addRule(RelativeLayout.BELOW, countBibite.getId());
            paramCountGastro.setMargins(20, 20, 0, 0);

            TextView countGastro = new TextView(context);
            countGastro.setText("Gastronomia servita: " + lista.get(0).get("totale_gastronomia"));
            testoDaStampare += countGastro.getText().toString() + "\n";
            countGastro.setId(View.generateViewId());
            countGastro.setTextSize(15);
            countGastro.setLayoutParams(paramCountGastro);
            countGastro.setTextColor(getResources().getColor(R.color.nero));
            layoutInformazioni.addView(countGastro);


            new AlertDialog.Builder(context)
                    .setTitle("Riepilogo Serata del " + dataRicerca)
                    .setView(layoutInformazioni)
                    .setPositiveButton("Stampa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Stampa stampa = new Stampa(getActivity(), context);
                                stampa.findBT();
                                stampa.openBT();
                                stampa.sendData(testoDaStampare + "\n\n");
                                stampa.closeBT();
                                testoDaStampare = "";
                                Toast.makeText(context, "Stampa Effettuata!", Toast.LENGTH_SHORT).show();
                                new HttpManager.AsyncManager(new AsyncResponse() {
                                    @Override
                                    public void processFinish(Object output) {
                                        aggiornaTabella();
                                        Toast.makeText(context, "Reset completato!", Toast.LENGTH_SHORT).show();
                                    }
                                }, context, "ELIMINA_ORDINI_COMPLETI_SERATA", new String[]{}).execute();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    })
                    .setIcon(R.drawable.pizza_logo)
                    .show();
        } else {
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show();
        }
    }

    private void aggiornaTabella() {
        // DELETE OLD
        for (Map.Entry<TableRow, TableLayout> entry : mappaRows.entrySet())
            (entry.getValue()).removeView(entry.getKey());
        mappaRows.clear();

        // UPDATE
        caricaOrdini();
    }

    private void caricaFattorini() {
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                aggiornaFattorini(output);
            }
        }, context, "GET_ELENCO_FATTORINI", new String[]{}).execute();
    }

    private void aggiornaFattorini(Object param) {
        // DELETE OLD
        nomiFattorini.clear();
        idFattorini.clear();

        // UPDATE
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (!lista.isEmpty()) {
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final Integer idFatt = Integer.parseInt(riga.get("idfattorino"));
                final String nomeFatt = riga.get("nomecompleto");
                nomiFattorini.add(nomeFatt);
                idFattorini.add(idFatt);
            }
        }

        // UPDATE SPINNER
        arrayFatt = new String[nomiFattorini.size()];
        arrayFatt = nomiFattorini.toArray(arrayFatt);

    }

    private String getFattorinoSelezionato(int indice) {
        return idFattorini.get(indice).toString();
    }


    private void caricaOrdini() {
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                processaOrdini(output);
            }
        }, context, "MONITORA_ORDINE", new String[]{dataRicerca}).execute();
    }


    private void processaOrdini(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if (!lista.isEmpty()) {
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String cognome = riga.get("cognome");
                final String idOrdine = riga.get("id");
                final int stato = Integer.parseInt(riga.get("tipo"));
                final String consegna = riga.get("consegna");
                final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("totale"))) + " \u20ac";
                final String social = riga.get("social");
                final String telefono = riga.get("telefono");

                TableRow row = new TableRow(context);
                row.setBackgroundResource(R.drawable.table_bottom_style);

                final TextView dataOrdine = makeTableRowWithText(riga.get("dataconsegna"), R.dimen.dim_115dp);
                final TextView oraOrdine = makeTableRowWithText(riga.get("oraconsegna").substring(0, 5), R.dimen.dim_60dp);
                final TextView nomeCliente = makeTableRowWithText((riga.get("nome")), R.dimen.dim_115dp);
                TextView cognomeCliente = makeTableRowWithText(cognome, R.dimen.dim_115dp);
                TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_80dp);
                final TextView viaOrdine = makeTableRowWithText((riga.get("via").equals("null") ? " ----- " : riga.get("via")), R.dimen.dim_115dp);
                final TextView cittaOrdine = makeTableRowWithText(riga.get("citta").equals("null") ? " ----- " : riga.get("citta"), R.dimen.dim_125dp);
                viaOrdine.setTextSize(13);
                cittaOrdine.setTextSize(13);
                ImageButton btnMostra = makeTableRowWithImageButton(R.drawable.mostra);
                ImageButton btnModifica = makeTableRowWithImageButton(R.drawable.modifica_nero);
                ImageButton btnConsegna = makeTableRowWithImageButton(R.drawable.consegna);
                ImageButton btnElimina = makeTableRowWithImageButton(R.drawable.elimina);

                btnMostra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                fixDettagliPizze(output, telefono, cognome, stato);
                            }
                        }, context, "GET_PIZZE_CON_EXTRA", new String[]{idOrdine}).execute();
                    }
                });

                btnConsegna.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RelativeLayout layoutDialog = new RelativeLayout(context);
                        RelativeLayout.LayoutParams dialogParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutDialog.setLayoutParams(dialogParams);

                        final AlertDialog dialog = new AlertDialog.Builder(context)
                                .setView(layoutDialog)
                                .setTitle("Scelta Fattorino")
                                .create();

                        dialog.show();

                        dialog.getWindow().setLayout(360, arrayFatt.length * 150);

                        for (int i = 0; i < arrayFatt.length; i++) {
                            final int indice = i;


                            RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_80dp));
                            btnParams.setMargins(30, 0, 0, 0);
                            if (layoutDialog.getChildCount() > 0)
                                btnParams.addRule(RelativeLayout.BELOW, layoutDialog.getChildAt(layoutDialog.getChildCount() - 1).getId());

                            final Button btnFattorino = new Button(context);
                            btnFattorino.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_115dp));
                            btnFattorino.setGravity(Gravity.CENTER_VERTICAL);
                            btnFattorino.setLayoutParams(btnParams);
                            btnFattorino.setId(View.generateViewId());
                            btnFattorino.setText(arrayFatt[i]);
                            btnFattorino.setTextColor(Color.GRAY);
                            btnFattorino.setTextSize(15);
                            btnFattorino.setBackgroundColor(Color.TRANSPARENT);

                            RelativeLayout.LayoutParams barraParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                            barraParams.addRule(RelativeLayout.BELOW, btnFattorino.getId());
                            barraParams.setMargins(25, 0, 25, 0);

                            View barra = new View(context);
                            barra.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            barra.setId(View.generateViewId());
                            barra.setLayoutParams(barraParams);
                            layoutDialog.addView(btnFattorino);
                            layoutDialog.addView(barra);

                            btnFattorino.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (stato != 3) {
                                        new HttpManager.AsyncManager(new AsyncResponse() {
                                            @Override
                                            public void processFinish(Object output) {
                                                new HttpManager.AsyncManager(new AsyncResponse() {
                                                    @Override
                                                    public void processFinish(Object output) {
                                                        Toast.makeText(context, "Consegna affidata al fattorino", Toast.LENGTH_SHORT).show();
                                                        aggiornaTabella();
                                                    }
                                                }, context, "MANDA_IN_CONSEGNA", new String[]{idOrdine}).execute();
                                            }

                                            ;
                                        }, context, "ASSEGNA_FATTORINO", new String[]{getFattorinoSelezionato(indice), idOrdine}).execute();
                                    } else {
                                        new HttpManager.AsyncManager(new AsyncResponse() {
                                            @Override
                                            public void processFinish(Object output) {
                                                Toast.makeText(context, "Fattorino cambiato correttamente", Toast.LENGTH_SHORT).show();
                                                aggiornaTabella();
                                            }
                                        }, context, "CAMBIA_FATTORINO", new String[]{getFattorinoSelezionato(indice), idOrdine}).execute();
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }

                    }
                });


                btnModifica.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        HashMap<String, String> tmp_hash = new HashMap<>(9);
                        tmp_hash.put("idordine", idOrdine);
                        tmp_hash.put("data", dataOrdine.getText().toString());
                        tmp_hash.put("ora", oraOrdine.getText().toString());
                        tmp_hash.put("cognome", cognome);
                        tmp_hash.put("nome", nomeCliente.getText().toString());
                        tmp_hash.put("via", viaOrdine.getText().toString());
                        tmp_hash.put("citta", cittaOrdine.getText().toString());
                        tmp_hash.put("telefono", telefono);
                        tmp_hash.put("sconto", "" + sconto);

                        NuovoOrdine fragment = new NuovoOrdine();
                        bundle.putSerializable("HASHMAP_ORDINE", tmp_hash);

                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.flContent, fragment)
                                .commit();
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
                                            }
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
                row.addView(btnModifica);

                if (!(stato == 2 || stato == 3)) {
                    btnConsegna.setEnabled(false);
                    btnConsegna.setImageResource(R.drawable.consegna_not_enabled);
                } else {
                    btnConsegna.setEnabled(true);
                    btnConsegna.setImageResource(R.drawable.consegna);
                }

                row.addView(btnConsegna);
                row.addView(btnElimina);

                if (stato == 1)
                    row.setBackgroundResource(R.color.menu_primary);

                mappaRows.put(row, (!(stato == 1 || stato == 3) ? tabellaOrdini : tabellaAssegnati));
                (!(stato == 1 || stato == 3) ? tabellaOrdini : tabellaAssegnati).addView(row);
            }
        } else {
            Toast.makeText(context, "Nessun nuovo ordine", Toast.LENGTH_SHORT).show();
        }

    }

    private void mostraDettaglio(SparseArray<HashMap<String, Object>> sparseDettagli, String telefono, String cognome, String stato) {
        final RelativeLayout layoutContenitore = new RelativeLayout(context);

        RelativeLayout layoutDettaglio = new RelativeLayout(context);
        layoutDettaglio.setId(View.generateViewId());

        RelativeLayout.LayoutParams paramsStatoText = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsStatoText.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsStatoText.setMargins(0, 15, 0, 0);
        TextView textStato = new TextView(context);
        textStato.setId(View.generateViewId());
        textStato.setText("Stato: " + stato);
        textStato.setTextAppearance(context, R.style.testoGrande);
        textStato.setLayoutParams(paramsStatoText);

        /*RelativeLayout.LayoutParams paramsConsegna = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsConsegna.addRule(RelativeLayout.BELOW, textStato.getId());
        paramsConsegna.addRule(RelativeLayout.CENTER_HORIZONTAL);
        TextView textConsegna = new TextView(context);
        textConsegna.setId(View.generateViewId());
        textConsegna.setText("Assegnato a: ");
        textConsegna.setTextAppearance(context, R.style.testoGrande);
        textConsegna.setLayoutParams(paramsConsegna);*/

        RelativeLayout.LayoutParams paramsTelefono = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_250dp), ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTelefono.addRule(RelativeLayout.BELOW, textStato.getId());
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

        RelativeLayout.LayoutParams paramsLayoutPizze = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutPizze.addRule(RelativeLayout.BELOW, layoutDettaglio.getId());

        scrollView = new ScrollView(context);
        scrollView.setLayoutParams(paramsLayoutPizze);

        scrollView = dettaglioPizze(sparseDettagli, scrollView);

        layoutDettaglio.addView(textStato);
        //layoutDettaglio.addView(textConsegna);
        layoutDettaglio.addView(textTelefono);
        layoutDettaglio.addView(separator);
        layoutContenitore.addView(layoutDettaglio);
        layoutContenitore.addView(scrollView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dettaglio ordine di " + cognome);
        builder.setView(layoutContenitore);
        builder.create().show();

    }


    private void fixDettagliPizze(Object param, String telefono, String cognome, int stato) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, Integer> hashExtra = new HashMap<String, Integer>();
        // SPARSE -> HASHMAP (STRING, OBJECT [STRING - HASHMAP<STRING,STRING>])
        SparseArray<HashMap<String, Object>> sparseDettagli = new SparseArray<HashMap<String, Object>>();

        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final int idColonna = Integer.parseInt(riga.get("id_colonna"));
            final String nomeProdotto = riga.get("nomeprodotto");
            final String prezzoProdotto = riga.get("prezzoprodotto");
            final String nomeExtra = riga.get("nomeextra");
            final String tipoExtra = riga.get("tipo");
            final String idMetro = riga.get("idmetro");

            HashMap<String, Object> hashValori = (sparseDettagli.get(idColonna) != null) ? sparseDettagli.get(idColonna) : new HashMap<String, Object>();
            hashValori.put("nomeprodotto", nomeProdotto);
            hashValori.put("prezzoprodotto", prezzoProdotto);
            hashValori.put("metro", (idMetro.equals("null")) ? "-1" : idMetro); // SE NULL, IDMETRO == -1

            HashMap<String, Integer> tmpExtra = (hashValori.get("extra") != null) ? (HashMap<String, Integer>) (hashValori.get("extra")) : new HashMap<String, Integer>();
            if (!nomeExtra.equals("null")) {
                if (tmpExtra.get(nomeExtra) != null) {
                    tmpExtra.put(nomeExtra, 3); // DOPPIO EXTRA
                } else {
                    tmpExtra.put(nomeExtra, Integer.parseInt(tipoExtra));
                }
            }
            hashValori.put("extra", tmpExtra);
            sparseDettagli.put(idColonna, hashValori);
        }
        mostraDettaglio(sparseDettagli, telefono, cognome, convertiStato(stato));
    }


    private ScrollView dettaglioPizze(SparseArray<HashMap<String, Object>> sparseDettagli, ScrollView scrollView) {

        RelativeLayout layoutPizze = new RelativeLayout(context);
        //layoutPizze.setLayoutParams(paramsLayoutPizze);
        layoutPizze.setId(View.generateViewId());

        int idmetro_old = 0;
        for (int i = 0; i < sparseDettagli.size(); i++) {
            boolean tolti = false;
            boolean aggiunti = false;
            HashMap<String, Object> hashTmp = sparseDettagli.valueAt(i);
            final String nomeProdotto = hashTmp.get("nomeprodotto").toString();
            final String prezzoProdotto = hashTmp.get("prezzoprodotto").toString();
            final int idMetro = Integer.parseInt(hashTmp.get("metro").toString());


            if ((idMetro != idmetro_old) && idMetro != -1) {

                RelativeLayout.LayoutParams paramsMetro = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (layoutPizze.getChildCount() > 0)
                    paramsMetro.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
                paramsMetro.setMargins(30, 30, 0, 0);

                TextView mezzometroTxt = new TextView(context);
                mezzometroTxt.setText("1/2 Metro");
                mezzometroTxt.setLayoutParams(paramsMetro);
                mezzometroTxt.setId(View.generateViewId());
                mezzometroTxt.setTextSize(15);
                mezzometroTxt.setTypeface(null, Typeface.BOLD);
                layoutPizze.addView(mezzometroTxt);

                idmetro_old = idMetro;
            }


            RelativeLayout.LayoutParams paramsNome = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (layoutPizze.getChildCount() > 0)
                paramsNome.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
            if (idMetro != -1)
                paramsNome.setMargins(60, 30, 0, 0);
            else
                paramsNome.setMargins(30, 30, 0, 0);


            nomeProdottoTxt = new TextView(context);
            nomeProdottoTxt.setText("- " + nomeProdotto);
            nomeProdottoTxt.setId(View.generateViewId());
            nomeProdottoTxt.setTextSize(15);
            nomeProdottoTxt.setTypeface(null, Typeface.BOLD);
            nomeProdottoTxt.setLayoutParams(paramsNome);
            layoutPizze.addView(nomeProdottoTxt);

            RelativeLayout.LayoutParams paramsPrezzo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsPrezzo.addRule(RelativeLayout.END_OF, nomeProdottoTxt.getId());
            if (layoutPizze.getChildCount() > 1)
                paramsPrezzo.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 2).getId());

            paramsPrezzo.setMargins(20, 30, 0, 0);

            TextView prezzoPizza = new TextView(context);
            prezzoPizza.setText(new DecimalFormat("#0.00 €").format(Float.parseFloat(prezzoProdotto)));
            prezzoPizza.setId(View.generateViewId());
            prezzoPizza.setTextSize(15);
            prezzoPizza.setTypeface(null, Typeface.BOLD);
            prezzoPizza.setLayoutParams(paramsPrezzo);
            layoutPizze.addView(prezzoPizza);


            RelativeLayout.LayoutParams paramTolti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramTolti.addRule(RelativeLayout.BELOW, nomeProdottoTxt.getId());
            paramTolti.setMargins(80, 0, 0, 0);

            TextView nomeingredientiTolti = new TextView(context);
            nomeingredientiTolti.setText("SENZA ");
            nomeingredientiTolti.setId(View.generateViewId());
            nomeingredientiTolti.setTextAppearance(context, R.style.testoPiccolo);
            nomeingredientiTolti.setMaxEms(20);
            nomeingredientiTolti.setLayoutParams(paramTolti);

            RelativeLayout.LayoutParams paramAggiunti = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramAggiunti.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
            paramAggiunti.setMargins(80, 0, 0, 0);

            TextView nomeingredientiAggiunti = new TextView(context);
            nomeingredientiAggiunti.setText("PIU' ");
            nomeingredientiAggiunti.setId(View.generateViewId());
            nomeingredientiAggiunti.setTextAppearance(context, R.style.testoPiccolo);
            nomeingredientiAggiunti.setMaxEms(20);


            HashMap<String, Integer> tmpExtra = (HashMap<String, Integer>) (hashTmp.get("extra"));
            Iterator itr = tmpExtra.entrySet().iterator();


            while (itr.hasNext()) {
                Map.Entry riga = (Map.Entry) itr.next();
                final String nomeIngrediente = riga.getKey().toString();
                final int tipoIngrediente = Integer.parseInt(riga.getValue().toString());

                if (nomeIngrediente.equals("Integrale") || nomeIngrediente.equals("7 Cereali")) {
                    prezzoPizza.append(" [" + nomeIngrediente + "]");
                } else if (tipoIngrediente == 2) {
                    tolti = true;
                    nomeingredientiTolti.append(" " + nomeIngrediente);
                } else if (tipoIngrediente == 1) {
                    aggiunti = true;
                    nomeingredientiAggiunti.append(" " + nomeIngrediente);
                } else if (tipoIngrediente == 3) {
                    aggiunti = true;
                    nomeingredientiAggiunti.append(" 2 x " + nomeIngrediente);
                }
            }


            if (tolti) {
                layoutPizze.addView(nomeingredientiTolti);
            }
            if (aggiunti) {
                if (tolti)
                    paramAggiunti.addRule(RelativeLayout.BELOW, layoutPizze.getChildAt(layoutPizze.getChildCount() - 1).getId());
                else
                    paramAggiunti.addRule(RelativeLayout.BELOW, nomeProdottoTxt.getId());
                nomeingredientiAggiunti.setLayoutParams(paramAggiunti);
                layoutPizze.addView(nomeingredientiAggiunti);
            }

        }
        scrollView.addView(layoutPizze);
        return scrollView;
    }


    private TextView makeTableRowWithText(String text, int resource) {
        recyclableTextView = new TextView(context);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setText(text);
        recyclableTextView.setTextSize(15);
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(resource));
        return recyclableTextView;
    }

    private ImageButton makeTableRowWithImageButton(int img) {
        recyclableImageButton = new ImageButton(context);
        recyclableImageButton.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        recyclableImageButton.setMaxHeight(getResources().getDimensionPixelSize(R.dimen.dim_26dp));
        recyclableImageButton.setPadding(10, 0, 0, 0);
        recyclableImageButton.setImageResource(img);
        recyclableImageButton.setBackgroundColor(Color.TRANSPARENT);
        return recyclableImageButton;
    }

    private String convertiStato(int Stato) {
        String stato = "";
        switch (Stato) {
            case 1:
                stato = "D'asporto";
                break;
            case 2:
                stato = "Da affidare al fattorino";
                break;
            case 3:
                stato = "In consegna";
                break;
        }
        return stato;
    }


    private void stampaOrdine(Object param, HashMap<String, String> hashOrdineCompletato) {
        testoDaStampare = "";
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        HashMap<String, Integer> hashExtra = new HashMap<String, Integer>();
        // SPARSE -> HASHMAP (STRING, OBJECT [STRING - HASHMAP<STRING,STRING>])
        SparseArray<HashMap<String, Object>> sparseDettagli = new SparseArray<HashMap<String, Object>>();

        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final int idColonna = Integer.parseInt(riga.get("id_colonna"));
            final String nomeProdotto = riga.get("nomeprodotto");
            final String prezzoProdotto = riga.get("prezzoprodotto");
            final String nomeExtra = riga.get("nomeextra");
            final String tipoExtra = riga.get("tipo");
            final String idMetro = riga.get("idmetro");

            HashMap<String, Object> hashValori = (sparseDettagli.get(idColonna) != null) ? sparseDettagli.get(idColonna) : new HashMap<String, Object>();
            hashValori.put("nomeprodotto", nomeProdotto);
            hashValori.put("prezzoprodotto", prezzoProdotto);
            hashValori.put("metro", (idMetro.equals("null")) ? "-1" : idMetro); // SE NULL, IDMETRO == -1

            HashMap<String, Integer> tmpExtra = (hashValori.get("extra") != null) ? (HashMap<String, Integer>) (hashValori.get("extra")) : new HashMap<String, Integer>();
            if (!nomeExtra.equals("null")) {
                if (tmpExtra.get(nomeExtra) != null) {
                    tmpExtra.put(nomeExtra, 3); // DOPPIO EXTRA
                } else {
                    tmpExtra.put(nomeExtra, Integer.parseInt(tipoExtra));
                }
            }
            hashValori.put("extra", tmpExtra);
            sparseDettagli.put(idColonna, hashValori);
        }


        int idmetro_old = -1;
        float countTotale = 0;
        int countPizze = 1;
        boolean metro = false;
        boolean metroUltima = false;
        for (int i = 0; i < sparseDettagli.size(); i++) {
            HashMap<String, Object> hashTmp = sparseDettagli.valueAt(i);
            final int idMetro = Integer.parseInt(hashTmp.get("metro").toString());
            final String prezzoprodotto = hashTmp.get("prezzoprodotto").toString();

            String intestazioneMetro = "======== 1/2 Metro =======";

            if (((idmetro_old != idMetro)) && idMetro != -1)
                testoDaStampare += intestazioneMetro + "\n";


            /*if (((idmetro_old != idMetro) && idmetro_old != -1) && i != 0) {

                switch (countPizze) {
                    case 1:
                        countTotale *= 2.5;
                        break;
                    case 2:
                        countTotale *= 1.25;
                        break;
                    case 3:
                        countTotale *= 0.83;
                        break;
                }

                if (countPizze != 0)
                    testoDaStampare += "======= (" + new DecimalFormat("#0.00 EUR").format(Funzioni.arrotonda((double) countTotale)) + ") ======\n";

                countTotale = 0;
                countPizze = 0;

            }

            if (idMetro != -1) {
                metro = true;
                countPizze++;
                countTotale += Float.parseFloat(prezzoprodotto);
            } else metro = false;*/

            idmetro_old = idMetro;

            String nomeprodotto = hashTmp.get("nomeprodotto").toString();

            String ingredientiAggiunti = (idMetro != -1) ? "  PIU'" : "PIU'";
            String ingredientiTolti = (idMetro != -1) ? "  NO" : "NO";

            HashMap<String, Integer> tmpExtra = (HashMap<String, Integer>) (hashTmp.get("extra"));
            Iterator itrIngredienti = tmpExtra.entrySet().iterator();
            while (itrIngredienti.hasNext()) {
                Map.Entry riga = (Map.Entry) itrIngredienti.next();
                final String nomeIngrediente = riga.getKey().toString();
                final int tipoIngrediente = Integer.parseInt(riga.getValue().toString());
                if (nomeIngrediente.equals("Integrale") || nomeIngrediente.equals("7 Cereali")) {
                    nomeprodotto += " [" + nomeIngrediente + "]";
                } else if (tipoIngrediente == 1)
                    ingredientiAggiunti += " " + nomeIngrediente;
                else if (tipoIngrediente == 2)
                    ingredientiTolti += " " + nomeIngrediente + " ";
                else if (tipoIngrediente == 3)
                    ingredientiAggiunti += " 2x" + nomeIngrediente;
            }

            testoDaStampare += (idMetro != -1) ? "   + " + nomeprodotto + " " + new DecimalFormat("#0.00 EUR").format(Float.parseFloat(prezzoprodotto)) + "\n" : "- " + nomeprodotto + " " + new DecimalFormat("#0.00 EUR").format(Float.parseFloat(prezzoprodotto)) + "\n";
            if (!(ingredientiTolti.equals("NO") || (ingredientiTolti.equals("  NO")))) {
                testoDaStampare += " " + ingredientiTolti + "\n";
                if (ingredientiAggiunti.equals("PIU'") || (ingredientiAggiunti.equals("  PIU'")))
                    testoDaStampare += "\n";
            }
            if (!(ingredientiAggiunti.equals("PIU'") || (ingredientiAggiunti.equals("  PIU'")))) {
                testoDaStampare += " " + ingredientiAggiunti + "\n\n";

            }
        }


        /*if (idmetro_old != -1) {
            switch (countPizze) {
                case 1:
                    countTotale *= 2.5;
                    break;
                case 2:
                    countTotale *= 1.25;
                    break;
                case 3:
                    countTotale *= 0.83;
                    break;
            }

            if (countPizze != 0)
                testoDaStampare += "======= (" + new DecimalFormat("#0.00 EUR").format(Funzioni.arrotonda((double) countTotale)) + ") ======\n";

        }*/



        testoDaStampare += "--------------------------\n";
        testoDaStampare += "Totale:        " + new DecimalFormat("#0.00 EUR").format(Float.parseFloat(hashOrdineCompletato.get("Totale"))) + "\n\n";

        testoDaStampare += "Data: " + Funzioni.formattaData(hashOrdineCompletato.get("Data")) + "\n";
        testoDaStampare += "Ora: " + hashOrdineCompletato.get("Ora") + "\n";
        if (!hashOrdineCompletato.get("Via").equals(""))
            testoDaStampare += "Via: " + hashOrdineCompletato.get("Via") + "\n";
        if (!hashOrdineCompletato.get("Citta").equals(""))
            testoDaStampare += "Citta: " + hashOrdineCompletato.get("Citta") + "\n";
        testoDaStampare += "Sig: " + hashOrdineCompletato.get("Cognome");
        if (!hashOrdineCompletato.get("Nome").equals(""))
            testoDaStampare += ", " + hashOrdineCompletato.get("Nome") + "\n";
        else testoDaStampare += "\n";
        if (!hashOrdineCompletato.get("Telefono").equals(""))
            testoDaStampare += "Tel: " + hashOrdineCompletato.get("Telefono") + "\n";

        System.out.println(testoDaStampare);

        try {
            Stampa stampa = new Stampa(getActivity(), context);
            stampa.findBT();
            stampa.openBT();
            stampa.sendData(testoDaStampare + "\n");
            stampa.closeBT();
            Toast.makeText(context, "Stampa Effettuata!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


}