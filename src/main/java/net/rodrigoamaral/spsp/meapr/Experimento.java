package net.rodrigoamaral.spsp.meapr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;


public class Experimento {

	String fileNameSolutionSet_;
	String fileNameTrueParetoFront_;
	int dimensions_;
	List<Point> points_ = null;

	private class Point {
		double[] vector_;

		public Point(int size) {
			vector_ = new double[size];
			for (int i = 0; i < size; i++)
				vector_[i] = 0.0f;
		}

	}

	public double[][] getSolutionSetObjMatrix(int indexPopulation) {
		points_ = new LinkedList<Point>();

		try {
			File archivo = new File(fileNameSolutionSet_);
			FileReader fr = null;
			BufferedReader br = null;
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			int contIndexPopulation = 0;
			// File reading
			String line;
			int lineCnt = 0;
			line = br.readLine(); // reading the first line (special case)

			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				try {
					Point auxPoint = new Point(dimensions_);
					for (int i = 0; i < dimensions_; i++) {
						String token = st.nextToken();
						auxPoint.vector_[i] = new Double(token);
					}

					points_.add(auxPoint);

					line = br.readLine();
					lineCnt++;
				} catch (NumberFormatException e) {
					System.err.println("Number in a wrong format in line " + lineCnt);
					System.err.println(line);
					line = br.readLine();
					lineCnt++;
				} catch (NoSuchElementException e2) {
					System.err.println("Line " + lineCnt + " does not have the right number of objectives");
					System.err.println(line);
					if (contIndexPopulation == indexPopulation) {
						br.close();
						break;
					} else {
						points_.clear();
					}
					contIndexPopulation++;
					line = br.readLine();
					lineCnt++;
				}
			}
			br.close();
		} catch (FileNotFoundException e3) {
			System.err.println("The file " + fileNameSolutionSet_ + " has not been found in your file system");
		} catch (IOException e3) {
			System.err.println("The file " + fileNameSolutionSet_ + " has not been found in your file system");
		}

		double[][] solutionSet = new double[points_.size()][dimensions_];
		for (int i = 0; i < points_.size(); i++) {
			Point auxPoint = points_.get(i);

			for (int j = 0; j < auxPoint.vector_.length; j++) {
				solutionSet[i][j] = auxPoint.vector_[j];
			}
		}

		return solutionSet;
	}

	public double[][] getTrueParetoFrontMatrix() {
		points_ = new LinkedList<Point>();

		try {
			File archivo = new File(fileNameTrueParetoFront_);
			FileReader fr = null;
			BufferedReader br = null;
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// File reading
			String line;
			int lineCnt = 0;
			line = br.readLine(); // reading the first line (special case)

			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				try {
					Point auxPoint = new Point(dimensions_);
					for (int i = 0; i < dimensions_; i++) {
						String token = st.nextToken();
						auxPoint.vector_[i] = new Double(token);
					}

					points_.add(auxPoint);

					line = br.readLine();
					lineCnt++;
				} catch (NumberFormatException e) {
					System.err.println("Number in a wrong format in line " + lineCnt);
					System.err.println(line);
					line = br.readLine();
					lineCnt++;
				} catch (NoSuchElementException e2) {
					System.err.println("Line " + lineCnt + " does not have the right number of objectives");
					System.err.println(line);
					line = br.readLine();
					lineCnt++;
				}
			}
			br.close();
		} catch (FileNotFoundException e3) {
			System.err.println("The file " + fileNameTrueParetoFront_ + " has not been found in your file system");
		} catch (IOException e3) {
			System.err.println("The file " + fileNameTrueParetoFront_ + " has not been found in your file system");
		}

		double[][] solutionSet = new double[points_.size()][dimensions_];
		for (int i = 0; i < points_.size(); i++) {
			Point auxPoint = points_.get(i);

			for (int j = 0; j < auxPoint.vector_.length; j++) {
				solutionSet[i][j] = auxPoint.vector_[j];
			}
		}

		return solutionSet;
	}

	/**
	 * @param nameSolutionSet:
	 *            nome do arquivo txt
	 * @param nameTrueParetoFront:
	 *            nome do arquivo txt da verdadeira fronteira de Pareto
	 * @param dimensions:
	 *            dimensões ex: 10 obj...
	 */
	public Experimento(String nameSolutionSet, String nameTrueParetoFront, int dimensions) {
		fileNameSolutionSet_ = nameSolutionSet;
		fileNameTrueParetoFront_ = nameTrueParetoFront;
		dimensions_ = dimensions;
	}

