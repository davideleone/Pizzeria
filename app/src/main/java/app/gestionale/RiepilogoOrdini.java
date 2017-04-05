package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

public class RiepilogoOrdini extends Fragment {

    private TableLayout tabellaOrdini;
    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;

    private Spinner spinnerDate;
    private ArrayAdapter<String> arrayDate;
    private String dataRicerca;

    private List<Integer> idOrdini = new ArrayList<Integer>();

    private String fattorini[] = {"Matteo", "Mirko", "Davide"};
    private Spinner spinnerFattorini;
    private ArrayAdapter<String> arrayFattorini;

    private List<Integer> idFattorini = new ArrayList<Integer>();
    private List<String> nomiFattorini = new ArrayList<String>();
    private List<TableRow> listaRows = new ArrayList<TableRow>();
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
        aggiornaFattorini();
        return view;
    }

    private void aggiornaTabella() {
        // DELETE OLD
        for (TableRow k : listaRows) tabellaOrdini.removeView(k);
        listaRows.clear();

        // UPDATE
        caricaOrdini();
    }

    private void aggiornaFattorini() {
        // DELETE OLD
        nomiFattorini.clear();
        idFattorini.clear();

        // UPDATE
        List<HashMap<String, Object>> risultatoQuery;
        risultatoQuery = DBmanager.selectQuery(EnumQuery.GET_ELENCO_FATTORINI.getValore());
        Iterator<HashMap<String, Object>> itr2 = risultatoQuery.iterator();
        if (itr2.hasNext()) {
            while (itr2.hasNext()) {
                HashMap<String, Object> riga = itr2.next();
                final Integer idFatt = Integer.parseInt(riga.get("idfattorino").toString());
                final String nomeFatt = riga.get("nomecompleto").toString();
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

    private int getFattorinoSelezionato() {
        return idFattorini.get(spinnerFattorini.getSelectedItemPosition());
    }

    public void caricaOrdini() {
        List<HashMap<String, Object>> risultatoQuery;
        risultatoQuery = DBmanager.selectQuery(EnumQuery.MONITORA_ORDINE.getValore(), dataRicerca);
        Iterator<HashMap<String, Object>> itr = risultatoQuery.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, Object> riga = itr.next();
                final String cognome = riga.get("cognome").toString();
                final String idOrdine = riga.get("id").toString();
                final String stato = riga.get("tipo").toString();
                final String consegna = riga.get("consegna").toString();
                final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("totale").toString())) + " \u20ac";
                final String social = riga.get("social").toString();

                TableRow row = new TableRow(context);
                row.setBackgroundResource(R.drawable.table_bottom_style);

                TextView dataOrdine = makeTableRowWithText(riga.get("dataconsegna").toString(), R.dimen.dim_150dp);
                TextView oraOrdine = makeTableRowWithText(riga.get("oraconsegna").toString().substring(0, 5), R.dimen.dim_80dp);
                TextView nomeCliente = makeTableRowWithText((riga.get("nome").toString()), R.dimen.dim_150dp);
                TextView cognomeCliente = makeTableRowWithText(cognome, R.dimen.dim_150dp);
                TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_100dp);
                TextView viaOrdine = makeTableRowWithText(riga.get("via").toString(), R.dimen.dim_200dp);
                TextView cittaOrdine = makeTableRowWithText(riga.get("citta").toString(), R.dimen.dim_150dp);

                ImageButton btnMostra = makeTableRowWithImageButton(R.drawable.mostra);
                ImageButton btnAccetta = makeTableRowWithImageButton(R.drawable.accetta);
                ImageButton btnConsegna = makeTableRowWithImageButton(R.drawable.consegna);
                ImageButton btnElimina = makeTableRowWithImageButton(R.drawable.elimina);

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
                                DBmanager.updateQuery(EnumQuery.ASSEGNA_FATTORINO.getValore(), false, getFattorinoSelezionato(), idOrdine);
                                DBmanager.updateQuery(EnumQuery.MANDA_IN_CONSEGNA.getValore(), false, idOrdine);
                                Toast.makeText(context, "Consegna affidata al fattorino", Toast.LENGTH_SHORT).show();
                                aggiornaTabella();
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
                        DBmanager.updateQuery(EnumQuery.ACCETTA_ORDINE.getValore(), false, idOrdine);
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
                                        DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE0.getValore(), false, idOrdine);
                                        DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE1.getValore(), false, idOrdine);
                                        DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE2.getValore(), false, idOrdine);
                                        DBmanager.updateQuery(EnumQuery.ELIMINA_ORDINE3.getValore(), false, idOrdine);
                                        aggiornaTabella();
                                        Toast.makeText(context, "Ordine Eliminato!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                //.setIcon(R.drawable.logo)
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

                listaRows.add(row);
                tabellaOrdini.addView(row);
            }
        } else {
            Toast.makeText(context, "Nessun nuovo ordine", Toast.LENGTH_SHORT).show();
        }
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