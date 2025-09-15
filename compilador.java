import java.io.*;
import java.util.*;

public class Analizador {
    // --- CONSTANTES ---
    static final int IDENTIFICADOR = 256;
    static final int NUMERO = 257;
    static final int CUADRADO = 258;
    static final int CIRCULO = 259;
    static final int TRIANGULO = 260;
    static final int RECTANGULO = 261;
    static final int LIENZO = 262;
    static final int COLOR = 263;
    static final int PUNTOYCOMA = ';';
    static final int PARENTESIS_INI = '(';
    static final int PARENTESIS_FIN = ')';
    static final int COMA = ',';

    static BufferedReader entrada;
    static PrintWriter salida;
    static BufferedReader tokens;

    static String lexema = "";
    static String colorFigura = "";
    static int tokenActual;

    static PrintWriter figuras; // archivo donde se guarda la representación intermedia

    // Variables auxiliares para recolectar info de cada figura
    static String tipoFiguraActual = "";
    static String nombreFigura = "";
    static int[] parametros = new int[10]; // hasta 10 parámetros por figura
    static int cantidad_parametros;

    // ------------------ FASE LÉXICA ------------------

    static int palabras_reservadas() {
        if (lexema.equals("cuadrado")) return CUADRADO;
        if (lexema.equals("circulo")) return CIRCULO;
        if (lexema.equals("triangulo")) return TRIANGULO;
        if (lexema.equals("rectangulo")) return RECTANGULO;
        if (lexema.equals("lienzo")) return LIENZO;
        return -1;
    }

    static int es_color_reservado() {
        String[] colores = {"rojo", "verde", "azul", "amarillo", "negro", "blanco", "gris", "marron", "celeste", "violeta", "naranja"};
        for (String color : colores) {
            if (lexema.equals(color)) return COLOR;
        }
        return -1;
    }

