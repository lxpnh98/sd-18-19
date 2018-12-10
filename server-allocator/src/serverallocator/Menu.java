package serverallocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Menu {

    private static final String RESET = "\u001B[0m";
    private static final String CLEAR = "\u001b[2J\u001b[H";
    private int state;

    private float balance;
    private ArrayList<String> data;

    public void show() {

        switch (state) {

            case 0:
                System.out.println(CLEAR+"*** Menu Inicial ***\n" +
                        "* 1 - Iniciar sessão                *\n" +
                        "* 2 - Efetuar registo               *\n" +
                        "* 3 - Sair                          *\n" +
                        "*************************************\n"+RESET);
                break;

            case 1:
                System.out.println(CLEAR+"*** WELCOME ***\n" +
                        "* 1 - Consultar conta corrente      *\n" +
                        "* 2 - Reservar servidor             *\n" +
                        "* 3 - Reservar instância            *\n" +
                        "* 0 - Logout                        *\n" +
                        "*************************************\n"+RESET);
                break;

            case 2:
                System.out.println(CLEAR+"*** WELCOME ***\n" +
                        "* 1 - Balance: " + this.balance + " *\n" +
                        "* 0 - Logout                        *\n" +
                        "*************************************\n"+RESET);
                break;

            case 3:
                System.out.println(CLEAR+"*** SERVIDORES FIXOS ***");

                for (int i = 0; i < data.size(); i+=2) {

                    System.out.println("* " + data.get(i) + " " + data.get(i+1) + " *");
                }

                System.out.println("* 0 - Voltar *");
                System.out.println("*************************************" + RESET);

                break;

            case 4:
                System.out.println(CLEAR+"*** SERVIDORES PARA ALUGAR ***");

                for (int i = 0; i < data.size(); i+=2) {

                    System.out.println("* " + data.get(i) + " " + data.get(i+1) + " *");
                }

                System.out.println("* 0 - Voltar *");
                System.out.println("*************************************" + RESET);

                break;

        }

        System.out.println("Escolha uma opção: ");
    }

    // método para ler strings da consola escritas pelo utilizador
    public String readStringFromUser(String question) {

        Scanner input = new Scanner(System.in);

        System.out.println(question);

        return input.nextLine();
    }

    // retorna input da escolha do utilizador
    public int userChoice() {

        int readInt;
        Scanner input = new Scanner(System.in);

        try {

            readInt = Integer.parseInt(input.nextLine());

        } catch (NumberFormatException e) {

            readInt =-1;
        }

        return readInt;
    }

    public int getState() {

        return this.state;
    }

    public void changeState(int newState) {

        this.state = newState;
    }

    public void changeBalance(float newBalance) {

        this.balance = newBalance;
    }

    public void changeArray(ArrayList<String> data) {

        this.data = data;

    }

}