//	public double getGD(double[][] solutionSetObjMatrix, double[][] trueParetoFrontMatrix, int numberOfObjectives) {
//		return new GenerationalDistance().generationalDistance(solutionSetObjMatrix, trueParetoFrontMatrix,
//				numberOfObjectives);
//	} // getGD
//
//	public double getIGD(double[][] solutionSetObjMatrix, double[][] trueParetoFrontMatrix, int numberOfObjectives) {
//		return new InvertedGenerationalDistance().invertedGenerationalDistance(solutionSetObjMatrix,
//				trueParetoFrontMatrix, numberOfObjectives);
//	} // getIGD

    public double getGD(Front front, Front referenceFront) {
        return new GenerationalDistance().generationalDistance(front, referenceFront);
    } // getGD

    public double getIGD(Front front, Front referenceFront) {
        return new InvertedGenerationalDistance().invertedGenerationalDistance(front, referenceFront);
    } // getIGD

	public void writeGDAndIGD(double gd, double igd, int index) {
		/*
		 * try { Open the file FileOutputStream fos = new
		 * FileOutputStream(fileNameSolutionSet_ + ".pf"); OutputStreamWriter
		 * osw = new OutputStreamWriter(fos); BufferedWriter bw = new
		 * BufferedWriter(osw);
		 * 
		 * String aux = "";
		 * 
		 * aux = "GD:   " + gd;
		 * 
		 * bw.append(aux);
		 * 
		 * bw.write(aux); bw.newLine();
		 * 
		 * aux = "IGD:  " + igd; bw.write(aux); bw.newLine();
		 * 
		 * Close the file bw.close(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		try (FileWriter fw = new FileWriter(fileNameSolutionSet_ + ".pf", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("");
			if (index == 0) {
				out.println("GD e IGD de todas execuções logo acima");
				out.println("");
			}
			String aux = "";
			aux = "GD:   " + gd;
			out.println(aux);
			aux = "IGD:  " + igd;
			out.println(aux);

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fw = new FileWriter(fileNameSolutionSet_ + ".gd", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			out.print(gd+" ");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fw = new FileWriter(fileNameSolutionSet_ + ".igd", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			out.print(igd+" ");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// Experimento experimento = new
		// Experimento("C:\\Users\\treinamento\\Desktop\\meapr\\teste.txt",
		// "C:\\Users\\treinamento\\Desktop\\meapr\\smpso.txt", 5);

		/*
		 * Experimento experimento = new Experimento(
		 * "C:\\Users\\Lucas\\Dropbox\\Proposta de mestrado\\construção do algoritmo MEAPR\\Comparação entre SMPSO e mMEAPR\\testes com mMEAPR\\resultados - artigo para o ENIAC\\meapr\\Broadcast\\java -Xms128m -Xmx2024m -jar meapr.jar 4 3 10 30 167 100 50 Broadcast 8 1\\3 obj DTLZ2Convexo"
		 * ,
		 * "C:\\Users\\Lucas\\Dropbox\\Proposta de mestrado\\construção do algoritmo MEAPR\\Comparação entre SMPSO e mMEAPR\\testes com mMEAPR\\resultados - artigo para o ENIAC\\pareto front - DTLZ\\CDTLZ2_3_pareto.txt"
		 * , 3);
		 */

		String fileNameTrueParetoFront_ = args[0]; //"C:\\Users\\Lucas\\Desktop\\meapr\\configuracao_gd_igd.txt";
		args = new String[3]; // para tds
		// args = new String[12]; // para wfg1
		try {
			File archivo = new File(fileNameTrueParetoFront_);
			FileReader fr = null;
			BufferedReader br = null;
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// File reading
			String line;
			int lineCnt = 0;
			line = br.readLine(); // reading the first line (special case)

			while (line != null) {				
				StringTokenizer st = new StringTokenizer(line,",");
				try {

					for (int i = 0; i < args.length; i++) {
						String token = st.nextToken().trim();
						args[i] = token;
					}

					// conteudo
					Experimento experimento = new Experimento(args[0], args[1], Integer.parseInt(args[2]));
					
					
					 /* Experimento experimento = new Experimento(
					  "C:\\Users\\treinamento\\Dropbox\\Proposta de mestrado\\construção do algoritmo MEAPR\\Comparação entre SMPSO e mMEAPR\\testes com mMEAPR\\resultados - artigo para o ENIAC\\meapr\\Broadcast\\java -Xms128m -Xmx2024m -jar meapr.jar 4 3 10 30 167 100 50 Broadcast 8 1\\3 obj DTLZ2Convexo"
					  ,
					  "C:\\Users\\treinamento\\Dropbox\\Proposta de mestrado\\construção do algoritmo MEAPR\\Comparação entre SMPSO e mMEAPR\\testes com mMEAPR\\resultados - artigo para o ENIAC\\pareto front - DTLZ\\CDTLZ2_3_pareto.txt"
					  , 3);*/
					 

//					for (int i = 0; i < 10; i++) {
//						double gd = experimento.getGD(experimento.getSolutionSetObjMatrix(i),
//								experimento.getTrueParetoFrontMatrix(), Integer.parseInt(args[2]));
//						double igd = experimento.getIGD(experimento.getSolutionSetObjMatrix(i),
//								experimento.getTrueParetoFrontMatrix(), Integer.parseInt(args[2]));
//						experimento.writeGDAndIGD(gd, igd, i);
//					}

                    for (int i = 0; i < 10; i++) {
                        double[][] solutionSet = experimento.getSolutionSetObjMatrix(i);
                        double[][] trueParetoFront = experimento.getTrueParetoFrontMatrix();
                        List<Solution> solutionList = experimento.getSolutionList(i);
                        Front front = new ArrayFront(); // <-- solutionList (?)
                        Front referenceFront = new ArrayFront();
                        double gd = experimento.getGD(front, referenceFront);
                        double igd = experimento.getIGD(front, referenceFront);
                        experimento.writeGDAndIGD(gd, igd, i);
                    }

					line = br.readLine();
					lineCnt++;
				} catch (NumberFormatException e) {
					System.err.println("Number in a wrong format in line " + lineCnt);
					System.err.println(line);
					line = br.readLine();
					lineCnt++;
				} catch (NoSuchElementException e2) {
					System.err.println("Line " + lineCnt + " does not have the right number of objectives");
					System.err.println(line);
					line = br.readLine();
					lineCnt++;
				}
			}
			br.close();
		} catch (FileNotFoundException e3) {
			System.err.println("The file " + fileNameTrueParetoFront_ + " has not been found in your file system");
		} catch (IOException e3) {
			System.err.println("The file " + fileNameTrueParetoFront_ + " has not been found in your file system");
		}

		/*
		 * double gd = experimento.getGD(experimento.getSolutionSetObjMatrix(),
		 * experimento.getTrueParetoFrontMatrix(), Integer.parseInt(args[2]));
		 * System.out.println("GD:  "+ gd);
		 * 
		 * double igd =
		 * experimento.getIGD(experimento.getSolutionSetObjMatrix(),
		 * experimento.getTrueParetoFrontMatrix(), Integer.parseInt(args[2]));
		 * System.out.println("IGD: "+ igd);
		 * 
		 * experimento.writeGDAndIGD(gd, igd);
		 */
	}

    private List<Solution> getSolutionList(int i) {
//        double[][] solutionSet = getSolutionSetObjMatrix(i);
//        DedicationMatrix dm =
        return null;
    }

}
