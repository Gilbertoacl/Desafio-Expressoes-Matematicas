package com.gilberto.amorim;

import java.util.*;

public class Main {

    public static final String MSG_INICIAL = "Digite uma expressão matemática ou digite 'sair' para encerrar o programa: ";
    public static final String MSG_ERRO = "Expressão inválida. Tente novamente.";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(MSG_INICIAL);
            String expressao = scanner.nextLine();

            if (expressao.equalsIgnoreCase("sair")) {
                break;
            }

            if(expressao.isEmpty()) {
                System.out.println(MSG_ERRO);
                continue;
            }

            processarExpressao(expressao);
        }
        scanner.close();
    }

    public static void processarExpressao(String expressao) {
        List<String> caracteres = separaCaracteres(expressao);
        LinkedList<String> caracterList = new LinkedList<>(caracteres);
        StringBuilder passos = new StringBuilder();

        while (caracterList.size() > 1) {
            int[] indicesOperacao = encontrarOperacao(caracterList);
            if (indicesOperacao == null) break;

            calcularExpressao(caracterList, indicesOperacao[0], indicesOperacao[1]);
            passos.append(String.join("", caracterList)).append("\n");
        }

        System.out.println("Expressão: \n" + expressao);
        System.out.println(passos.toString().trim());
    }

    private static int[] encontrarOperacao(LinkedList<String> caracterList) {
        int inicio = -1, fim = -1;

        for (int i = 0; i < caracterList.size(); i++) {
            if (caracterList.get(i).equals(")")) {
                int j = i - 1;
                while (j >= 0 && !caracterList.get(j).equals("(")) {
                    j--;
                }
                if (j >= 0) {
                    return new int[]{j, i};
                }
            }
        }

        int maiorPrecedencia = -1;
        int indiceOperador = -1;

        for (int i = 0; i < caracterList.size(); i++) {
            String token = caracterList.get(i);
            if (token.length() == 1 && ehOperador(token.charAt(0))) {
                int precedencia = definirPrecedencia(token.charAt(0));
                if (precedencia > maiorPrecedencia) {
                    maiorPrecedencia = precedencia;
                    indiceOperador = i;
                }
            }
        }

        return (indiceOperador != -1) ? new int[]{indiceOperador - 1, indiceOperador + 1} : null;
    }

    private static void calcularExpressao(LinkedList<String> caracterList, int inicio, int fim) {
        int operadorIndex = -1;
        int maiorPrecedencia = -1;

        for (int i = inicio; i <= fim; i++) {
            String token = caracterList.get(i);
            if (token.length() == 1 && ehOperador(token.charAt(0))) {
                int precedencia = definirPrecedencia(token.charAt(0));
                if (precedencia > maiorPrecedencia) {
                    maiorPrecedencia = precedencia;
                    operadorIndex = i;
                }
            }
        }

        if (operadorIndex == -1) {
            return;
        }

        double operandoA = Double.parseDouble(caracterList.get(operadorIndex - 1));
        double operandoB = Double.parseDouble(caracterList.get(operadorIndex + 1));
        char operador = caracterList.get(operadorIndex).charAt(0);
        double resultado = realizarCalculo(operador, operandoA, operandoB);
        String resultadoStr = (resultado % 1 == 0) ? String.valueOf((int) resultado) : String.valueOf(resultado);

        for (int i = 0; i <= (fim - inicio); i++) {
            caracterList.remove(inicio);
        }

        caracterList.add(inicio, resultadoStr);
    }

    private static double realizarCalculo(char operador, double a, double b) {
        switch (operador) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                return 0;
        }
    }

    public static List<String> separaCaracteres(String expressao) {
        List<String> caracteres = new ArrayList<>();
        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);
            if (Character.isDigit(c)) {
                StringBuilder numero = new StringBuilder();
                while (i < expressao.length() && Character.isDigit(expressao.charAt(i))) {
                    numero.append(expressao.charAt(i));
                    i++;
                }
                i--;
                caracteres.add(numero.toString());
            } else {
                caracteres.add(String.valueOf(c));
            }
        }
        return caracteres;
    }

    public static boolean ehOperador(char caracter) {
        return caracter == '+' || caracter == '-' || caracter == '*' || caracter == '/' || caracter == '^';
    }

    public static int definirPrecedencia(char operador) {
        switch (operador) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }
}
