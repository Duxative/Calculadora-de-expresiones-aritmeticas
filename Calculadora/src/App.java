import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        String expresion;

        while (true) {
            System.out.println("Escribe una expresión en notación infija (o 'quit' para salir):");
            System.out.print("> ");
            expresion = teclado.nextLine();

            if (expresion.equalsIgnoreCase("quit")) {
                break;
            }

            List<String> tokens = obtenerTokens(expresion);
            System.out.println("Tokens (infija): " + listaToString(tokens));

            List<String> postfija = convertirAPostfija(tokens);
            System.out.println("Tokens (postfija): " + listaToString(postfija));

            try {
                double resultado = evaluarPostfija(postfija);
                System.out.println("Resultado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        teclado.close();
    }

    public static boolean esOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    public static String listaToString(List<String> lista) {
        StringBuilder resultado = new StringBuilder();
        for (String token : lista) {
            resultado.append(token).append(" ");
        }
        return resultado.toString().trim();
    }

    public static ArrayList<String> convertirAPostfija(List<String> tokens) {
        Stack<String> pila = new Stack<>();
        ArrayList<String> salida = new ArrayList<>();

        for (String token : tokens) {
            if (token.equals("(")) {
                pila.push(token);
            } else if (token.equals(")")) {
                while (!pila.isEmpty() && !pila.peek().equals("(")) {
                    salida.add(pila.pop());
                }
                pila.pop();
            } else if (esOperando(token)) {
                salida.add(token);
            } else if (esOperador(token)) {
                while (!pila.isEmpty() && obtenerPrecedencia(pila.peek()) >= obtenerPrecedencia(token)) {
                    salida.add(pila.pop());
                }
                pila.push(token);
            }
        }

        while (!pila.isEmpty()) {
            salida.add(pila.pop());
        }

        return salida;
    }

    public static boolean esOperando(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<String> obtenerTokens(String expresion) {
        StringTokenizer tokenizer = new StringTokenizer(expresion, " ()+-*/^", true);
        ArrayList<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.trim().isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static int obtenerPrecedencia(String token) {
        switch (token) {
            case "^": return 3;
            case "*": case "/": return 2;
            case "+": case "-": return 1;
            default: return 0;
        }
    }

    public static double evaluarPostfija(List<String> postfija) throws Exception {
        Stack<Double> pila = new Stack<>();

        for (String token : postfija) {
            if (esOperando(token)) {
                pila.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                if (pila.size() < 2) {
                    throw new Exception("Expresión inválida");
                }
                double operando2 = pila.pop();
                double operando1 = pila.pop();
                double resultado = 0;

                switch (token) {
                    case "+": resultado = operando1 + operando2; break;
                    case "-": resultado = operando1 - operando2; break;
                    case "*": resultado = operando1 * operando2; break;
                    case "/":
                        if (operando2 == 0) {
                            throw new ArithmeticException("División por cero");
                        }
                        resultado = operando1 / operando2;
                        break;
                    case "^": resultado = Math.pow(operando1, operando2); break;
                }

                pila.push(resultado);
            }
        }

        if (pila.size() != 1) {
            throw new Exception("Expresión inválida");
        }

        return pila.pop();
    }
}