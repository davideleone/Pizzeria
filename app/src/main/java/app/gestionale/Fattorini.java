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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Manuel on 06/04/2017.
 */

public class Fattorini extends Fragment {

    private TableLayout tabellaOrdini;
    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;

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

        View view = inflater.inflate(R.layout.activity_fattorini, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);

        tabellaOrdini = (TableLayout) view.findViewById(R.id.tabella_fattorini);


        caricaOrdini();
     //   aggiornaFattorini();
/**
        FloatingActionButton btn_add = (FloatingActionButton) view.findViewById(R.id.btn_Aggiungi);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });*/

        return view;
    }

    public void caricaOrdini() {
        System.out.println("DATA = " + Funzioni.getCurrentDate());
        List<HashMap<String, Object>> risultatoQuery;
        risultatoQuery = DBmanager.selectQuery(EnumQuery.GET_TOTALE_FATTORINI.getValore(), Funzioni.getCurrentDate());
        Iterator<HashMap<String, Object>> itr = risultatoQuery.iterator();
        if (itr.hasNext()) {
            while (itr.hasNext()) {
                HashMap<String, Object> riga = itr.next();
                final String nome = riga.get("nomefatt").toString();
                final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("prezzotot").toString())) + " \u20ac";

                TableRow row = new TableRow(context);
                row.setBackgroundResource(R.drawable.table_bottom_style);

                TextView nomeFattorino = makeTableRowWithText(nome, R.dimen.dim_450dp);
                TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_450dp);

                ImageButton btnMostra = makeTableRowWithImageButton(R.drawable.mostra);
/**
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
                                .setIcon(R.drawable.logo)
                                .show();

                    }
                });*/


                row.addView(nomeFattorino);
                row.addView(totaleOrdine);

                row.addView(btnMostra);

                tabellaOrdini.addView(row);
            }
        } else {
            Toast.makeText(context, "Nessun ordine assegnato per la consegna", Toast.LENGTH_SHORT).show();
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
