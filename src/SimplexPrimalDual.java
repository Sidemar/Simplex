


import java.util.Scanner;
//import org.apache.commons.math3.fraction.Fraction;
//import org.apache.commons.math3.fraction.FractionFormat;


public class SimplexPrimalDual {

    
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        
        int linhas = entrada.nextInt();
        int colunas = entrada.nextInt();
        String tipo = entrada.next();
        
        System.out.println(linhas);
        System.out.println(colunas);
        System.out.println(tipo);
        
        double[][] A = new double[linhas][colunas];

        A[0][0] = 0;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if(i == 0 && j == 0) {
                    j = 1;
                    
                }
                    String valor = entrada.next();
                    A[i][j] = Double.parseDouble(valor);
            }
        }
        
        if("min".equals(tipo)) {
            for (int j = 0; j < colunas; j++) {
                A[0][j] = -1*A[0][j];
            }
        }
        
        
        //FractionFormat form = new FractionFormat();
        for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    System.out.print(A[i][j] + "\t");
                }
                System.out.println("");
            }
        
        

        int[] VB = new int[linhas - 1];
        int v_folga = 0;
        
        
        for (int j = 1; j < colunas; j++) {
            if(A[0][j] == 0) {
                v_folga = j;
                break;
            }
        }

        for (int i = 0; i < linhas - 1; i++) {
            VB[i] = v_folga + i;
        }

        boolean solucao_otima = false;
        boolean solucaoInviavel = true;
        boolean solucaoIlimitada = true;
        
        //FractionFormat form = new FractionFormat();

        while (!solucao_otima) {
            
            solucao_otima = true;
            solucaoInviavel = true;
            solucaoIlimitada = true;
            
            for (int j = 1; j < colunas; j++) {
                if (A[0][j] > 0) {
                    solucao_otima = false;
                }
            }
            
            if(solucao_otima) {
                solucaoInviavel = false;
                solucaoIlimitada = false;
                break;
            }
            
            for (int i = 1; i < linhas; i++) {
                if(A[i][0] >= 0) 
                    solucaoInviavel = false;
            }
            
            if(solucaoInviavel) {
                System.out.println("Solução Inviavel no primal");
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
                if(A[i][coluna_pivo] > 0) {
                    solucaoIlimitada = false;
                    break;
                }
            }
            
            if(solucaoIlimitada) {
                System.out.println("Solução Ilimitada");
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

            System.out.println("Linha pivo: " + linha_pivo);
            System.out.println("Coluna pivo: " + coluna_pivo);
            System.out.println("Elemento pivo: " + elemento_pivo);

            System.out.print("Variaveis da base: ");
            for (int i = 0; i < VB.length; i++) {
                System.out.print(VB[i] + " ");
            }
            System.out.println("");

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
            
            System.out.println("");

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    System.out.printf("%.6f \t", A[i][j]);
                }
                System.out.println("");
            }

        }
        
        solucao_otima = false;
        System.out.println(solucaoInviavel);
        System.out.println(solucaoIlimitada);
        while (!solucao_otima && !solucaoInviavel && !solucaoIlimitada) {

            solucao_otima = true;
            //solucaoInviavel = true;
            solucaoIlimitada = true;
            
            for (int j = 1; j < linhas; j++) {
                if (A[j][0] < 0) {
                    solucao_otima = false;
                }
            }
            
            if(solucao_otima) {
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
                if(A[linha_pivo][i] < 0) {
                    solucaoIlimitada = false;
                    break;
                }
            }
            
            if(solucaoIlimitada) {
                System.out.println("Solução Ilimitada");
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

            System.out.println("Linha pivo: " + linha_pivo);
            System.out.println("Coluna pivo: " + coluna_pivo);
            System.out.println("Elemento pivo: " + elemento_pivo);

            System.out.print("Variaveis da base: ");
            for (int i = 0; i < VB.length; i++) {
                System.out.print(VB[i] + " ");
            }
            System.out.println("");

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
            
            System.out.println("");

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    System.out.printf("%.6f \t", A[i][j]);
                }
                System.out.println("");
            }

        }
    }
}