    static int obtener_token_lexico() throws IOException {
        int c;
        do {
            c = entrada.read();
        } while (Character.isWhitespace(c));
        if (c == -1) return -1;

        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char) c);
                c = entrada.read();
            } while (Character.isLetterOrDigit(c) || c == '_');
            lexema = sb.toString();
            if (c != -1) entrada.unread(c);

            int palabra = palabras_reservadas();
            if (palabra != -1) return palabra;

            int color_token = es_color_reservado();
            if (color_token != -1) return color_token;

            return IDENTIFICADOR;
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char) c);
                c = entrada.read();
            } while (Character.isDigit(c));
            lexema = sb.toString();
            if (c != -1) entrada.unread(c);
            return NUMERO;
        }

        lexema = String.valueOf((char) c);
        return c;
    }

    static void guardar_token(int token) {
        switch (token) {
            case IDENTIFICADOR:
                salida.println("IDENTIFICADOR " + lexema);
                break;
            case NUMERO:
                salida.println("NUMERO " + lexema);
                break;
            case CUADRADO:
                salida.println("CUADRADO " + lexema);
                break;
            case CIRCULO:
                salida.println("CIRCULO " + lexema);
                break;
            case TRIANGULO:
                salida.println("TRIANGULO " + lexema);
                break;
            case RECTANGULO:
                salida.println("RECTANGULO " + lexema);
                break;
            case LIENZO:
                salida.println("LIENZO " + lexema);
                break;
            case COLOR:
                salida.println("COLOR " + lexema);
                break;
            case PARENTESIS_INI:
                salida.println("PARENTESIS_INI " + lexema);
                break;
            case PARENTESIS_FIN:
                salida.println("PARENTESIS_FIN " + lexema);
                break;
            case COMA:
                salida.println("COMA " + lexema);
                break;
            case PUNTOYCOMA:
                salida.println("PUNTOYCOMA " + lexema);
                break;
            default:
                salida.println("DESCONOCIDO " + lexema);
                break;
        }
    }

    static void fase_lexica() throws IOException {
        entrada = new BufferedReader(new FileReader("entrada1.txt"));
        salida = new PrintWriter(new FileWriter("tokens.txt"));

        int token;
        while ((token = obtener_token_lexico()) != -1) {
            guardar_token(token);
        }

        entrada.close();
        salida.close();
        System.out.println("Fase lexica completada. Tokens guardados en tokens.txt");
    }

    // ------------------ FASE SINTÁCTICA ------------------

    static int leer_token_parser() throws IOException {
        String tipo;
        if ((tipo = tokens.readLine()) == null) return -1;

        String[] parts = tipo.split(" ");
        lexema = parts[1];

        switch (parts[0]) {
            case "IDENTIFICADOR": return IDENTIFICADOR;
            case "NUMERO": return NUMERO;
            case "CUADRADO": return CUADRADO;
            case "CIRCULO": return CIRCULO;
            case "TRIANGULO": return TRIANGULO;
            case "RECTANGULO": return RECTANGULO;
            case "LIENZO": return LIENZO;
            case "COLOR": return COLOR;
            case "PARENTESIS_INI": return PARENTESIS_INI;
            case "PARENTESIS_FIN": return PARENTESIS_FIN;
            case "COMA": return COMA;
            case "PUNTOYCOMA": return PUNTOYCOMA;
            default: return -1;
        }
    }

    static void error(String msg) {
        System.out.println("Error de sintaxis: " + msg + ". Token actual: [" + lexema + "]");
        System.exit(1);
    }

    static void avanzar() throws IOException {
        tokenActual = leer_token_parser();
    }

    static void match(int esperado) throws IOException {
        if (tokenActual == esperado) avanzar();
        else error("Token inesperado");
    }

    static void parseInstruccion() throws IOException {
        if (tokenActual == LIENZO) {
            parseLienzoDecl();
        } else if (tokenActual == CUADRADO || tokenActual == CIRCULO ||
                tokenActual == TRIANGULO || tokenActual == RECTANGULO) {
            parseFiguraDecl();
        } else {
            error("Se esperaba 'lienzo' o tipo de figura");
        }
    }

    static void parseLienzoDecl() throws IOException {
        match(LIENZO);
        match(PARENTESIS_INI);

        String ancho = "";
        String alto = "";
        if (tokenActual != NUMERO) error("Se esperaba un numero (ancho)");
        ancho = lexema;
        match(NUMERO);

        match(COMA);

        if (tokenActual != NUMERO) error("Se esperaba un numero (alto)");
        alto = lexema;
        match(NUMERO);

        figuras.println("LIENZO " + ancho + " " + alto);

        match(PARENTESIS_FIN);
        match(PUNTOYCOMA);
    }

    static void parseFiguraDecl() throws IOException {
        if (tokenActual == CUADRADO) tipoFiguraActual = "CUADRADO";
        else if (tokenActual == CIRCULO) tipoFiguraActual = "CIRCULO";
        else if (tokenActual == TRIANGULO) tipoFiguraActual = "TRIANGULO";
        else if (tokenActual == RECTANGULO) tipoFiguraActual = "RECTANGULO";
        else error("Tipo de figura desconocido");

        parseTipoFigura(); // avanzar token

        nombreFigura = lexema;
        match(IDENTIFICADOR);

        match(PARENTESIS_INI);

        cantidad_parametros = 0;

        if (tokenActual != NUMERO) {
            error("Se esperaba al menos un número como parámetro");
        }

        parametros[cantidad_parametros++] = Integer.parseInt(lexema);
        match(NUMERO);

        while (tokenActual == COMA) {
            match(COMA);

            if (tokenActual == NUMERO) {
                parametros[cantidad_parametros++] = Integer.parseInt(lexema);
                match(NUMERO);
            } else if (tokenActual == COLOR || tokenActual == IDENTIFICADOR) {
                break;
            } else {
                error("Se esperaba un número o un color después de la coma");
            }
        }

        match(COMA);

        if (tokenActual != COLOR && tokenActual != IDENTIFICADOR) {
            error("Se esperaba un color como último parámetro");
        }

        colorFigura = lexema;
        avanzar();

        match(PARENTESIS_FIN);
        match(PUNTOYCOMA);

        // Guardar la figura
        figuras.print(tipoFiguraActual + " " + nombreFigura);
        for (int i = 0; i < cantidad_parametros; i++) {
            figuras.print(" " + parametros[i]);
        }
        figuras.println(" " + colorFigura);
    }

    static void parseTipoFigura() throws IOException {
        if (tokenActual == CUADRADO || tokenActual == CIRCULO ||
                tokenActual == TRIANGULO || tokenActual == RECTANGULO) {
            avanzar();
        } else {
            error("Tipo de figura no reconocido");
        }
    }

    static void parseParametros() throws IOException {
        match(NUMERO);
        while (tokenActual == COMA) {
            match(COMA);
            match(NUMERO);
        }
    }

    static void fase_sintactica() throws IOException {
        tokens = new BufferedReader(new FileReader("tokens.txt"));
        figuras = new PrintWriter(new FileWriter("figuras.txt"));

        parsePrograma();

        tokens.close();
        figuras.close();
        System.out.println("Análisis sintáctico completado correctamente. Figuras guardadas en figuras.txt");
    }

    static void parsePrograma() throws IOException {
        avanzar();
        while (tokenActual != -1) {
            parseInstruccion();
        }
    }

    // ------------------ MAIN ------------------

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("--lex")) {
            fase_lexica();
        } else if (args.length == 1 && args[0].equals("--parse")) {
            fase_sintactica();
        } else {
            fase_lexica();
            fase_sintactica();
        }
    }
}

