# Desafio expressoes matematicas
Deve ser realizado um programa que resolva expressões matemáticas.

## Requisitos: 

- `Requisito 1`: O programa deve ser escrito em Java
- `Requisito 2`: O programa poderá resolver apenas uma expressão ou várias, em loop.
- `Requisito 3`: O programa deverá exibir o passo a passo para resolução da expressão, conforme o exemplo abaixo:

``` shell
((2+3)*5)^2
(5*5)^2
25^2
625
```


## Explicação do código

Este programa permite que o usuário insira uma expressão matemática simples, a qual será resolvida passo a passo, exibindo cada etapa do cálculo até chegar ao resultado final. O objetivo é simular a resolução manual de expressões, respeitando a precedência dos operadores matemáticos e o uso de parênteses.

A estrutura do código segue boas práticas de organização e responsabilidade única para cada método, tornando-o mais legível e de fácil manutenção.

### Separação da expressão em caracteres
Usando uma Lista para armazenar osa numeros e operadores separadamente e uma ``StringBuilder`` para capturar os números inteiros, com isso percorremos a String caractere por caractere, agrupando os números e armazenando operadores individualmente.

```java
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
```

### Processamento da expressão
Aqui apliquei as regras matemáticas para resolver a expressão passo a passo.
Usei uma ``LinkedList<String>`` para manipulação eficiente da expressão, removendo e substituindo valores conforme resolvemos as operações.
Chamo ``encontrarOperacao(listaTokens)`` para encontrar qual operação deve ser resolvida primeiro e ``calcularExpressao(listaTokens, inicio, fim)`` para resolver a operação e substituir o trecho resolvido pelo resultado.

```java
public static void processarExpressao(String expressao) {
    List<String> tokens = separarTokens(expressao);
    LinkedList<String> listaTokens = new LinkedList<>(tokens);
    StringBuilder passos = new StringBuilder();

    while (listaTokens.size() > 1) {
        int[] indicesOperacao = encontrarOperacao(listaTokens);
        if (indicesOperacao == null) break;

        calcularExpressao(listaTokens, indicesOperacao[0], indicesOperacao[1]);
        passos.append(String.join("", listaTokens)).append("\n");
    }

    System.out.println("Expressão: \n" + expressao);
    System.out.println(passos.toString().trim());
}
```

###  Encontrar a Operação a ser resolvida
Primeiro, verificamos se há parênteses poriorizando a resolução do que está dentro deles e se não houver parênteses, encontramos o operador de maior precedência.

```java
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

```

###  Resolvendo a operação encontrada
Este método pega dois operandos e um operador e resolve a operação, substituindo a parte da expressão já resolvida.
Converto os operandos para ``double`` para suportar operações com divisão.
Armazenando o resultado formatado como ``inteiro`` caso ele não tenha casas decimais.
Removendo os elementos da lista e substituindo pelo resultado.

```java
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


```