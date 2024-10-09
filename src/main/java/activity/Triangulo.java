package activity;



import java.util.Scanner;

public class Triangulo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Leitura dos lados do triângulo
        System.out.print("Digite o primeiro lado: ");
        int lado1 = scanner.nextInt();

        System.out.print("Digite o segundo lado: ");
        int lado2 = scanner.nextInt();

        System.out.print("Digite o terceiro lado: ");
        int lado3 = scanner.nextInt();

        // Verifica se os lados formam um triângulo válido
        if (ehTrianguloValido(lado1, lado2, lado3)) {
            // Verifica o tipo de triângulo
            if (lado1 == lado2 && lado2 == lado3) {
                System.out.println("O triângulo é Equilátero.");
            } else if (lado1 == lado2 || lado2 == lado3 || lado1 == lado3) {
                System.out.println("O triângulo é Isósceles.");
            } else {
                System.out.println("O triângulo é Escaleno.");
            }
        } else {
            System.out.println("Os valores fornecidos não formam um triângulo.");
        }

        scanner.close();
    }

    // Função para verificar se os lados formam um triângulo válido
    public static boolean ehTrianguloValido(int lado1, int lado2, int lado3) {
        return (lado1 + lado2 > lado3) && (lado1 + lado3 >= lado2) && (lado2 + lado3 > lado1);
    }
}