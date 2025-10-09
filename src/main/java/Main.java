package main.java;

/**
 * Classe Main
 *
 * Esta é a classe principal do programa "ResourceGuard". 
 * Contém o método main que inicia a aplicação e alguns utilitários.
 */
public class Main {

    // Nome do programa, armazenado como variável privada 
    private static String ProgramName = "HexaWarden";

    /**
     * Retorna o nome do programa.
     *
     * @return Uma String com o nome do programa.
     */
    public static String GetProgramName() {
        return ProgramName;
    }

    /**
     * Método principal que inicia a execução do programa.
     *
     * @param args Argumentos passados pela linha de comando (não utilizados aqui).
     */
    public static void main(String[] args) {
        print("Hello, World!");          // imprime uma mensagem de teste
        InterfaceUI.CreateInterface();   // chama a criação da interface gráfica
        database.StartDataBase(); // chama a database
    }

    /**
     * Método utilitário para imprimir mensagens no console de erro.
     *
     * @param vlr A mensagem a ser exibida.
     */
    public static void print(String vlr) {
        System.err.println(vlr); // imprime no console de erro
    }
}
