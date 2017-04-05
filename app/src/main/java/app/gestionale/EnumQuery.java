package app.gestionale;

public enum EnumQuery {
    GET_PIZZA_RICERCA_CON_INGREDIENTE("SELECT nome, prezzo FROM prodotto p join composto c on p.nome = c.nomeprodotto where c.nomeingrediente ilike ?"),
    GET_PIZZA_RICERCA("SELECT nome,prezzo FROM prodotto where tipo = 'Pizza' AND nome ilike ? order by nome"),
    GET_LISTA_UTENTI("select distinct nome, cognome, prefisso, telefono from cliente where COALESCE(prefisso, '') <> ''"),
    GET_ORDINE_INUTILI("select id from ordine where dataconsegna IS NULL"),
    ELIMINA_ORDINE0("DELETE FROM extra where idcolonnaextra = (SELECT id_colonna from contiene where idordine = ?)"),
    ELIMINA_ORDINE1("DELETE FROM crea WHERE idordine = ?"),
    ELIMINA_ORDINE2("DELETE FROM contiene WHERE idordine = ?"),
    ELIMINA_ORDINE3("DELETE FROM ordine WHERE id = ?"),
    ACCETTA_ORDINE("UPDATE ordine SET tipo = '2' where id = ?"),
    MANDA_IN_CONSEGNA("UPDATE ordine SET tipo = '3' where id = ?"),
    ASSEGNA_FATTORINO("INSERT INTO consegna VALUES (?, ?);"),
    MODIFICA_ORARIO("UPDATE ordine SET oraconsegna = ?::time without time zone where id = ?"),
    COUNT_ORDINI("select count(id) as nordini from ordine where dataconsegna = ?::date and oraconsegna = ?::time without time zone"),
    GET_CLIENTE("SELECT C.id, C.nome, C.cognome, C.prefisso, C.telefono from cliente c join crea cc on c.id = cc.idcliente where cc.idordine = ?"),
    INSERISCI_CLIENTE("INSERT INTO cliente(id, cognome, nome, prefisso, telefono) VALUES (?, ?, ?,?::text, ?::text)"),
    AGGIORNA_ORDINE_ASPORTO("update ordine set consegna = 'false', tipo = '2', oraconsegna=?::time without time zone, dataconsegna=?::date, prefisso=?::text, telefono=?::text where id=?"),
    AGGIORNA_ORDINE_DOMICILIO("update ordine set consegna = 'true', tipo = '2', citta=?, oraconsegna=?::time without time zone, dataconsegna=?::date, via=?, prefisso=?::text, telefono=?::text where id=?"),
    ASSOCIA_ORDINE_CLIENTE("INSERT INTO crea(idordine, idcliente, nome, cognome, social) VALUES (?, ?, ?, ?, ?)"),
    CHECK_ORARIO("select count(id_colonna) as npizze from contiene join ordine on idordine = id where dataconsegna = ?::date and oraconsegna = ?::time without time zone"),
    MONITORA_ORDINE("SELECT O.consegna, O.prefisso, O.telefono, O.id, to_char(O.data, 'dd-mm-yyyy') as data, O.ora, O.totale, C.cognome, C.nome, O.tipo, to_char(O.dataconsegna, 'dd-mm-yyyy') as dataconsegna, O.oraconsegna, O.citta, O.via, C.social from ordine o join crea c on o.id = c.idordine where O.dataconsegna = ?::date and O.tipo <> 3 order by O.dataconsegna desc, O.oraconsegna desc"),
    MONITORA_ORDINI_ARCHIVIO("SELECT O.consegna, O.prefisso, O.telefono, O.id, to_char(O.data, 'dd-mm-yyyy') as data, O.ora, O.totale, C.cognome, C.nome, O.tipo, to_char(O.dataconsegna, 'dd-mm-yyyy') as dataconsegna, O.oraconsegna, O.citta, O.via, C.social from ordine o join crea c on o.id = c.idordine where O.dataconsegna is not null order by O.dataconsegna desc, O.oraconsegna desc"),
    CREA_ORDINE("INSERT INTO ordine(id, totale, data, ora, tipo, npizze) values (default,'0.1', default, default, default, default) returning id;"),
    GET_LISTA_PIZZE("SELECT nome,prezzo FROM prodotto where tipo = 'Pizza' order by nome"),
    GET_LISTA_INGREDIENTI("SELECT C.nomeingrediente, P.prezzo FROM composto C JOIN Prodotto P ON C.nomeprodotto = P.nome WHERE nomeprodotto = ? order by C.nomeingrediente not ilike 'pomodoro', C.nomeingrediente not ilike 'mozzarella', C.nomeingrediente;"),
    GET_AGGIUNTE("SELECT nomeingrediente, prezzo FROM ingrediente ORDER BY nomeingrediente;"),
    TOGLI_PIZZA_FROM_ORDINE("DELETE FROM contiene WHERE id_colonna = ?"),
    AGGIUNGI_PRODOTTO_TO_ORDINE("insert into contiene(idordine, nomeprodotto, prezzoprodotto) select ordine.id,  prodotto.nome, prodotto.prezzo from ordine, prodotto where prodotto.nome = ? AND id = ? GROUP BY ordine.id, prodotto.nome returning contiene.id_colonna"),
    AGGIORNA_TOTALE_ORDINE("UPDATE ordine SET totale = (SELECT SUM(C.prezzoprodotto) FROM contiene C JOIN ordine O ON C.idordine = O.id WHERE O.id = ?) WHERE id = ? returning totale"),
    GET_TOTALE_ORDINE("SELECT totale FROM ordine WHERE id = ?;"),
    GET_CITTA("SELECT nome FROM citta ORDER BY nome"),
    GET_PIZZA_IN_ORDINE("SELECT C.id_colonna, C.nomeprodotto, C.prezzoprodotto FROM contiene C WHERE C.idordine = ?"),
    GET_UTENTE("SELECT * FROM Cliente WHERE id = ?::text"),
    GET_PREZZO_PIZZA("SELECT prezzoprodotto from contiene where id_colonna = ?"),
    LISTA_EXTRA_AGGIUNTI("SELECT E.nomeextra FROM extra E WHERE E.idcolonnaextra = ? AND E.tipo = '1'"),
    LISTA_EXTRA_TOLTI("SELECT E.nomeextra FROM extra E WHERE E.idcolonnaextra = ? AND E.tipo = '2'"),
    RIMUOVI_TUTTI_EXTRA("DELETE FROM extra WHERE idcolonnaextra = ?"),
    AGGIUNGI_EXTRA("insert into extra(nomeextra, idcolonnaextra, prezzo, tipo) values (?, ?, (select ingrediente.prezzo from ingrediente where ingrediente.nomeingrediente ilike ?), ?)"),
    TOGLI_EXTRA("DELETE FROM extra WHERE idextra = (SELECT MIN(idextra) from extra WHERE idcolonnaextra = ? AND nomeextra = ?);"),
    GET_LISTA_INGREDIENTI_ED_EXTRA("SELECT C.nomeingrediente FROM composto C JOIN Prodotto P ON C.nomeprodotto = P.nome JOIN contiene CC ON CC.nomeprodotto = P.nome WHERE CC.id_colonna = ? and c.nomeingrediente not in (select nomeextra from extra where idcolonnaextra = ? and tipo = '2') union all select E.nomeextra from extra E join composto c on e.nomeextra = c.nomeingrediente WHERE E.idcolonnaextra = ? and E.tipo = '1'"),
    ADD_TOTALE_PIZZA_CON_EXTRA("UPDATE contiene SET prezzoprodotto = prezzoprodotto + (SELECT E.prezzo FROM Extra E JOIN Contiene C ON C.id_colonna = E.idcolonnaextra WHERE C.id_colonna = ? AND E.tipo = '1' AND E.idextra = (SELECT MAX(idextra) FROM Extra)) WHERE id_colonna = ? returning prezzoprodotto"),
    REMOVE_TOTALE_PIZZA_CON_EXTRA("UPDATE contiene SET prezzoprodotto = prezzoprodotto - (SELECT E.prezzo FROM Extra E JOIN Contiene C ON C.id_colonna = E.idcolonnaextra WHERE C.id_colonna = ? AND E.nomeextra = ? AND E.idextra = (SELECT MAX(idextra) FROM Extra)) WHERE id_colonna = ?"),
    GET_FATTURATO("select sum(totale) from ordine where dataconsegna > '2017-03-01' and dataconsegna < '2017-03-31'"),
    GET_ELENCO_FATTORINI("SELECT idfattorino, nome || ' ' || cognome AS nomecompleto FROM fattorino;");
    private String qVal;

    private EnumQuery(String qVal) {
        this.qVal = qVal;
    }

    public String getValore() {
        return this.qVal;
    }

}
