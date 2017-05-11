
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import org.apache.commons.math3.fraction.Fraction;
//import org.apache.commons.math3.fraction.FractionFormat;

public class SimplexPrimalDual {

    public static void simplex(String textoEntrada) throws IOException {
        textoEntrada = textoEntrada.replaceAll("(\\t|\\r?\\n)+", " ");
        String array[] = textoEntrada.split(" ");

        String tipo = array[0];
        int linhas = Integer.parseInt(array[1]);
        int colunas = Integer.parseInt(array[2]);

        double[][] A = new double[linhas][colunas];
        String[][] A2 = new String[linhas][colunas];

        A[0][0] = 0;
        int k = 3;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (i == 0 && j == 0) {
                    j = 1;

                }
                String valor = array[k++];
                A[i][j] = Double.parseDouble(valor);
                A2[i][j] = valor;
            }
        }
        
        FileWriter arq = new FileWriter("saida.txt");
        PrintWriter gravarArq = new PrintWriter(arq);
        
        //Imprime o modelo
        gravarArq.print("MODELO\n\n");
        gravarArq.print("\t"+tipo+": Z = ");
        for (int i = 1; i < colunas; i++) {
            if(!"0".equals(A2[0][i]))
                gravarArq.print(A2[0][i]+"x"+i+" ");
        }
        
        gravarArq.print("\nSujeito a:\n");
        
        for (int i = 1; i < linhas; i++) {
            gravarArq.print("\t");
            for (int j = 1; j < colunas; j++) {
                if(!"0".equals(A2[i][j]))
                    gravarArq.print(A2[i][j]+"x"+j+" ");
            }
            gravarArq.print("= "+A2[i][0]);
            gravarArq.println();
        }
        
        gravarArq.print("\t");
        for (int i = 1; i < colunas; i++) {
            if(i == (colunas - 1))
                gravarArq.print("x"+i+" >= 0 ");
            else
                gravarArq.print("x"+i+" >= 0, ");
        }
        gravarArq.println();
        
        gravarArq.print("\n");

        if ("min".equals(tipo)) {
            for (int j = 0; j < colunas; j++) {
                A[0][j] = -1 * A[0][j];
            }
        }

        int[] VB = new int[linhas - 1];
        int v_folga = 0;

        for (int j = 1; j < colunas; j++) {
            if (A[0][j] == 0) {
                v_folga = j;
                break;
            }
        }

        for (int i = 0; i < linhas - 1; i++) {
            VB[i] = v_folga + i;
        }

        // Impressão do quadro
        
        gravarArq.print("QUADRO INICIAL\n");
        gravarArq.print("\t\t\t");
        for (int j = 1; j < colunas; j++) {
            gravarArq.print("X" + j + "\t\t");
        }

        gravarArq.println();

        for (int i = 0; i < linhas; i++) {
            if (i != 0) {
                gravarArq.print("X" + VB[i - 1] + "\t");
            } else {
                gravarArq.print("Z \t");
            }
            for (int j = 0; j < colunas; j++) {
                gravarArq.printf("%.7f \t", A[i][j]);
            }
            gravarArq.println("");
        }

        boolean solucao_otima = false;
        boolean solucaoInviavel = true;
        boolean solucaoIlimitada = true;

        while (!solucao_otima) {

            solucao_otima = true;
            solucaoInviavel = true;
            solucaoIlimitada = true;

            for (int j = 1; j < colunas; j++) {
                if (A[0][j] > 0) {
                    solucao_otima = false;
                }
            }

            if (solucao_otima) {
                solucaoInviavel = false;
                solucaoIlimitada = false;
                break;
            }

            for (int i = 1; i < linhas; i++) {
                if (A[i][0] >= 0) {
                    solucaoInviavel = false;
                }
            }

            if (solucaoInviavel) {
                gravarArq.println("\nSolução Inviavel no primal");
                break;
            }

            double maior = Double.NEGATIVE_INFINITY;
            int coluna_pivo = 1;
            for (int j = 1; j < colunas; j++) {
                if (A[0][j] > maior) {
                    coluna_pivo = j;
                    maior = A[0][j];
                }
            }

            for (int i = 1; i < linhas; i++) {
                if (A[i][coluna_pivo] > 0) {
                    solucaoIlimitada = false;
                    break;
                }
            }

            if (solucaoIlimitada) {
                gravarArq.println("\nSolução Ilimitada");
                break;
            }

            double menor = Double.POSITIVE_INFINITY;
            int linha_pivo = 1;

            for (int i = 1; i < linhas; i++) {
                if (A[i][coluna_pivo] > 0 && A[i][0] >= 0) {
                    double razao = A[i][0] / A[i][coluna_pivo];
                    if (razao < menor) {
                        linha_pivo = i;
                        menor = razao;
                    }
                }
            }

            double elemento_pivo = A[linha_pivo][coluna_pivo];

            VB[linha_pivo - 1] = coluna_pivo;

            /*System.out.println("Linha pivo: " + linha_pivo);
            System.out.println("Coluna pivo: " + coluna_pivo);
            System.out.println("Elemento pivo: " + elemento_pivo);

            System.out.print("Variaveis da base: ");
            for (int i = 0; i < VB.length; i++) {
                System.out.print(VB[i] + " ");
            }
            System.out.println("");*/
            for (int j = 0; j < colunas; j++) {
                A[linha_pivo][j] = A[linha_pivo][j] / elemento_pivo;
            }

            double[] elem_coluna_pivo = new double[linhas];

            for (int i = 0; i < linhas; i++) {
                elem_coluna_pivo[i] = A[i][coluna_pivo];
            }

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    if (i != linha_pivo) {
                        A[i][j] = A[i][j] - elem_coluna_pivo[i] * A[linha_pivo][j];
                    }
                }
            }

            gravarArq.println();

            /*for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    System.out.printf("%.6f \t", A[i][j]);
                }
                System.out.println("");
            }*/
            // Impressão do quadro
            gravarArq.print("PIVOTEAMENTO DO PRIMAL ...\n");
            gravarArq.print("\t\t\t");
            for (int j = 1; j < colunas; j++) {
                gravarArq.print("X" + j + "\t\t");
            }

            gravarArq.println();

            for (int i = 0; i < linhas; i++) {
                if (i != 0) {
                    gravarArq.print("X" + VB[i - 1] + "\t");
                } else {
                    gravarArq.print("Z \t");
                }
                for (int j = 0; j < colunas; j++) {
                    gravarArq.printf("%.7f \t", A[i][j]);
                }
                gravarArq.println("");
            }

        }

        solucao_otima = false;

        while (!solucao_otima && !solucaoInviavel && !solucaoIlimitada) {

            solucao_otima = true;
            //solucaoInviavel = true;
            solucaoIlimitada = true;

            for (int j = 1; j < linhas; j++) {
                if (A[j][0] < 0) {
                    solucao_otima = false;
                }
            }

            if (solucao_otima) {
                break;
            }

            /*for (int i = 0; i < linhas; i++) {
                if(A[i][0] >= 0) 
                    solucaoInviavel = false;
            }
            
            if(solucaoInviavel) {
                System.out.println("Solução Inviavel no primal");
                break;
            }*/
            double menor = Double.POSITIVE_INFINITY;
            int linha_pivo = 1;
            for (int j = 1; j < linhas; j++) {
                if (A[j][0] < menor) {
                    linha_pivo = j;
                    menor = A[j][0];
                }
            }

            for (int i = 1; i < colunas; i++) {
                if (A[linha_pivo][i] < 0) {
                    solucaoIlimitada = false;
                    break;
                }
            }

            if (solucaoIlimitada) {
                gravarArq.println("\nSolução Ilimitada");
                break;
            }

            menor = Double.POSITIVE_INFINITY;
            int coluna_pivo = 1;

            for (int i = 1; i < colunas; i++) {
                if (A[linha_pivo][i] < 0) {
                    double razao = A[0][i] / A[linha_pivo][i];
                    if (razao < menor) {
                        coluna_pivo = i;
                        menor = razao;
                    }
                }
            }

            double elemento_pivo = A[linha_pivo][coluna_pivo];

            VB[linha_pivo - 1] = coluna_pivo;

            /*System.out.println("Linha pivo: " + linha_pivo);
            System.out.println("Coluna pivo: " + coluna_pivo);
            System.out.println("Elemento pivo: " + elemento_pivo);

            System.out.print("Variaveis da base: ");
            for (int i = 0; i < VB.length; i++) {
                System.out.print(VB[i] + " ");
            }
            System.out.println("");*/

            for (int j = 0; j < colunas; j++) {
                A[linha_pivo][j] = A[linha_pivo][j] / elemento_pivo;
            }

            double[] elem_coluna_pivo = new double[linhas];

            for (int i = 0; i < linhas; i++) {
                elem_coluna_pivo[i] = A[i][coluna_pivo];
            }

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    if (i != linha_pivo) {
                        A[i][j] = A[i][j] - elem_coluna_pivo[i] * A[linha_pivo][j];
                    }
                }
            }

            gravarArq.println("");

            // Impressão do quadro
            gravarArq.print("PIVOTEAMENTO DO DUAL ...\n");
            gravarArq.print("\t\t\t");
            for (int j = 1; j < colunas; j++) {
                gravarArq.print("X" + j + "\t\t");
            }

            gravarArq.println();

            for (int i = 0; i < linhas; i++) {
                if (i != 0) {
                    gravarArq.print("X" + VB[i - 1] + "\t");
                } else {
                    gravarArq.print("Z \t");
                }
                for (int j = 0; j < colunas; j++) {
                    gravarArq.printf("%.7f \t", A[i][j]);
                }
                gravarArq.println("");
            }

        }
        
        for (int i = 0; i < linhas; i++) {
            if (i != 0) {
                gravarArq.print("X" + VB[i - 1] + " = ");
                gravarArq.printf("%.7f ", A[i][0]);
            } else {
                if ("max".equals(tipo)) {
                    A[i][0] *= -1;
                }
                
                gravarArq.printf("\n\nSolução = %.7f \t", A[i][0]);
                gravarArq.printf("\nVariáveis da base: ");
            }

            gravarArq.println("");
        }
        
        arq.close();
        
    }
}
