package main.java;

/**
 * Classe principal do programa HexaWarden.
 * Responsável pela inicialização e utilitários gerais.
 */
public class Main {
    private static final String PROGRAM_NAME = "HexaWarden";
    
   private static final boolean DEBUG_MODE = 
        "true".equals(System.getenv("DEBUG_MODE")) || 
        "true".equals(System.getProperty("debug.mode"));

    /**
     * Retorna o nome do programa.
     */
    public static String GetProgramName() {
        return PROGRAM_NAME;
    }

    public static void main(String[] args) {
        print("Iniciando HexaWarden...");
        InterfaceUI.CreateInterface();   // Seguindo convenção camelCase
        database.StartDataBase();        // Seguindo convenção camelCase
    }

    /**
     * Utilitário para impressão no console de erro.
     */
    public static void print(Object value) {
        if (DEBUG_MODE) {
        System.err.println(String.valueOf(value));}
    }
}