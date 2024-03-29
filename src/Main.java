import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

//Target Control
public final class Main {

	@SuppressWarnings({ "unchecked", "unused" })
	public static void main(String[] args) {

		try {
			Scanner scanner = new Scanner(System.in);

		    System.out.println("Please enter DATA folder path.");
		    String dirName = scanner.next();

		    if (!dirName.endsWith("DATA")) {
		    	System.out.println("folder is must be ended with \"DATA\"");
		    	System.exit(0);
		    }

			Path dataDir = Paths.get(dirName);
			Path resultParDir = dataDir.getParent().resolve("NEW_RESULT");

			if (Files.notExists(resultParDir)) {
				Files.createDirectory(resultParDir);
			}

			List<Path> folders = Files.list(dataDir).collect(Collectors.toList());

			for (Path folder : folders) {
				Path net = Files.list(folder.resolve("net")).collect(Collectors.toList()).get(0);

				List<Path> modules = Files.list(folder.resolve("target")).collect(Collectors.toList());

				for (Path module : modules) {

					Network nw = new Network(net, module);

					String fileName = net.getFileName().toString().split(".txt")[0];
					String moduleName = module.getFileName().toString().split(".txt")[0];

					nw.name = fileName+"__"+moduleName;
					Path resultDir = resultParDir.resolve(nw.name);

					System.out.println(nw.name);

					System.out.println("######################");

					if (Files.notExists(resultDir)) {
						Files.createDirectory(resultDir);
					}

					TimeUtil t1 = new TimeUtil();
					TimeUtil t2 = new TimeUtil();

					IloCplex cplex = new IloCplex();
					cplex.setOut(null);
					cplex.setParam(IloCplex.Param.MIP.Display, 0);
	//				cplex.setParam(IloCplex.Param.Read.DataCheck, IloCplex.DataCheck.Warn);
	//				cplex.setParam(IloCplex.Param.MIP.Limits.Solutions, 10);
	//				cplex.setParam(IloCplex.IntParam.RootAlg,IloCplex.Algorithm.Auto);
	//				cplex.setParam(IloCplex.MIPStartEffort.NoCheck);

					IloIntVar[] y = cplex.boolVarArray(nw.N);

					ArrayList<Integer> controlled = MM(cplex, nw, y, t1, t2);

					t1.end();

					System.out.println("\nTotal: "+t1.getTimeSec()+" Alg: "+t2.getTimeSec()+"\n");

					writeAboutCategory(nw, resultDir, controlled);
					writeAboutNetwork(nw, t1.getTimeSec(), resultDir, controlled, t2.getTimeSec());
					
					y = null;
					cplex = null;
					nw.setNull();
					nw = null;
				}
			}

			System.out.println("\nAll Done");
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Integer> MM(IloCplex cplex, Network nw, IloIntVar[]y, TimeUtil t1, TimeUtil t2) {
		t1.check();

		Alg.buildILP(cplex, nw, y);

		ArrayList<Integer> controlled = Alg.getMM(cplex, y, t2);

		t1.check();
		return controlled;
	}

	public static void writeAboutCategory(Network nw, Path resultDir, ArrayList<Integer> controlledIx) {
		PrintWriter pr = null;
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();

		columns.append("Node Name,Category");

		ArrayList<String> driver = new ArrayList<>();
		ArrayList<Integer> driverIx = new ArrayList<>();
		ArrayList<String> controlled = new ArrayList<>();

		ArrayList<Integer> nodeList = null;
		ArrayList<String> ccList = null;
		String cc = null;
		
		for (int i=0; i<nw.N; i++) {
			if (!controlledIx.contains(i)) {
				driverIx.add(i);
			}
		}

		for (int i=0; i<3; i++) {
			switch (i) {
				case 0:
					ccList = nw.targetList;
					cc = "Target";
					break;
				case 1:
					nodeList =driverIx;
					ccList = driver;
					cc = "Driver";
					break;
				case 2:
					nodeList = controlledIx;
					ccList = controlled;
					cc = "_";
					break;
			}

			if (i!=0) {
				for (int j : nodeList) {
					ccList.add(nw.nodeList.get(j));
				}
			}

			ccList.sort(Comparator.comparing(String::length).thenComparing(String::compareTo));

			if (i!=0) {
				for (String name : ccList) {
					values.append(name).append(",").append(cc).append("\n");
				}
			}

			try {
				Path ccFile = resultDir.resolve(cc+".txt");
				if (Files.notExists(ccFile)) {
					Files.createFile(ccFile);
				}
				pr = new PrintWriter(
						new BufferedWriter(
							new FileWriter(ccFile.toFile())));
				for (int j=0; j<ccList.size(); j++) {
					pr.println(ccList.get(j));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pr != null) {
					try {
						pr.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("WARNING : FILE CAN NOT BE CLOSED");
					}
				}
			}
		}

		try {
			Path ccFile = resultDir.resolve("control_category.csv");
			if (Files.notExists(ccFile)) {
				Files.createFile(ccFile);
			}
			pr = new PrintWriter(
					new BufferedWriter(
							new FileWriter(ccFile.toFile())));
			pr.println(columns);
			pr.println(values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pr != null) {
				try {
					pr.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("WARNING : FILE CAN NOT BE CLOSED");
				}
			}
		}
	}

	public static void writeAboutNetwork(Network nw, double t, Path resultDir, ArrayList<Integer> controlled, double t2) {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();

		columns.append("Name,N,E");
		values.append(nw.name).append(",");
		values.append(nw.N).append(",");
		values.append(nw.E).append("\n");

		PrintWriter pr = null;

		try {
			Path configFile = resultDir.resolve("net_config.csv");
			if (Files.notExists(configFile)) {
				Files.createFile(configFile);
			}
			pr = new PrintWriter(
					new BufferedWriter(
						new FileWriter(configFile.toFile())));
			pr.println(columns);
			pr.println(values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pr != null) {
				try {
					pr.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("WARNING : FILE CAN NOT BE CLOSED");
				}
			}
		}

		columns = new StringBuilder();
		values = new StringBuilder();

		columns.append("Time,ALG_TIME,Driver,Target");
		values.append(t).append(",");
		values.append(t2).append(",");
		values.append(nw.N-controlled.size()).append(",");
		values.append(nw.targetList.size()).append("\n");

		try {
			Path configFile = resultDir.resolve("net_result.csv");
			if (Files.notExists(configFile)) {
				Files.createFile(configFile);
			}
			pr = new PrintWriter(
					new BufferedWriter(
						new FileWriter(configFile.toFile())));
			pr.println(columns);
			pr.println(values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pr != null) {
				try {
					pr.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("WARNING : FILE CAN NOT BE CLOSED");
				}
			}
		}
	}
}
