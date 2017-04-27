package app.gestionale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        RelativeLayout layoutInserimentoToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutInserimentoToolbar);
        layoutInserimentoToolbar.setVisibility(View.GONE);
        RelativeLayout layoutRicercaToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutRicercaToolbar);
        layoutRicercaToolbar.setVisibility(View.GONE);
        tabellaOrdini = (TableLayout) view.findViewById(R.id.tabella_fattorini);

        caricaFattorini();

        FloatingActionButton btn_add = (FloatingActionButton) view.findViewById(R.id.btn_Aggiungi);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout inserimentoLayout = new RelativeLayout(context);
                RelativeLayout.LayoutParams paramInserimento = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramInserimento.addRule(RelativeLayout.CENTER_HORIZONTAL);
                paramInserimento.addRule(RelativeLayout.CENTER_VERTICAL);
                paramInserimento.setMargins(0, 30, 0, 0);
                inserimentoLayout.setLayoutParams(paramInserimento);

                RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
                paramBarra.setMargins(30, 20, 30, 10);
                paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);

                View viewBarra = new View(context);
                viewBarra.setId(View.generateViewId());
                viewBarra.setBackgroundColor(getResources().getColor(R.color.celeste));
                viewBarra.setLayoutParams(paramBarra);
                inserimentoLayout.addView(viewBarra);

                RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextInputLayout nomeInput = new TextInputLayout(context);
                editTextParams.addRule(RelativeLayout.BELOW, viewBarra.getId());
                editTextParams.setMargins(40, 20, 0, 40);
                nomeInput.setLayoutParams(editTextParams);
                nomeInput.setId(View.generateViewId());

                final TextInputEditText nome = new TextInputEditText(context);
                final TextInputEditText cognome = new TextInputEditText(context);

                nome.setTextSize(25);
                nome.setHint("Nome Fattorino");
                nomeInput.addView(nome);

                RelativeLayout.LayoutParams editTextCognomeParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextInputLayout cognomeInput = new TextInputLayout(context);
                editTextCognomeParams.addRule(RelativeLayout.BELOW, nomeInput.getId());
                editTextCognomeParams.setMargins(40, 0, 0, 40);
                cognomeInput.setLayoutParams(editTextCognomeParams);
                cognome.setTextSize(25);
                cognome.setHint("Cognome Fattorino");
                cognomeInput.addView(cognome);

                inserimentoLayout.addView(nomeInput);
                inserimentoLayout.addView(cognomeInput);


                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(inserimentoLayout)
                        .setTitle("Inserisci Nuovo Fattorino")
                        .setPositiveButton("Conferma", null)
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
                                    Toast.makeText(context, "Fattorino inserito correttamente", Toast.LENGTH_SHORT).show();
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

    private void caricaFattorini() {
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                caricaFattorini(output);
            }
        }, context, "GET_TOTALE_FATTORINI", new String[]{Funzioni.getCurrentDate()}).execute();
    }


    private void caricaFattorini(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        if(!lista.isEmpty()){
            while (itr.hasNext()) {
                HashMap<String, String> riga = itr.next();
                final String idFattorino = riga.get("idfattorino");
                final String nome = riga.get("nomefatt");
                final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("prezzotot"))) + " \u20ac";

                TableRow row = new TableRow(context);
                row.setBackgroundResource(R.drawable.table_bottom_style);

                TextView nomeFattorino = makeTableRowWithText(nome, R.dimen.dim_450dp);
                TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_450dp);

                ImageButton btnMostra = makeTableRowWithImageButton(R.drawable.mostra);
                btnMostra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                riepilogoFattorini(output, nome, totale);
                            }
                        }, null, "GET_RIEPILOGO_FATTORINO", new String[]{idFattorino}).execute();
                    }
                });
                row.addView(nomeFattorino);
                row.addView(totaleOrdine);
                row.addView(btnMostra);

                tabellaOrdini.addView(row);
            }
        } else {
            Toast.makeText(context, "Nessun ordine assegnato per la consegna", Toast.LENGTH_SHORT).show();
        }
    }

    private void riepilogoFattorini(Object param, String nomeFatt, String totaleConsegne) {
        RelativeLayout contenitoreLayoutFattorino = new RelativeLayout(context);

        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramBarra.setMargins(30, 0, 20, 30);
        View barraInAlto = new View(context);
        barraInAlto.setId(View.generateViewId());
        barraInAlto.setBackgroundResource(R.color.celeste);
        barraInAlto.setLayoutParams(paramBarra);
        contenitoreLayoutFattorino.addView(barraInAlto);

        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final String totale = riga.get("totale");
            final String via = riga.get("via");

            RelativeLayout.LayoutParams paramVia = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_300dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramVia.addRule(RelativeLayout.BELOW, contenitoreLayoutFattorino.getChildAt(contenitoreLayoutFattorino.getChildCount() - 1).getId());
            paramVia.setMargins(30, 0, 30, 30);
            TextView viaText = new TextView(context);
            viaText.setId(View.generateViewId());
            viaText.setText(via);
            viaText.setTextAppearance(context, R.style.testoGrande);
            viaText.setLayoutParams(paramVia);
            contenitoreLayoutFattorino.addView(viaText);

            RelativeLayout.LayoutParams paramTotale = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_300dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramTotale.addRule(RelativeLayout.BELOW, contenitoreLayoutFattorino.getChildAt(contenitoreLayoutFattorino.getChildCount() - 2).getId());
            paramTotale.addRule(RelativeLayout.END_OF, viaText.getId());

            TextView totaleText = new TextView(context);
            totaleText.setId(View.generateViewId());
            totaleText.setTextAppearance(context, R.style.testoGrande);
            totaleText.setText("" + new DecimalFormat("#0.00 €").format(Float.parseFloat(totale)));
            totaleText.setLayoutParams(paramTotale);
            contenitoreLayoutFattorino.addView(totaleText);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Riepilogo Fattorino -" + nomeFatt + " - " + totaleConsegne);
        builder.setView(contenitoreLayoutFattorino);
        builder.create().show();
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
        recyclableImageButton.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_150dp));
        recyclableImageButton.setMaxHeight(getResources().getDimensionPixelSize(R.dimen.dim_26dp));
        recyclableImageButton.setPadding(40, 0, 0, 0);
        recyclableImageButton.setImageResource(img);
        recyclableImageButton.setBackgroundColor(Color.TRANSPARENT);
        return recyclableImageButton;
    }
}
