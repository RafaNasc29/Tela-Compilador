package Compilador;

import Compilador.misc.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Compilador {
    public static List<ErrorMessage> errorMessageList = new ArrayList<>();
    public static GeraCodigo codigo;
    static List<Token> lista = new ArrayList<>();
    static Deque<SimboloCSD> tabelaSimbolos = new ArrayDeque<>();
    static int label;
    static int memoryPointer = 0;

    /* Testing functions */

    private static boolean isDigit(int r) {
        return r >= 48 && r <= 57;
    }

    private static boolean isCharacter(int r) {
        return (r >= 65 && r <= 90) || (r >= 97 && r <= 122);
    }

    private static boolean isColon(int r) {
        return r == 58;
    }

    private static boolean isArithmeticOp(int r) {
        return (r == 42) || (r == 43) || (r == 45);
    }

    private static boolean isRelationalOp(int r) {
        return (r == 33) || (r == 60) || (r == 61) || (r == 62);
    }

    private static boolean isPunctuation(int r) {
        return (r == 40) || (r == 41) || (r == 44) || (r == 46) || (r == 59);
    }

    /* Error handling functions*/

    private static void errorHandler(int line, String message) {
        errorMessageList.add(new ErrorMessage(line + 1, message));
        System.out.println("Linha " + (line + 1) + " " + message);
    }

    /* Lexical analyser functions */

    private static Container trataDigito(int r, LineNumberReader lr) throws IOException {
        StringBuilder number = new StringBuilder(String.valueOf((char) r));
        r = lr.read();
        while (isDigit(r)) {
            number.append((char) r);
            r = lr.read();
        }
        return new Container(new Token(number.toString(), "snumero"), r);
    }

    private static Container trataPalavra(int r, LineNumberReader lr) throws IOException {
        StringBuilder word = new StringBuilder(String.valueOf((char) r));
        Token token = new Token("", "");
        r = lr.read();
        while (isCharacter(r) || isDigit(r) || (r == 95)) {
            word.append((char) r);
            r = lr.read();
        }
        switch (word.toString()) {
            case "programa" -> {
                token.lexema = "programa";
                token.simbolo = "sprograma";
            }
            case "se" -> {
                token.lexema = "se";
                token.simbolo = "sse";
            }
            case "entao" -> {
                token.lexema = "entao";
                token.simbolo = "sentao";
            }
            case "senao" -> {
                token.lexema = "senao";
                token.simbolo = "ssenao";
            }
            case "enquanto" -> {
                token.lexema = "enquanto";
                token.simbolo = "senquanto";
            }
            case "faca" -> {
                token.lexema = "faca";
                token.simbolo = "sfaca";
            }
            case "fim" -> {
                token.lexema = "fim";
                token.simbolo = "sfim";
            }
            case "escreva" -> {
                token.lexema = "escreva";
                token.simbolo = "sescreva";
            }
            case "leia" -> {
                token.lexema = "leia";
                token.simbolo = "sleia";
            }
            case "var" -> {
                token.lexema = "var";
                token.simbolo = "svar";
            }
            case "inteiro" -> {
                token.lexema = "inteiro";
                token.simbolo = "sinteiro";
            }
            case "booleano" -> {
                token.lexema = "booleano";
                token.simbolo = "sbooleano";
            }
            case "verdadeiro" -> {
                token.lexema = "verdadeiro";
                token.simbolo = "sverdadeiro";
            }
            case "falso" -> {
                token.lexema = "falso";
                token.simbolo = "sfalso";
            }
            case "procedimento" -> {
                token.lexema = "procedimento";
                token.simbolo = "sprocedimento";
            }
            case "funcao" -> {
                token.lexema = "funcao";
                token.simbolo = "sfuncao";
            }
            case "div" -> {
                token.lexema = "div";
                token.simbolo = "sdiv";
            }
            case "e" -> {
                token.lexema = "e";
                token.simbolo = "se";
            }
            case "ou" -> {
                token.lexema = "ou";
                token.simbolo = "sou";
            }
            case "nao" -> {
                token.lexema = "nao";
                token.simbolo = "snao";
            }
            case "inicio" -> {
                token.lexema = "inicio";
                token.simbolo = "sinicio";
            }
            default -> {
                token.lexema = word.toString();
                token.simbolo = "sidentificador";
            }
        }
        return new Container(token, r);
    }

    private static Container trataAtribuicao(LineNumberReader lr) throws IOException {
        int r = lr.read();
        Token token = new Token("", "");
        if (r == 61) {
            token.lexema = ":=";
            token.simbolo = "satribuicao";
            r = lr.read();
        } else {
            token.lexema = ":";
            token.simbolo = "sdoispontos";
        }
        return new Container(token, r);
    }

    private static Container trataAritmetico(int r, LineNumberReader lr) throws IOException {
        Token token = new Token("", "");
        switch (r) {
            case 42 -> {
                token.lexema = "*";
                token.simbolo = "smult";
            }
            case 43 -> {
                token.lexema = "+";
                token.simbolo = "smais";
            }
            case 45 -> {
                token.lexema = "-";
                token.simbolo = "smenos";
            }
        }
        r = lr.read();
        return new Container(token, r);
    }

    private static Container trataRelacional(int r, LineNumberReader lr) throws IOException {
        Token token = new Token("", "");
        if (r == 33) {
            int temp = r;
            r = lr.read();
            if (r == 61) {
                r = lr.read();
                token.lexema = "!=";
                token.simbolo = "sdif";
            } else {
                errorHandler(lr.getLineNumber(), "Carácter inválido " + '"' + (char) temp + '"');
            }
        } else if (r == 60) {
            r = lr.read();
            if (r == 61) {
                r = lr.read();
                token.lexema = "<=";
                token.simbolo = "smenorig";
            } else {
                token.lexema = "<";
                token.simbolo = "smenor";
            }
        } else if (r == 61) {
            r = lr.read();
            token.lexema = "=";
            token.simbolo = "sig";
        } else if (r == 62) {
            r = lr.read();
            if (r == 61) {
                r = lr.read();
                token.lexema = ">=";
                token.simbolo = "smaiorig";
            } else {
                token.lexema = ">";
                token.simbolo = "smaior";
            }
        }
        return new Container(token, r);
    }

    private static Container trataPontuacao(int r, LineNumberReader lr) throws IOException {
        Token token = new Token("", "");
        switch (r) {
            case 40 -> {
                token.lexema = "(";
                token.simbolo = "sabre_parenteses";
            }
            case 41 -> {
                token.lexema = ")";
                token.simbolo = "sfecha_parenteses";
            }
            case 44 -> {
                token.lexema = ",";
                token.simbolo = "svirgula";
            }
            case 46 -> {
                token.lexema = ".";
                token.simbolo = "sponto";
            }
            case 59 -> {
                token.lexema = ";";
                token.simbolo = "sponto_virgula";
            }
        }
        r = lr.read();
        return new Container(token, r);
    }

    private static Container pegaToken(int r, LineNumberReader lr) throws IOException {
        Container container = new Container(new Token("", ""), r);
        if (isDigit(r)) {
            container = trataDigito(r, lr);
        } else if (isCharacter(r)) {
            container = trataPalavra(r, lr);
        } else if (isColon(r)) {
            container = trataAtribuicao(lr);
        } else if (isArithmeticOp(r)) {
            container = trataAritmetico(r, lr);
        } else if (isRelationalOp(r)) {
            container = trataRelacional(r, lr);
        } else if (isPunctuation(r)) {
            container = trataPontuacao(r, lr);
        } else {
            errorHandler(lr.getLineNumber(), "Caráctere inválido " + '"' + (char) r + '"');
            container = pegaToken(container.read = lr.read(), lr);
        }
        return container;
    }

    public static Container analisadorLexical(int r, LineNumberReader lr) throws IOException {
        Container container = new Container(new Token("", ""), r);
        int curLine;
        while (r == 123 || r == 32 || r == 13 || r == 10 || r == 9) {
            if (r == 123) {
                curLine = lr.getLineNumber();
                while (r != 125 && r != -1) {
                    r = lr.read();
                }
                if (r == -1) {
                    errorHandler(curLine, "Comentário não fechado");
                }
                r = lr.read();
            }
            while (r == 32 || r == 13 || r == 9 || r == 10) {
                r = lr.read();
            }
        }
        if (r != -1) {
            container = pegaToken(r, lr);
        } else {
            container.read = r;
        }
        return container;
    }

    /* Tabela de simbolos CSD */

    public static void insereTabela(String nome, String tipo, String escopo, String memoria) {
        tabelaSimbolos.push(new SimboloCSD(nome, tipo, escopo, memoria));
    }

    public static SimboloCSD consultaTabela(String nome) {
        for (SimboloCSD csd : tabelaSimbolos) {
            if (csd.nome.equals(nome)) {
                return csd;
            }
        }
        return null;
    }


    public static void colocaTipoTabela(String tipo) {
        for (SimboloCSD csd : tabelaSimbolos) {
            if (csd.tipo.equals("variavel")) {
                csd.tipo = csd.tipo + "-" + tipo;
            } else break;
        }
    }

    private static boolean pesquisaDuplicVarTabela(String lexema) {
        for (SimboloCSD csd : tabelaSimbolos) {
            if (csd.nome.equals(lexema)) {
                return false;
            }
            if (csd.escopo.equals("L")) {
                return true;
            }
        }
        return true;
    }

    public static void desempilha() throws IOException {
        SimboloCSD aux = null;
        int endereco = 0;
        int contador = 0;
        for (SimboloCSD csd : tabelaSimbolos) {
            aux = tabelaSimbolos.pop();
            if (aux.escopo.equals("L")) break;

            if (aux.tipo.equals("variavel-booleano") || aux.tipo.equals("variavel-inteiro"))
                contador += 1;
            if (tabelaSimbolos.getFirst().escopo.equals("L")) {
                endereco = Integer.valueOf(csd.memoria);
            }
        }
        if (contador > 0)
            codigo.gera("", "DALLOC", String.valueOf(endereco), String.valueOf(contador));
        aux.escopo = "";
        tabelaSimbolos.push(aux);
    }

    public static void geraCodigoExpressao(List<Token> expressao) throws IOException {
        for (Token t : expressao) {
            switch (t.simbolo) {
                case "smais" -> codigo.gera("", "ADD", "", "");
                case "smenos" -> codigo.gera("", "SUB", "", "");
                case "sdiv" -> codigo.gera("", "DIVI", "", "");
                case "smult" -> codigo.gera("", "MULT", "", "");
                case "smaior" -> codigo.gera("", "CMA", "", "");
                case "smaiorig" -> codigo.gera("", "CMAQ", "", "");
                case "sig" -> codigo.gera("", "CEQ", "", "");
                case "smenor" -> codigo.gera("", "CME", "", "");
                case "smenorig" -> codigo.gera("", "CMEQ", "", "");
                case "sdif" -> codigo.gera("", "CDIF", "", "");
                case "se" -> codigo.gera("", "AND", "", "");
                case "sou" -> codigo.gera("", "OR", "", "");
                case "snao" -> codigo.gera("", "NEG", "", "");
                case "sverdadeiro" -> codigo.gera("", "LDC", "1", "");
                case "sfalso" -> codigo.gera("", "LDC", "0", "");
                case "sunario" -> {
                    if (t.lexema.equals("-u")) {
                        codigo.gera("", "INV", "", "");
                    }
                }
                default -> {
                    if (t.simbolo.equals("sidentificador")) {
                        SimboloCSD csd = consultaTabela(t.lexema);
                        if (csd != null) {
                            if (csd.tipo.equals("variavel-inteiro") || csd.tipo.equals("variavel-booleano")) {
                                codigo.gera("", "LDV", csd.memoria, "");
                            } else {
                                codigo.gera("", "CALL", csd.memoria, "");
                            }
                        }
                    } else {
                        codigo.gera("", "LDC", t.lexema, "");
                    }
                }
            }
        }
    }

    private static boolean analisaTipoSem(List<Token> expressao, String tipo, LineNumberReader lr) {
        Token ultimo = expressao.get(expressao.size() - 1);
        SimboloCSD csd = null;
        if (ultimo.simbolo.equals("sidentificador")) {
            csd = consultaTabela(ultimo.lexema);
        }

        if (tipo.equals("variavel-inteiro") || tipo.equals("funcao-inteiro")) {
            tipo = "inteiro";
        } else if (tipo.equals("variavel-booleano") || tipo.equals("funcao-booleano")) {
            tipo = "booleano";
        } else {
            errorHandler(lr.getLineNumber(), "Identificador invalido");
            return false;
        }

        if (tipo.equals("inteiro")) {
            if (ultimo.simbolo.equals("smais") || ultimo.simbolo.equals("smenos") || ultimo.simbolo.equals("sdiv") || ultimo.simbolo.equals("smult") ||
                    (csd != null && (csd.tipo.equals("funcao-inteiro") || csd.tipo.equals("variavel-inteiro")))) {
                return true;
            } else return false;
        } else {
            if (ultimo.simbolo.equals("smais") || ultimo.simbolo.equals("smenos") || ultimo.simbolo.equals("sdiv") || ultimo.simbolo.equals("smult") ||
                    (csd != null && (csd.tipo.equals("funcao-inteiro") || csd.tipo.equals("variavel-inteiro")))) {
                return false;
            } else
                return true;
        }
    }
    /* Syntax analyzer functions */

    public static Container analisaEtVariaveis(Container container, LineNumberReader lr) throws IOException {
        if (container.equals("svar")) {
            container.setToken(analisadorLexical(container.read, lr));
            if (container.token.simbolo.equals("sidentificador")) {
                while (container.token.simbolo.equals("sidentificador")) {
                    container = analisaVariaveis(container, lr);
                    if (container.token.simbolo.equals("sponto_virgula")) {
                        container.setToken(analisadorLexical(container.read, lr));
                    } else {
                        errorHandler(lr.getLineNumber(), "Esperando um ' ; '");
                    }
                }
            } else {
                errorHandler(lr.getLineNumber(), "Esperando identificador");
            }
        }
        return container;
    }

    private static Container analisaVariaveis(Container container, LineNumberReader lr) throws IOException {
        int contador = 0;
        int temp = 0;
        int flag = 1;
        while (!container.token.simbolo.equals("sdoispontos")) {
            if (container.equals("sidentificador")) {
                if (pesquisaDuplicVarTabela(container.token.lexema)) {

                    memoryPointer += 1;
                    if (flag == 1) {
                        temp = memoryPointer;
                        flag = 0;
                    }

                    insereTabela(container.token.lexema, "variavel", "", String.valueOf(memoryPointer));
                    contador += 1;
                    container.setToken(analisadorLexical(container.read, lr));
                    if (container.token.simbolo.equals("svirgula") || container.token.simbolo.equals("sdoispontos")) {
                        if (container.token.simbolo.equals("svirgula")) {
                            container.setToken(analisadorLexical(container.read, lr));
                            if (container.token.simbolo.equals("sdoispontos")) {
                                errorHandler(lr.getLineNumber(), "Esperando um identificador");
                            }
                        }
                    } else errorHandler(lr.getLineNumber(), "Esperando um ' , ' ou ' : '");
                } else {
                    errorHandler(lr.getLineNumber(), "Variavel já declarada");
                    break;
                }
            } else {
                errorHandler(lr.getLineNumber(), "Esperando um identificador");
                break;
            }
        }
        container.setToken(analisadorLexical(container.read, lr));
        container = analisaTipo(container, lr);

        codigo.gera("", "ALLOC", String.valueOf(temp), String.valueOf(contador));
        return container;
    }

    public static Container analisaTipo(Container container, LineNumberReader lr) throws IOException {

        if (!container.token.simbolo.equals("sinteiro") && !container.token.simbolo.equals("sbooleano")) {
            errorHandler(lr.getLineNumber(), "Tipo invalido");
        } else
            colocaTipoTabela(container.token.lexema);

        container.setToken(analisadorLexical(container.read, lr));

        return container;
    }


    public static Container analisaComandos(Container container, LineNumberReader lr) throws IOException {
        boolean flagError = true;

        if (container.token.simbolo.equals("sinicio")) {
            container.setToken(analisadorLexical(container.read, lr));
            container = analisaComandoSimples(container, lr);
            while (!container.equals("sfim")) {
                if (container.token.simbolo.equals("sponto_virgula")) {
                    container.setToken(analisadorLexical(container.read, lr));
                    if (!container.equals("sfim")) {
                        container = analisaComandoSimples(container, lr);
                    }
                } else {
                    if (flagError) {
                        flagError = false;
                        errorHandler(lr.getLineNumber(), "Esperando um ' ; '");
                    }
                    container.setToken(analisadorLexical(container.read, lr));
                }
            }
            container.setToken(analisadorLexical(container.read, lr));
        } else {
            errorHandler(lr.getLineNumber(), "Esperando Identificador Inicio");
        }

        return container;
    }

    public static Container analisaComandoSimples(Container container, LineNumberReader lr) throws IOException {

        if (container.token.simbolo.equals("sidentificador")) {
            container = analisaAtribChProcedimento(container, lr);
        } else if (container.equals("sse")) {
            container = analisaSe(container, lr);
        } else if (container.equals("senquanto")) {
            container = analisaEnquanto(container, lr);
        } else if (container.equals("sleia")) {
            container = analisaLeia(container, lr);
        } else if (container.equals("sescreva")) {
            container = analisaEscreva(container, lr);
        } else container = analisaComandos(container, lr);

        return container;
    }

    public static Container analisaAtribChProcedimento(Container container, LineNumberReader lr) throws IOException {
        Token b = container.token;
        container.setToken(analisadorLexical(container.read, lr));
        if (container.token.simbolo.equals("satribuicao")) {
            container = analisaAtribuicao(container.read, container.token, lr);

            var csd = consultaTabela(b.lexema);
            if (!analisaTipoSem(container.expressao, csd.tipo, lr)) {
                errorHandler(lr.getLineNumber(), "Tipo incompatível");
            }
            if (csd.tipo.equals("variavel-booleano") || csd.tipo.equals("variavel-inteiro"))
                codigo.gera("", "STR", csd.memoria, "");
            else if (csd.tipo.equals("funcao-booleano") || csd.tipo.equals("funcao-inteiro"))
                codigo.gera("", "STR", "0", "");
            container.expressao = new ArrayList<>();
        } else {
            SimboloCSD csd = consultaTabela(b.lexema);
            if (csd != null && csd.tipo.equals("procedimento")) {
                codigo.gera("", "CALL", csd.memoria, "");
            }
            container = analisaChamadaProcedimento(container, lr);
        }

        return container;
    }


    public static Container analisaAtribuicao(int r, Token token, LineNumberReader lr) throws IOException {
        Container container = new Container(token, r);

        container.setToken(analisadorLexical(container.read, lr));
        container = analisaExpressao(container, lr);

        container.expressao = infixToPostfix(container.expressao, lr);
        geraCodigoExpressao(container.expressao);

        return container;
    }

    public static Container analisaChamadaProcedimento(Container container, LineNumberReader lr) throws IOException {
        return container;
    }

    public static Container analisaChamadaFuncao(Container container, LineNumberReader lr) throws IOException {

        container.setToken(analisadorLexical(container.read, lr));


        return container;
    }


    public static Container analisaLeia(Container container, LineNumberReader lr) throws IOException {

        container.setToken(analisadorLexical(container.read, lr));

        if (container.token.simbolo.equals("sabre_parenteses")) {
            container.setToken(analisadorLexical(container.read, lr));
            if (container.token.simbolo.equals("sidentificador")) {
                if (pesquisadeClVarTabela(container.token.lexema)) {
                    SimboloCSD csd = consultaTabela(container.token.lexema);
                    if (csd != null && csd.tipo.equals("variavel-inteiro")) {
                        codigo.gera("", "RD", "", "");
                        codigo.gera("", "STR", csd.memoria, "");
                    }
                    else{
                        errorHandler(lr.getLineNumber(),"variavel " + container.token.lexema + " incompativel com comando leia");
                    }
                    container.setToken(analisadorLexical(container.read, lr));
                    if (container.token.simbolo.equals("sfecha_parenteses")) {
                        container.setToken(analisadorLexical(container.read, lr));
                    } else {
                        errorHandler(lr.getLineNumber(), "Esperando ' ) '");
                    }
                } else {
                    errorHandler(lr.getLineNumber(), "Variavel nao declarada");
                }
            } else {
                errorHandler(lr.getLineNumber(), "Esperando identificador");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando ' ( '");
        }

        return container;
    }

    private static boolean pesquisadeClVarTabela(String lexema) {
        var aux = tabelaSimbolos.stream().filter(t -> t.nome.equals(lexema)).findAny().orElse(null);
        if (aux == null) {
            return false;
        }
        return true;
    }

    private static boolean pesquisaDeclVarProcTabela(String lexema) {
        var aux = tabelaSimbolos.stream().filter(t -> t.nome.equals(lexema) && (t.tipo.equals("variavel-inteiro") || t.tipo.equals("variavel-booleano"))).findAny().orElse(null);

        if (aux == null) {
            return false;
        }

        return true;
    }

    public static Container analisaEscreva(Container container, LineNumberReader lr) throws IOException {

        container.setToken(analisadorLexical(container.read, lr));
        if (container.token.simbolo.equals("sabre_parenteses")) {
            container.setToken(analisadorLexical(container.read, lr));
            if (container.token.simbolo.equals("sidentificador")) {
                if (pesquisaDeclVarProcTabela(container.token.lexema)) {
                    SimboloCSD csd = consultaTabela(container.token.lexema);
                    if (csd != null && csd.tipo.equals("variavel-inteiro")) {
                        codigo.gera("", "LDV", csd.memoria, "");
                        codigo.gera("", "PRN", "", "");
                    }else{
                        errorHandler(lr.getLineNumber(),"variavel " + container.token.lexema + " incompativel com comando escreva");
                    }

                    container.setToken(analisadorLexical(container.read, lr));
                    if (container.token.simbolo.equals("sfecha_parenteses")) {
                        container.setToken(analisadorLexical(container.read, lr));
                    } else {
                        errorHandler(lr.getLineNumber(), "Esperando ' ) '");
                    }
                } else {
                    errorHandler(lr.getLineNumber(), "Não é uma variavel");
                }
            } else {
                errorHandler(lr.getLineNumber(), "Esperando identificador");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando ' ( '");
        }

        return container;
    }

    public static Container analisaEnquanto(Container container, LineNumberReader lr) throws IOException {
        int aux1, aux2;

        aux1 = label;
        codigo.gera("L" + label, "NULL", "", "");
        label += 1;

        container.setToken(analisadorLexical(container.read, lr));

        container = analisaExpressao(container, lr);
        container.expressao = infixToPostfix(container.expressao, lr);

        geraCodigoExpressao(container.expressao);

        if (analisaTipoSem(container.expressao, "variavel-booleano", lr)) {
            if (container.token.simbolo.equals("sfaca")) {

                aux2 = label;
                codigo.gera("", "JMPF", "L" + label, "");
                label += 1;

                container.setToken(analisadorLexical(container.read, lr));
                container = analisaComandoSimples(container, lr);

                codigo.gera("", "JMP", "L" + aux1, "");
                codigo.gera("L" + aux2, "NULL", "", "");
            } else {
                errorHandler(lr.getLineNumber(), "Esperando 'faca'");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Tipos Incompativeis");
        }

        return container;
    }

    public static Container analisaSe(Container container, LineNumberReader lr) throws IOException {
        container.setToken(analisadorLexical(container.read, lr));
        int auxrot1 = 0, auxrot2 = 0;
        container = analisaExpressao(container, lr);
        container.expressao = infixToPostfix(container.expressao, lr);

        geraCodigoExpressao(container.expressao);
        container.expressao = new ArrayList<>();
        if (container.token.simbolo.equals("sentao")) {
            codigo.gera("", "JMPF", "L" + label, "");
            auxrot1 = label;
            label += 1;

            container.setToken(analisadorLexical(container.read, lr));
            container = analisaComandoSimples(container, lr);
            if (container.token.simbolo.equals("ssenao")) {
                codigo.gera("", "JMP", "L" + label, "");
                auxrot2 = label;

                codigo.gera("L" + auxrot1, "NULL", "", "");

                container.setToken(analisadorLexical(container.read, lr));
                container = analisaComandoSimples(container, lr);
                codigo.gera("L" + auxrot2, "NULL", "", "");
            } else {
                codigo.gera("L" + auxrot1, "NULL", "", "");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando um 'entao'");
        }

        return container;
    }

    public static Container analisaExpressao(Container container, LineNumberReader lr) throws IOException {
        container = analisaExpressaoSimples(container, lr);
        if (container.token.simbolo.equals("smaior") || container.token.simbolo.equals("smaiorig") ||
                container.token.simbolo.equals("smenor") || container.token.simbolo.equals("smenorig")
                || container.token.simbolo.equals("sig") || container.token.simbolo.equals("sdif")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
            container = analisaExpressaoSimples(container, lr);
        }

        return container;
    }

    public static Container analisaExpressaoSimples(Container container, LineNumberReader lr) throws IOException {
        if (container.token.simbolo.equals("smais") || container.token.simbolo.equals("smenos")) {
            String unario;
            if (container.equals("smais")) {
                unario = "+u";
            } else
                unario = "-u";
            container.expressao.add(new Token(unario, "sunario"));

            /*container.setToken(analisadorLexical(container.read, lr));
            if (container.equals("snumero"))
                container.expressao.add(container.token);
            else{
                if(container.equals("sidentificador")){
                    SimboloCSD csd = consultaTabela(container.token.lexema);
                    if(csd != null && csd.tipo.equals("variavel-inteiro")){
                        container.expressao.add(container.token);
                    }else
                        errorHandler(lr.getLineNumber(), "Tipo incompativel");
                }else
                    errorHandler(lr.getLineNumber(), "Simbolo invalido");
            }*/
            container.setToken(analisadorLexical(container.read, lr));
        }
        container = analisaTermo(container, lr);
        while (container.token.simbolo.equals("smais") || container.token.simbolo.equals("smenos") ||
                container.token.simbolo.equals("sou")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
            container = analisaTermo(container, lr);
        }

        return container;
    }

    public static Container analisaTermo(Container container, LineNumberReader lr) throws IOException {
        container = analisaFator(container, lr);
        while (container.token.simbolo.equals("smult") || container.token.simbolo.equals("sdiv") ||
                container.token.simbolo.equals("se")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
            container = analisaFator(container, lr);
        }

        return container;
    }

    public static Container analisaFator(Container container, LineNumberReader lr) throws IOException {

        if (container.token.simbolo.equals("sidentificador")) {
            SimboloCSD csd = consultaTabela(container.token.lexema);
            if(csd != null){
                if(csd.tipo.equals("funcao-inteiro") || csd.tipo.equals("funcao-booleano")){
                    container.expressao.add(container.token);
                    analisaChamadaFuncao(container, lr);
                }
                else{
                    container.expressao.add(container.token);
                    container.setToken(analisadorLexical(container.read,lr));
                }
            }else
                errorHandler(lr.getLineNumber(), "Variavel " + container.token.lexema + " nao encontrado");
        } else if (container.token.simbolo.equals("snumero")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
        } else if (container.token.simbolo.equals("snao")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
            container = analisaFator(container, lr);
        } else if (container.token.simbolo.equals("sabre_parenteses")) {
            container.expressao.add(container.token);

            container.setToken(analisadorLexical(container.read, lr));
            container = analisaExpressao(container, lr);
            if (container.token.simbolo.equals("sfecha_parenteses")) {
                container.expressao.add(container.token);
                container.setToken(analisadorLexical(container.read, lr));
            } else {
                errorHandler(lr.getLineNumber(), "Esperando ' ) '");
            }
        } else if (container.token.lexema.equals("verdadeiro") || container.token.lexema.equals("falso")) {
            container.expressao.add(container.token);
            container.setToken(analisadorLexical(container.read, lr));
        } else {
            errorHandler(lr.getLineNumber(), "Lexema invalido");
            container.setToken(analisadorLexical(container.read,lr));
        }

        return container;
    }

    public static Container analisaSubrotina(Container container, LineNumberReader lr) throws IOException {

        int auxrot = 0, flag;
        flag = 0;
        if (container.equals("sprocedimento") || container.equals("sfuncao")) {
            auxrot = label;
            codigo.gera("", "JMP", "L" + label, "");
            label += 1;
            flag = 1;
        }

        while (container.token.simbolo.equals("sprocedimento") || container.token.simbolo.equals("sfuncao")) {
            if (container.token.simbolo.equals("sprocedimento")) {
                container = analisaDeclaracaoProcedimento(container.read, lr);
            } else {
                container = analisaDeclaracaoFuncao(container.read, lr);
            }
            if (container.token.simbolo.equals("sponto_virgula")) {
                container.setToken(analisadorLexical(container.read, lr));
            } else {
                errorHandler(lr.getLineNumber(), "Esperando um ' ; '");
            }
        }

        if (flag == 1) {
            codigo.gera("L" + auxrot, "NULL", "", "");
        }
        return container;
    }

    private static Container analisaDeclaracaoFuncao(int r, LineNumberReader lr) throws IOException {
        Container container = analisadorLexical(r, lr);
        if (container.token.simbolo.equals("sidentificador")) {
            if (!pesquisadeClVarTabela(container.token.lexema)) {
                insereTabela(container.token.lexema, "", "L", "L" + label);
                label += 1;
                container.setToken(analisadorLexical(container.read, lr));
                if (container.token.simbolo.equals("sdoispontos")) {
                    container.setToken(analisadorLexical(container.read, lr));
                    if (container.token.simbolo.equals("sinteiro") || container.token.simbolo.equals("sbooleano")) {
                        if (container.equals("sinteiro")) {
                            SimboloCSD aux = tabelaSimbolos.pop();
                            aux.tipo = "funcao-inteiro";
                            tabelaSimbolos.push(aux);
                        } else {
                            SimboloCSD aux = tabelaSimbolos.pop();
                            aux.tipo = "funcao-booleano";
                            tabelaSimbolos.push(aux);
                        }
                        codigo.gera(tabelaSimbolos.peek().memoria, "NULL", "", "");
                        container.setToken(analisadorLexical(container.read, lr));
                        if (container.token.simbolo.equals("sponto_virgula")) {
                            container = analisaBloco(container.read, lr);
                            desempilha();
                            codigo.gera("", "RETURNF", "", "");
                        }
                    } else {
                        errorHandler(lr.getLineNumber(), "Esperando declaração de tipo");
                    }
                } else {
                    errorHandler(lr.getLineNumber(), "Esperando um ' : '");
                }
            } else {
                errorHandler(lr.getLineNumber(), "Funcao ja declarada");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando identificador");
        }
        return container;
    }

    private static Container analisaDeclaracaoProcedimento(int r, LineNumberReader lr) throws IOException {
        Container container = analisadorLexical(r, lr);
        String nivel = "L";
        if (container.token.simbolo.equals("sidentificador")) {
            if (!pesquisadeClVarTabela(container.token.lexema)) {
                insereTabela(container.token.lexema, "procedimento", nivel, "L" + label);
                codigo.gera("L" + label, "NULL", "", "");
                label += 1;
                container.setToken(analisadorLexical(container.read, lr));
                if (container.token.simbolo.equals("sponto_virgula")) {
                    container = analisaBloco(container.read, lr);
                    desempilha();
                    codigo.gera("", "RETURN", "", "");
                } else {
                    errorHandler(lr.getLineNumber(), "Esperando um ' ; '");
                }
            } else {
                errorHandler(lr.getLineNumber(), "Procedimento ja declarado");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando identificador");
        }
        return container;
    }

    public static Container analisaBloco(int r, LineNumberReader lr) throws IOException {
        Container container = analisadorLexical(r, lr);
        container = analisaEtVariaveis(container, lr);//analisa_et_variaveis
        container = analisaSubrotina(container, lr);//analisa_subrotinas
        container = analisaComandos(container, lr);//analisa_comandos
        return container;
    }

    public static Container analisadorSintatico(LineNumberReader lr) throws IOException {
        int r = lr.read();
        label = 1;
        Container container = analisadorLexical(r, lr);
        if (container.token.simbolo.equals("sprograma")) {
            container.setToken(analisadorLexical(container.read, lr));
            if (container.token.simbolo.equals("sidentificador")) {
                insereTabela(container.token.lexema, "nomedeprograma", "", null);
                codigo.gera("", "START", "", "");
                container.setToken(analisadorLexical(container.read, lr));
                if (container.token.simbolo.equals("sponto_virgula")) {
                    container = analisaBloco(container.read, lr);
                    if (container.token.simbolo.equals("sponto")) {
                        int curLine = lr.getLineNumber();
                        container.setToken(analisadorLexical(container.read, lr));
                        if (container.read == -1 && container.token.lexema.isEmpty()) {
                            codigo.gera("", "HLT", "", "");
                        } else {
                            errorHandler(curLine, "Não é EOF ou comentário não fechado");
                        }
                    } else {
                        errorHandler(lr.getLineNumber(), "Esperando um ' . '");
                    }
                } else {
                    errorHandler(lr.getLineNumber(), "Esperando um ' ; '");
                }
            } else {
                errorHandler(lr.getLineNumber(), "Esperando identificador");
            }
        } else {
            errorHandler(lr.getLineNumber(), "Esperando identificador 'programa'");
        }
        return container;
    }

    public static void run(File file) throws IOException {
        lista = new ArrayList<>();
        errorMessageList = new ArrayList<>();
        tabelaSimbolos = new ArrayDeque<>();
        label = 0;
        memoryPointer = 0;

        LineNumberReader lr = new LineNumberReader(new FileReader(file.getAbsolutePath()));
        codigo = new GeraCodigo(file);
        Container container = analisadorSintatico(lr);
        codigo.closeBw();
        lr.close();


        /*File fileObj = new File("tests/gera1.txt.obj");
        lr = new LineNumberReader(new FileReader(fileObj));

        MaquinaVirtual maquinaVirtual = new MaquinaVirtual(lr);
        maquinaVirtual.loadPC();
        maquinaVirtual.execute();*/
    }

    static void testExpression(List<Token> expression, LineNumberReader lr) {
        SimboloCSD csd;
        boolean flag = false;
        String temp, aux1, aux2;
        Deque<String> stack = new ArrayDeque<>();
        Deque<String> auxStack = new ArrayDeque<>();

        for (int i = expression.size() - 1; i >= 0; i--) {
            switch (expression.get(i).simbolo) {
                case "sidentificador" -> {
                    csd = consultaTabela(expression.get(i).lexema);
                    if (csd != null) {
                        if (csd.tipo.equals("variavel-inteiro") || csd.tipo.equals("funcao-inteiro")) {
                            stack.push("inteiro");
                        } else if (csd.tipo.equals("variavel-booleano") || csd.tipo.equals("funcao-booleano")) {
                            stack.push("booleano");
                        }
                    }
                }
                case "smult", "sdiv", "smais", "smenos" -> stack.push("arithmeticOp");
                case "smaior", "smaiorig", "smenor", "smenorig", "sdif", "sigual" -> stack.push("relationalOp");
                case "se", "sou" -> stack.push("logicalOp");
                case "snao" -> stack.push("negOp");
                case "sunario" -> stack.push("unaryOp");
                case "snumero" -> stack.push("inteiro");
                case "sfalso", "sverdadeiro" -> stack.push("booleano");
                default -> stack.push(expression.get(i).lexema);
            }
        }
        while (!stack.isEmpty()) {
            temp = stack.pop();
            try {
                switch (temp) {
                    case "arithmeticOp" -> {
                        aux1 = auxStack.pop();
                        aux2 = auxStack.pop();
                        if (aux1.equals("inteiro") && aux2.equals("inteiro")) {
                            auxStack.push("inteiro");
                        } else flag = true;
                    }
                    case "relationalOp" -> {
                        aux1 = auxStack.pop();
                        aux2 = auxStack.pop();
                        if (aux1.equals("inteiro") && aux2.equals("inteiro")) {
                            auxStack.push("booleano");
                        } else flag = true;
                    }
                    case "logicalOp" -> {
                        aux1 = auxStack.pop();
                        aux2 = auxStack.pop();
                        if (aux1.equals("booleano") && aux2.equals("booleano")) {
                            auxStack.push("booleano");
                        } else flag = true;
                    }
                    case "unaryOp" -> {
                        aux1 = auxStack.pop();
                        if (aux1.equals("inteiro")) {
                            auxStack.push("inteiro");
                        } else flag = true;
                    }
                    case "negOp" -> {
                        aux1 = auxStack.pop();
                        if (aux1.equals("booleano")) {
                            auxStack.push("booleano");
                        } else flag = true;
                    }
                    default -> auxStack.push(temp);
                }
            }catch(Exception e){
                errorHandler(lr.getLineNumber(), "Expressao invalida");
            }
            if (flag) break;
        }
        if (flag) {
            errorHandler(lr.getLineNumber(), "Expressão com tipos incompatíveis.");
        }
    }

    static int Prec(Token token) {
        if (token.simbolo.equals("sunario")) {
            return 7;
        } else if (token.simbolo.equals("smult") || token.simbolo.equals("sdiv")) {
            return 6;
        } else if (token.simbolo.equals("smais") || token.simbolo.equals("smenos")) {
            return 5;
        } else if (token.simbolo.equals("smaior") || token.simbolo.equals("smaiorig") || token.simbolo.equals("smenor") ||
                token.simbolo.equals("smenorig") || token.simbolo.equals("sdif") || token.simbolo.equals("sigual")) {
            return 4;
        } else if (token.simbolo.equals("snao")) {
            return 3;
        } else if (token.simbolo.equals("se")) {
            return 2;
        } else if (token.simbolo.equals("sou")) {
            return 1;
        } else {
            return -1;
        }
    }

    public static List<Token> infixToPostfix(List<Token> exp, LineNumberReader lr) {
        // Initializing empty String for result
        List<Token> result = new ArrayList<>();

        // Initializing empty stack
        Deque<Token> stack
                = new ArrayDeque<Token>();

        for (Token t : exp) {

            // If the scanned character is an
            // operand, add it to output.
            if (t.simbolo.equals("sidentificador") || t.simbolo.equals("snumero") ||
                    t.simbolo.equals("sverdadeiro") || t.simbolo.equals("sfalso"))
                result.add(t);

                // If the scanned character is an '(',
                // push it to the stack.
            else if (t.lexema.equals("("))
                stack.push(t);

                // If the scanned character is an ')',
                // pop and output from the stack
                // until an '(' is encountered.
            else if (t.lexema.equals(")")) {
                while (!stack.isEmpty()
                        && !stack.peek().lexema.equals("(")) {
                    result.add(stack.peek());
                    stack.pop();
                }

                stack.pop();
            }

            // An operator is encountered
            else {
                while (!stack.isEmpty()
                        && Prec(t) <= Prec(stack.peek())) {

                    result.add(stack.peek());
                    stack.pop();
                }
                stack.push(t);
            }
        }

        // Pop all the operators from the stack
        while (!stack.isEmpty()) {
            if (stack.peek().lexema.equals("(")) {
                errorHandler(lr.getLineNumber(), "Expressão inválida");
            }
            result.add(stack.peek());
            stack.pop();
        }

        testExpression(result, lr);

        return result;
    }
}